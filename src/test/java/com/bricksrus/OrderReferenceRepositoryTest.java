package com.bricksrus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderReferenceRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderReferenceRepository orderReferenceRepository;

    @Test
    public void createOrderReference() {
        OrderReference ref = new OrderReference(10);
        entityManager.persist(ref);
        entityManager.flush();

        assertEquals(orderReferenceRepository.count(), 1);

        OrderReference found = orderReferenceRepository.findOne(ref.getId());
        assertEquals(found.getNumBricks(), 10);
    }

    @Test
    public void createTwoOrderReferences() {
        OrderReference ref1 = new OrderReference(10);
        OrderReference ref2 = new OrderReference(100);
        entityManager.persist(ref1);
        entityManager.persist(ref2);
        entityManager.flush();

        assertEquals(orderReferenceRepository.count(), 2);

        OrderReference found1 = orderReferenceRepository.findOne(ref1.getId());
        assertEquals(found1.getNumBricks(), 10);

        OrderReference found2 = orderReferenceRepository.findOne(ref2.getId());
        assertEquals(found2.getNumBricks(), 100);
    }
}
