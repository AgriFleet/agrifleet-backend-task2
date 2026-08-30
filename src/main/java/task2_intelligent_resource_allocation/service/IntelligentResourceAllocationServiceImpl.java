package task2_intelligent_resource_allocation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task2_intelligent_resource_allocation.algorithm.GreedyPriorityAllocator;
import task2_intelligent_resource_allocation.algorithm.HungarianAlgorithm;
import task2_intelligent_resource_allocation.dto.AllocationResponseDTO;
import task2_intelligent_resource_allocation.entity.AllocatedAssignmentEntity;
import task2_intelligent_resource_allocation.entity.AllocationBatchEntity;
import task2_intelligent_resource_allocation.entity.BookingEntity;
import task2_intelligent_resource_allocation.entity.VehicleEntity;
import task2_intelligent_resource_allocation.repository.AllocatedAssignmentRepository;
import task2_intelligent_resource_allocation.repository.AllocationBatchRepository;
import task2_intelligent_resource_allocation.repository.BookingRepository;
import task2_intelligent_resource_allocation.repository.VehicleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntelligentResourceAllocationServiceImpl implements IntelligentResourceAllocationService {

    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final AllocationBatchRepository batchRepository;
    private final AllocatedAssignmentRepository assignmentRepository;
    private final HungarianAlgorithm hungarianAlgorithm;
    private final GreedyPriorityAllocator greedyAllocator;

    public IntelligentResourceAllocationServiceImpl(VehicleRepository vehicleRepository,
                                                    BookingRepository bookingRepository,
                                                    AllocationBatchRepository batchRepository,
                                                    AllocatedAssignmentRepository assignmentRepository,
                                                    HungarianAlgorithm hungarianAlgorithm,
                                                    GreedyPriorityAllocator greedyAllocator) {
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.batchRepository = batchRepository;
        this.assignmentRepository = assignmentRepository;
        this.hungarianAlgorithm = hungarianAlgorithm;
        this.greedyAllocator = greedyAllocator;
    }

    @Override
    @Transactional
    public AllocationResponseDTO executeBatchAllocation() {
        long startTime = System.currentTimeMillis();
        List<VehicleEntity> vehicles = vehicleRepository.findByAvailabilityStatus("AVAILABLE");
        List<BookingEntity> bookings = bookingRepository.findByBookingStatus("PENDING");

        int n = Math.max(vehicles.size(), bookings.size());
        if (n == 0) return buildEmptyResponse("BATCH_HUNGARIAN");

        double[][] costMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i < vehicles.size() && j < bookings.size()) {
                    costMatrix[i][j] = calculateHaversine(
                            vehicles.get(i).getCurrentLat(), vehicles.get(i).getCurrentLng(),
                            bookings.get(j).getFarmLat(), bookings.get(j).getFarmLng());
                } else {
                    costMatrix[i][j] = 99999.0;
                }
            }
        }

        int[] assignments = hungarianAlgorithm.solve(costMatrix);

        double totalDistance = 0;
        List<AllocationResponseDTO.AssignmentDetail> details = new ArrayList<>();

        AllocationBatchEntity batch = saveBatchRecord("SCHEDULED_BATCH", vehicles.size() + "x" + bookings.size(), 0.0, 0.0);

        for (int vIdx = 0; vIdx < assignments.length; vIdx++) {
            int bIdx = assignments[vIdx];
            if (vIdx < vehicles.size() && bIdx < bookings.size()) {
                double dist = costMatrix[vIdx][bIdx];
                totalDistance += dist;
                saveAssignmentRecord(batch.getBatchId(), vehicles.get(vIdx).getVehicleId(), bookings.get(bIdx).getBookingId(), dist);

                AllocationResponseDTO.AssignmentDetail detail = new AllocationResponseDTO.AssignmentDetail();
                detail.setVehicleId(vehicles.get(vIdx).getVehicleId());
                detail.setBookingId(bookings.get(bIdx).getBookingId());
                detail.setDistanceKm(dist);
                details.add(detail);
            }
        }

        long execTime = System.currentTimeMillis() - startTime;
        batch.setTotalNetworkCost(totalDistance);
        batch.setExecutionTimeMs((double) execTime);
        batchRepository.save(batch);

        AllocationResponseDTO response = new AllocationResponseDTO();
        response.setBatchId(batch.getBatchId());
        response.setStrategyUsed("BATCH_HUNGARIAN");
        response.setTotalMatches(details.size());
        response.setTotalNetworkDistanceKm(totalDistance);
        response.setExecutionTimeMs((double) execTime);
        response.setAssignments(details);
        return response;
    }

    @Override
    @Transactional
    public AllocationResponseDTO executeInstantAllocation() {
        long startTime = System.currentTimeMillis();
        List<VehicleEntity> vehicles = vehicleRepository.findByAvailabilityStatus("AVAILABLE");
        List<BookingEntity> bookings = bookingRepository.findByBookingStatus("PENDING");

        List<GreedyPriorityAllocator.MatchCandidate> matches = greedyAllocator.allocateInstantly(vehicles, bookings);

        AllocationBatchEntity batch = saveBatchRecord("REALTIME_GREEDY", vehicles.size() + "x" + bookings.size(), 0.0, 0.0);

        double totalDistance = 0;
        List<AllocationResponseDTO.AssignmentDetail> details = new ArrayList<>();

        for (GreedyPriorityAllocator.MatchCandidate match : matches) {
            totalDistance += match.distanceKm;
            saveAssignmentRecord(batch.getBatchId(), match.vehicle.getVehicleId(), match.booking.getBookingId(), match.distanceKm);

            AllocationResponseDTO.AssignmentDetail detail = new AllocationResponseDTO.AssignmentDetail();
            detail.setVehicleId(match.vehicle.getVehicleId());
            detail.setBookingId(match.booking.getBookingId());
            detail.setDistanceKm(match.distanceKm);
            details.add(detail);
        }

        long execTime = System.currentTimeMillis() - startTime;
        batch.setTotalNetworkCost(totalDistance);
        batch.setExecutionTimeMs((double) execTime);
        batchRepository.save(batch);

        AllocationResponseDTO response = new AllocationResponseDTO();
        response.setBatchId(batch.getBatchId());
        response.setStrategyUsed("INSTANT_GREEDY");
        response.setTotalMatches(details.size());
        response.setTotalNetworkDistanceKm(totalDistance);
        response.setExecutionTimeMs((double) execTime);
        response.setAssignments(details);
        return response;
    }

    private AllocationBatchEntity saveBatchRecord(String type, String dims, double cost, double time) {
        AllocationBatchEntity batch = new AllocationBatchEntity();
        batch.setBatchType(type);
        batch.setMatrixDimensions(dims);
        batch.setCostMatrixPayload("[]");
        batch.setCandidateVehicleIds("[]");
        batch.setCandidateBookingIds("[]");
        batch.setTotalNetworkCost(cost);
        batch.setExecutionTimeMs(time);
        return batchRepository.save(batch);
    }

    private void saveAssignmentRecord(Long batchId, Long vehicleId, Long bookingId, double distance) {
        AllocatedAssignmentEntity assignment = new AllocatedAssignmentEntity();
        assignment.setBatchId(batchId);
        assignment.setVehicleId(vehicleId);
        assignment.setBookingId(bookingId);
        assignment.setDeadheadDistanceKm(distance);
        assignment.setEstimatedEta("PENDING");
        assignment.setAssignmentStatus("CONFIRMED");
        assignmentRepository.save(assignment);
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private AllocationResponseDTO buildEmptyResponse(String strategy) {
        AllocationResponseDTO response = new AllocationResponseDTO();
        response.setStrategyUsed(strategy);
        response.setTotalMatches(0);
        return response;
    }
}