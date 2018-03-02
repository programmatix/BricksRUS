package com.bricksrus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderServiceTest {

    @TestConfiguration
    static class OrderServiceTestContextConfiguration {

        @Bean
        public OrderService orderService() {
            return new OrderService();
        }
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderReferenceRepository orderReferenceRepository;

    @Test
    public void createSimpleOrder() {
        CreateOrderRequest req = new CreateOrderRequest(1000);
        OrderReference ref = orderService.createOrder(req);

        assertEquals(ref.getNumBricks(), 1000);

        assertEquals(orderReferenceRepository.count(), 1);
        assertEquals(orderReferenceRepository.findOne(ref.getId()).getNumBricks(), 1000);
    }

    private OrderReference createOrder(int numBricks) {
        CreateOrderRequest req = new CreateOrderRequest(1000);
        return orderService.createOrder(req);
    }

    @Test
    public void getOrder() {
        OrderReference ref = createOrder(10000);
        OrderReference fetched = orderService.findById(ref.getId());

        assertEquals(fetched.getId(), ref.getId());
        assertEquals(fetched.getNumBricks(), ref.getNumBricks());
    }
}
