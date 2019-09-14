/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.enjoy.james.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import com.enjoy.james.dao.OrderDao;
import com.enjoy.james.model.Order;
import com.enjoy.james.service.OrderService;
import com.enjoy.james.service.TransService;
import com.enjoy.james.vo.OrderVO;


/**
* service层：此处有调用远程系统的动作，如何保证数据的一致性
* @author 【享学课堂】 James老师QQ：1076258117 架构技术QQ群：684504192  
* @throws Exception
*/
@Service
public class OrderServiceImpl implements OrderService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private final String url = "http://127.0.0.1:9080/trans/sendOrder?orderid=";
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private TransService transService;

	@Override
	public Order queryOrderByid(String orderid) {
		// TODO Auto-generated method stub
		return orderDao.queryOrderByid(orderid);

	}

	@Transactional
	public String update(Order order) {
		String flag = "－1";
		String orderid = order.getOrderid();
		try {
			logger.info("order =======================", order);
			OrderVO orderVo = new OrderVO();
			BeanUtils.copyProperties(order, orderVo);
			flag = transService.trans(url+orderid);

			order = new Order();
			order.setOrderid(orderid);
			if (flag == "0") {
				order.setOrderstatus("0");
			} else {
				order.setOrderstatus("1");
			}
			orderDao.update(order);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("异常－－－－－－", e);
		}

		return flag;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 事务中包括远程调用，本地连接占用的时间会因为远程调用的时间而不可控，被消耗干净 导致其它乃至数据库的服务不可用
	 * 
	 * 连接解决后，被调用的服务可能会出现失败，需要引入出款请求模型
	 * 
	 * 使用TransactionTemplate，改善连接
	 */
	@Autowired
	private TransactionTemplate template;

	// 使用propagation 指定事务的传播行为，即当前的事务方法被另外一个事务方法调用时如何使用事务。
	// NEVER表示在方法里没有事务，每一段的template.execute存在事务，调远程接口时没有事务，不占用连接
	@Transactional(propagation = Propagation.NEVER)
	public String updateByTemplate(Order order) {
		String orderid = order.getOrderid();
		logger.info("order =======================", order);
		// spring的编程式事务, 这是编程式事务的写法出现了很大的变形
		template.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				Order orderProcess = new Order();
				orderProcess.setOrderid(orderid);
				orderProcess.setOrderstatus("4");// 订单处理中的状态
				orderDao.update(orderProcess);
				return null;
			}
		});

		OrderVO orderVo = new OrderVO();
		BeanUtils.copyProperties(order, orderVo);
		String flag = transService.trans(url+orderid);

		template.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				Order orderFinished = new Order();
				orderFinished.setOrderid(orderid);
				if (null != flag && flag == "0") {
					orderFinished.setOrderstatus("0");// 失败
				} else {
					orderFinished.setOrderstatus("2");// 成功
				}
				orderDao.update(orderFinished);
				return null;
			}
		});

		return "0";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 悲观锁
	 * select * from xxx where id = xxx for update
	 * 
	 * 乐观锁
	 * select version from xxx where id = xxx
	 * update xxx set xxx = xxx where id = xxx and version = 'version'
	 * 
	 * 基于状态机的乐观锁
	 * int i = update xxx set status = 4 where id=xxx and version =1
	 * i==1
	 */
	@Override
	public String updateByTemplateThread(Order order) {
		String orderid = order.getOrderid();
		logger.info("order =======================", order);
		// spring的编程式事务, 这是编程式事务的写法出现了很大的变形
		Boolean lockStatus = template
				.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						Order orderProcess = new Order();
						orderProcess.setOrderid(orderid);
						orderProcess.setOrderstatus("4");// 订单处理中的状态
						orderProcess.setVersion(1);
						return 1 == orderDao.updateByVersion(orderProcess);
					}
				});
        logger.info("-----------------lockStatus====="+lockStatus);
		if (lockStatus) {
			OrderVO orderVo = new OrderVO();
			BeanUtils.copyProperties(order, orderVo);
			String flag = transService.trans(url+orderid);

			template.execute(new TransactionCallback<Object>() {
				@Override
				public Object doInTransaction(TransactionStatus status) {
					Order orderFinished = new Order();
					orderFinished.setOrderid(orderid);
					orderFinished.setVersion(1);
					if (null != flag && flag == "0") {
						orderFinished.setOrderstatus("0");// 失败
					} else {
						orderFinished.setOrderstatus("2");// 成功
					}
					orderDao.updateByVersion(orderFinished);
					return null;
				}
			});
		} else {
			logger.error("========lockfail============" + order.getOrderid());
		}
		return "0";
	}

}
