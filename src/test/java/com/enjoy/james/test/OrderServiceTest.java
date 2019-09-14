package com.enjoy.james.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.enjoy.james.ClientApplication;
import com.enjoy.james.service.OrderService;



/**
 * @author liuzh
 * @since 2017/9/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ClientApplication.class)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void test() {
        String orderid = "1";
        
    }
}
