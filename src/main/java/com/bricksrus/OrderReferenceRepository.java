package com.bricksrus;

import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface OrderReferenceRepository extends CrudRepository<OrderReference, Integer> {
}
