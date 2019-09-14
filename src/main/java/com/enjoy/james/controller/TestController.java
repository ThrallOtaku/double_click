package com.enjoy.james.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoy.james.model.Order;
import com.enjoy.james.service.OrderService;

/**
* 控制层：对外开放接口地址及路径   http://127.0.0.1:9090/order/query?orderid=1
* @author 【享学课堂】 James老师QQ：1076258117 架构技术QQ群：684504192  
* @throws Exception
*/
@Controller
@RequestMapping("/order")
public class TestController {

    @Autowired
    private OrderService orderService;


    @RequestMapping(value = "/query")
    @ResponseBody
    public Order query(String orderid) {
    	Order order = orderService.queryOrderByid(orderid);
        return order;
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String update(String orderid) {
       Order order = orderService.queryOrderByid(orderid);
       return orderService.update(order);
    }
    
    @RequestMapping(value = "/updateByTemplate")
    @ResponseBody
    public String updateByTemplate(String orderid) {
       Order order = orderService.queryOrderByid(orderid);
       return orderService.updateByTemplate(order);
    }

	@RequestMapping(value = "/updateByTemplateThread")
	@ResponseBody
	public String updateByTemplateThread(String orderid) {
		Order order = orderService.queryOrderByid(orderid);
		for (int i = 0; i < 6; i++) {
			Thread t = new Thread(new ExecuteThread(order));
			t.start();
		}
		return "OK";
	}

	private class ExecuteThread implements Runnable {
		private Order order;

		public ExecuteThread(Order order) {
			this.order = order;
		}

		public void run() {
			orderService.updateByTemplate(order);
		}
	}

}
