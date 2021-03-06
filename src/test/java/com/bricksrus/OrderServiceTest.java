package com.bricksrus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

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
    public void getOrder_Exists_ShouldSucceed() {
        OrderReference ref = createOrder(10000);
        Optional<OrderReference> fetched = orderService.findById(ref.getId());

        assertTrue(fetched.isPresent());
        assertEquals(fetched.get().getId(), ref.getId());
        assertEquals(fetched.get().getNumBricks(), ref.getNumBricks());
    }

    @Test
    public void getOrder_DoesNotExist_ShouldFail() {
        Optional<OrderReference> fetched = orderService.findById(1);
        assertFalse(fetched.isPresent());
    }

    @Test
    public void findAll_NoOrders_ShouldBeEmpty() {
        List<OrderReference> refs = orderService.findAll();
        assertEquals(refs.size(), 0);
    }

    @Test
    public void findAll_OrdersPresent_ShouldExistAndBeSorted() {
        createOrder(100);
        createOrder(500);
        List<OrderReference> refs = orderService.findAll();
        assertEquals(refs.size(), 2);
        assertTrue(refs.get(0).getId() < refs.get(1).getId());
    }

    @Test
    public void updateOrder_OrderNotPresent_ShouldFail() {
        OrderReference ref = createOrder(100);
        assertNotEquals(10000, ref.getId());
        Optional<OrderReference> out = orderService.updateOrder(10000, new UpdateOrderRequest(200));

        assertFalse(out.isPresent());
    }

    @Test
    public void updateOrder_OrderPresent_ShouldSucceed() {
        OrderReference ref = createOrder(100);
        Optional<OrderReference> out = orderService.updateOrder(ref.getId(), new UpdateOrderRequest(200));

        assertTrue(out.isPresent());
        assertEquals(ref.getId(), out.get().getId());
        assertEquals(200, out.get().getNumBricks());

        assertEquals(1, orderReferenceRepository.count());

        // Check it's persisted correctly too
        OrderReference first = orderReferenceRepository.findAll().iterator().next();
        assertEquals(first.getId(), ref.getId());
        assertEquals(first.getNumBricks(), 200);
    }
}
