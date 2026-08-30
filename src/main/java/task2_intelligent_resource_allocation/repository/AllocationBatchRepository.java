package task2_intelligent_resource_allocation.repository;

import task2_intelligent_resource_allocation.entity.AllocationBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationBatchRepository extends JpaRepository<AllocationBatchEntity, Long> {
    List<AllocationBatchEntity> findAllByOrderByBatchIdDesc();
}