package task2_intelligent_resource_allocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task2_intelligent_resource_allocation.entity.AllocationBatchEntity;

@Repository
public interface AllocationBatchRepository extends JpaRepository<AllocationBatchEntity, Long> {
}