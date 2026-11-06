

package com.dipesh.inventoryservice.Repository;

import com.dipesh.inventoryservice.entity.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedOrderRepository
        extends JpaRepository<ProcessedOrder, Long> {
}
