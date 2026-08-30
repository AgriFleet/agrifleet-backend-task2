package task2_intelligent_resource_allocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task2_intelligent_resource_allocation.entity.BookingEntity;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByBookingStatus(String status);
}