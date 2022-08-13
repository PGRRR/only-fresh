package com.devkurly.cart.mapper;

import com.devkurly.cart.domain.Cart;
import com.devkurly.mapper.CartMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class CartMapperTest {

    @Autowired
    com.devkurly.mapper.CartMapper CartMapper;

    @Test
    public void xmlAppContext() {
        ApplicationContext ac = new GenericXmlApplicationContext("1root-context.xml");
        CartMapper mapper = ac.getBean("CartMapper", CartMapper.class);
        org.springframework.util.Assert.isInstanceOf(CartMapper.class, mapper);
    }

    @Test
    public void insert() {
        // given
        Cart cart = new Cart();
        cart.setUser_id(1);
        cart.setPdt_id(1);
        cart.setPdt_qty(20);

        // when
        CartMapper.insert(cart);

        // then
        Assert.assertSame(1,cart.getUser_id());
    }

    @Test
    public void findById() {
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }
}