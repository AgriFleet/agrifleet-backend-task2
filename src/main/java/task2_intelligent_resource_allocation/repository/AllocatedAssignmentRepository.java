package task2_intelligent_resource_allocation.repository;

import task2_intelligent_resource_allocation.entity.AllocatedAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocatedAssignmentRepository extends JpaRepository<AllocatedAssignmentEntity, Long> {
    List<AllocatedAssignmentEntity> findByBatchId(Long batchId);
}