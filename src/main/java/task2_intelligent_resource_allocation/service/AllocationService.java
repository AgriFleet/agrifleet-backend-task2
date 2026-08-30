package task2_intelligent_resource_allocation.service;

import task2_intelligent_resource_allocation.algorithm.GreedyAllocator;
import task2_intelligent_resource_allocation.algorithm.HungarianAlgorithm;
import task2_intelligent_resource_allocation.entity.AllocatedAssignmentEntity;
import task2_intelligent_resource_allocation.entity.AllocationBatchEntity;
import task2_intelligent_resource_allocation.repository.AllocatedAssignmentRepository;
import task2_intelligent_resource_allocation.repository.AllocationBatchRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AllocationService {

    private final AllocationBatchRepository batchRepository;
    private final AllocatedAssignmentRepository assignmentRepository;

    public AllocationService(AllocationBatchRepository batchRepository, AllocatedAssignmentRepository assignmentRepository) {
        this.batchRepository = batchRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public AllocationBatchEntity executeHungarianBatch() {
        long startTime = System.nanoTime();

        long[] vehicleIds = {1, 2, 3, 8};
        long[] bookingIds = {1, 2, 3, 4};

        double[][] costMatrix = {
                {5.85, 7.60, 6.20, 9.40},
                {4.90, 4.20, 8.10, 8.50},
                {9.10, 11.20, 5.80, 4.30},
                {3.40, 6.10, 12.00, 7.80}
        };

        int[] assignments = HungarianAlgorithm.findOptimalAssignments(costMatrix);

        long endTime = System.nanoTime();
        double execTimeMs = (endTime - startTime) / 1_000_000.0;

        double totalCost = 0.0;
        for (int i = 0; i < assignments.length; i++) {
            totalCost += costMatrix[i][assignments[i]];
        }

        AllocationBatchEntity batch = new AllocationBatchEntity();
        batch.setBatchType("SCHEDULED_BATCH");
        batch.setMatrixDimensions(vehicleIds.length + "x" + bookingIds.length);
        batch.setCostMatrixPayload(Arrays.deepToString(costMatrix));
        batch.setCandidateVehicleIds(Arrays.toString(vehicleIds));
        batch.setCandidateBookingIds(Arrays.toString(bookingIds));
        batch.setTotalNetworkCost(totalCost);
        batch.setExecutionTimeMs(Math.round(execTimeMs * 100.0) / 100.0);
        batch = batchRepository.save(batch);

        for (int i = 0; i < assignments.length; i++) {
            AllocatedAssignmentEntity assignment = new AllocatedAssignmentEntity();
            assignment.setBatchId(batch.getBatchId());
            assignment.setVehicleId(vehicleIds[i]);
            assignment.setBookingId(bookingIds[assignments[i]]);
            assignment.setDeadheadDistanceKm(costMatrix[i][assignments[i]]);
            assignment.setEstimatedEta("2026-08-25 08:00:00");
            assignmentRepository.save(assignment);
        }

        return batch;
    }

    public AllocationBatchEntity executeGreedyRealtime(Long bookingId) {
        long startTime = System.nanoTime();

        long[] vehicleIds = {4, 5, 6, 7};
        double[] distances = {3.10, 8.40, 5.20, 11.00};

        GreedyAllocator.CandidateVehicle bestCandidate = GreedyAllocator.findBestVehicle(vehicleIds, distances);

        long endTime = System.nanoTime();
        double execTimeMs = (endTime - startTime) / 1_000_000.0;

        AllocationBatchEntity batch = new AllocationBatchEntity();
        batch.setBatchType("REALTIME_GREEDY");
        batch.setMatrixDimensions("1x" + vehicleIds.length);
        batch.setCostMatrixPayload("[[" + bestCandidate.transitCostKm + "]]");
        batch.setCandidateVehicleIds(Arrays.toString(vehicleIds));
        batch.setCandidateBookingIds("[" + bookingId + "]");
        batch.setTotalNetworkCost(bestCandidate.transitCostKm);
        batch.setExecutionTimeMs(Math.round(execTimeMs * 100.0) / 100.0);
        batch = batchRepository.save(batch);

        AllocatedAssignmentEntity assignment = new AllocatedAssignmentEntity();
        assignment.setBatchId(batch.getBatchId());
        assignment.setVehicleId(bestCandidate.vehicleId);
        assignment.setBookingId(bookingId);
        assignment.setDeadheadDistanceKm(bestCandidate.transitCostKm);
        assignment.setEstimatedEta("ASAP");
        assignmentRepository.save(assignment);

        return batch;
    }

    public List<AllocationBatchEntity> getAllBatches() {
        return batchRepository.findAllByOrderByBatchIdDesc();
    }

    // THIS IS THE MISSING METHOD FOR THE VISUALIZER
    public List<AllocatedAssignmentEntity> getAssignmentsByBatchId(Long batchId) {
        return assignmentRepository.findByBatchId(batchId);
    }
}