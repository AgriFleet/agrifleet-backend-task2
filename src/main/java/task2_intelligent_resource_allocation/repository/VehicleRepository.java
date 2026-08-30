package task2_intelligent_resource_allocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task2_intelligent_resource_allocation.entity.VehicleEntity;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findByAvailabilityStatus(String status);
}