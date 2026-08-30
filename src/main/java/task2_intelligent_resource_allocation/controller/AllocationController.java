package task2_intelligent_resource_allocation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task2_intelligent_resource_allocation.dto.AllocationResponseDTO;
import task2_intelligent_resource_allocation.entity.AllocatedAssignmentEntity;
import task2_intelligent_resource_allocation.entity.AllocationBatchEntity;
import task2_intelligent_resource_allocation.entity.BookingEntity;
import task2_intelligent_resource_allocation.entity.VehicleEntity;
import task2_intelligent_resource_allocation.repository.AllocatedAssignmentRepository;
import task2_intelligent_resource_allocation.repository.AllocationBatchRepository;
import task2_intelligent_resource_allocation.repository.BookingRepository;
import task2_intelligent_resource_allocation.repository.VehicleRepository;
import task2_intelligent_resource_allocation.service.IntelligentResourceAllocationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/intelligent-resource-allocation")
public class AllocationController {

    private final IntelligentResourceAllocationService allocationService;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final AllocationBatchRepository batchRepository;
    private final AllocatedAssignmentRepository assignmentRepository;

    public AllocationController(IntelligentResourceAllocationService allocationService,
                                VehicleRepository vehicleRepository,
                                BookingRepository bookingRepository,
                                AllocationBatchRepository batchRepository,
                                AllocatedAssignmentRepository assignmentRepository) {
        this.allocationService = allocationService;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.batchRepository = batchRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // ==========================================
    // 1. CORE ALGORITHM ENDPOINTS (POST)
    // ==========================================

    @PostMapping("/batch-match")
    public ResponseEntity<AllocationResponseDTO> runBatchAllocation() {
        AllocationResponseDTO response = allocationService.executeBatchAllocation();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/instant-dispatch")
    public ResponseEntity<AllocationResponseDTO> runInstantDispatch() {
        AllocationResponseDTO response = allocationService.executeInstantAllocation();
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 2. ALLOCATION HISTORY ENDPOINTS (GET)
    // ==========================================

    @GetMapping("/batches")
    public ResponseEntity<List<AllocationBatchEntity>> getAllBatches() {
        return ResponseEntity.ok(batchRepository.findAll());
    }

    @GetMapping("/batches/{batchId}")
    public ResponseEntity<AllocationBatchEntity> getBatchById(@PathVariable Long batchId) {
        Optional<AllocationBatchEntity> batch = batchRepository.findById(batchId);
        return batch.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AllocatedAssignmentEntity>> getAllAssignments() {
        return ResponseEntity.ok(assignmentRepository.findAll());
    }

    // ==========================================
    // 3. VEHICLE MANAGEMENT ENDPOINTS
    // ==========================================

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleEntity>> getAllVehicles() {
        return ResponseEntity.ok(vehicleRepository.findAll());
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleEntity> getVehicleById(@PathVariable Long id) {
        Optional<VehicleEntity> vehicle = vehicleRepository.findById(id);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleEntity> createVehicle(@RequestBody VehicleEntity vehicle) {
        VehicleEntity savedVehicle = vehicleRepository.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
    }

    // ==========================================
    // 4. BOOKING MANAGEMENT ENDPOINTS
    // ==========================================

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingEntity>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingEntity> getBookingById(@PathVariable Long id) {
        Optional<BookingEntity> booking = bookingRepository.findById(id);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingEntity> createBooking(@RequestBody BookingEntity booking) {
        BookingEntity savedBooking = bookingRepository.save(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }
}