package com.enjoy.james.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.enjoy.james.service.TransService;


/**
* 远程调用接口工具
* @author 【享学课堂】 James老师QQ：1076258117 架构技术QQ群：684504192  
* @throws Exception
*/
@Service
public class TransServiceImpl implements TransService {

    @Autowired
    private RestTemplate restTemplate ;
	@Override
	public String trans(String url) {
		return restTemplate.getForEntity(url, String.class).getBody();
	}
	

}
