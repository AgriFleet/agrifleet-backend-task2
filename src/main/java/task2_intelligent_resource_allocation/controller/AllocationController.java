package task2_intelligent_resource_allocation.controller;

import task2_intelligent_resource_allocation.entity.AllocatedAssignmentEntity;
import task2_intelligent_resource_allocation.entity.AllocationBatchEntity;
import task2_intelligent_resource_allocation.service.AllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/allocation")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping("/batch/hungarian")
    public ResponseEntity<AllocationBatchEntity> runScheduledBatch() {
        return ResponseEntity.ok(allocationService.executeHungarianBatch());
    }

    @PostMapping("/realtime/greedy")
    public ResponseEntity<AllocationBatchEntity> runRealtimeGreedy(@RequestParam Long bookingId) {
        return ResponseEntity.ok(allocationService.executeGreedyRealtime(bookingId));
    }

    @GetMapping("/batches")
    public ResponseEntity<List<AllocationBatchEntity>> getAllBatches() {
        return ResponseEntity.ok(allocationService.getAllBatches());
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AllocatedAssignmentEntity>> getAssignments(@RequestParam Long batchId) {
        return ResponseEntity.ok(allocationService.getAssignmentsByBatchId(batchId));
    }
    @PutMapping("/assignments/{id}/confirm")
    public ResponseEntity<Void> confirmAssignment(@PathVariable Long id) {
        allocationService.confirmAssignment(id);
        return ResponseEntity.ok().build();
    }
}