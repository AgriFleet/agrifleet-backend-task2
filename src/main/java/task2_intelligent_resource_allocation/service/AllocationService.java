package task2_intelligent_resource_allocation.service;

import task2_intelligent_resource_allocation.algorithm.GreedyAllocator;
import task2_intelligent_resource_allocation.algorithm.HungarianAlgorithm;
import task2_intelligent_resource_allocation.entity.AllocatedAssignmentEntity;
import task2_intelligent_resource_allocation.entity.AllocationBatchEntity;
import task2_intelligent_resource_allocation.repository.AllocatedAssignmentRepository;
import task2_intelligent_resource_allocation.repository.AllocationBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AllocationService {

    private final AllocationBatchRepository batchRepository;
    private final AllocatedAssignmentRepository assignmentRepository;
    private final RestTemplate restTemplate;

    private final String CORE_SERVICE_URL = "http://localhost:8080/api/v1";

    public AllocationService(AllocationBatchRepository batchRepository,
                             AllocatedAssignmentRepository assignmentRepository) {
        this.batchRepository = batchRepository;
        this.assignmentRepository = assignmentRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public AllocationBatchEntity executeHungarianBatch() {
        long startTime = System.nanoTime();

        List<VehicleDTO> vehicles = fetchAvailableVehicles();
        List<BookingDTO> bookings = fetchPendingBookings();

        int numVehicles = vehicles.size();
        int numBookings = bookings.size();

        if (numVehicles == 0 || numBookings == 0) {
            throw new RuntimeException("Cannot run batch: Need at least 1 AVAILABLE vehicle and 1 PENDING booking in the database.");
        }

        int n = Math.max(numVehicles, numBookings);

        double[][] costMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i < numVehicles && j < numBookings) {
                    costMatrix[i][j] = calculateHaversineDistance(
                            vehicles.get(i).getCurrentLat(), vehicles.get(i).getCurrentLng(),
                            bookings.get(j).getFarmLat(), bookings.get(j).getFarmLng()
                    );
                } else {
                    costMatrix[i][j] = 99999.0;
                }
            }
        }

        int[] assignments = HungarianAlgorithm.findOptimalAssignments(costMatrix);

        long endTime = System.nanoTime();
        double execTimeMs = (endTime - startTime) / 1_000_000.0;

        double totalCost = 0.0;
        for (int i = 0; i < assignments.length; i++) {
            int assignedBookingIdx = assignments[i];
            if (i < numVehicles && assignedBookingIdx < numBookings) {
                totalCost += costMatrix[i][assignedBookingIdx];
            }
        }

        AllocationBatchEntity batch = new AllocationBatchEntity();
        batch.setBatchType("SCHEDULED_BATCH");
        batch.setMatrixDimensions(numVehicles + "x" + numBookings);
        batch.setCostMatrixPayload(Arrays.deepToString(costMatrix));

        long[] vIds = vehicles.stream().mapToLong(VehicleDTO::getVehicleId).toArray();
        long[] bIds = bookings.stream().mapToLong(BookingDTO::getBookingId).toArray();
        batch.setCandidateVehicleIds(Arrays.toString(vIds));
        batch.setCandidateBookingIds(Arrays.toString(bIds));

        batch.setTotalNetworkCost(Math.round(totalCost * 100.0) / 100.0);
        batch.setExecutionTimeMs(Math.round(execTimeMs * 100.0) / 100.0);
        batch = batchRepository.save(batch);

        for (int i = 0; i < assignments.length; i++) {
            int assignedBookingIdx = assignments[i];
            if (i < numVehicles && assignedBookingIdx < numBookings) {
                AllocatedAssignmentEntity assignment = new AllocatedAssignmentEntity();
                assignment.setBatchId(batch.getBatchId());
                assignment.setVehicleId(vehicles.get(i).getVehicleId());
                assignment.setBookingId(bookings.get(assignedBookingIdx).getBookingId());
                assignment.setDeadheadDistanceKm(Math.round(costMatrix[i][assignedBookingIdx] * 100.0) / 100.0);
                assignment.setEstimatedEta("PENDING");
                assignmentRepository.save(assignment);
            }
        }

        return batch;
    }

    @Transactional
    public AllocationBatchEntity executeGreedyRealtime(Long bookingId) {
        long startTime = System.nanoTime();

        BookingDTO targetBooking = fetchBookingById(bookingId);
        List<VehicleDTO> vehicles = fetchAvailableVehicles();
        if (vehicles.isEmpty()) {
            throw new RuntimeException("No available vehicles to dispatch!");
        }

        long[] vehicleIds = new long[vehicles.size()];
        double[] distances = new double[vehicles.size()];

        for (int i = 0; i < vehicles.size(); i++) {
            VehicleDTO v = vehicles.get(i);
            vehicleIds[i] = v.getVehicleId();
            distances[i] = calculateHaversineDistance(
                    v.getCurrentLat(), v.getCurrentLng(),
                    targetBooking.getFarmLat(), targetBooking.getFarmLng()
            );
        }

        GreedyAllocator.CandidateVehicle bestCandidate = GreedyAllocator.findBestVehicle(vehicleIds, distances);

        long endTime = System.nanoTime();
        double execTimeMs = (endTime - startTime) / 1_000_000.0;

        AllocationBatchEntity batch = new AllocationBatchEntity();
        batch.setBatchType("REALTIME_GREEDY");
        batch.setMatrixDimensions("1x" + vehicles.size());
        batch.setCostMatrixPayload("[[" + Math.round(bestCandidate.transitCostKm * 100.0) / 100.0 + "]]");
        batch.setCandidateVehicleIds(Arrays.toString(vehicleIds));
        batch.setCandidateBookingIds("[" + bookingId + "]");
        batch.setTotalNetworkCost(Math.round(bestCandidate.transitCostKm * 100.0) / 100.0);
        batch.setExecutionTimeMs(Math.round(execTimeMs * 100.0) / 100.0);
        batch = batchRepository.save(batch);

        AllocatedAssignmentEntity assignment = new AllocatedAssignmentEntity();
        assignment.setBatchId(batch.getBatchId());
        assignment.setVehicleId(bestCandidate.vehicleId);
        assignment.setBookingId(bookingId);
        assignment.setDeadheadDistanceKm(Math.round(bestCandidate.transitCostKm * 100.0) / 100.0);
        assignment.setEstimatedEta("ASAP");
        assignmentRepository.save(assignment);

        return batch;
    }

    public List<AllocationBatchEntity> getAllBatches() {
        return batchRepository.findAllByOrderByBatchIdDesc();
    }

    public List<AllocatedAssignmentEntity> getAssignmentsByBatchId(Long batchId) {
        return assignmentRepository.findByBatchId(batchId);
    }

    public void confirmAssignment(Long assignmentId) {
        AllocatedAssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));

        assignment.setAssignmentStatus("DISPATCHED");
        assignmentRepository.save(assignment);
    }

    private List<VehicleDTO> fetchAvailableVehicles() {
        VehicleDTO[] allVehicles = restTemplate.getForObject(CORE_SERVICE_URL + "/vehicles", VehicleDTO[].class);
        return Arrays.stream(allVehicles != null ? allVehicles : new VehicleDTO[0])
                .filter(v -> "AVAILABLE".equalsIgnoreCase(v.getAvailabilityStatus()))
                .collect(Collectors.toList());
    }

    private List<BookingDTO> fetchPendingBookings() {
        BookingDTO[] allBookings = restTemplate.getForObject(CORE_SERVICE_URL + "/bookings", BookingDTO[].class);
        return Arrays.stream(allBookings != null ? allBookings : new BookingDTO[0])
                .filter(b -> "PENDING".equalsIgnoreCase(b.getBookingStatus()))
                .collect(Collectors.toList());
    }

    private BookingDTO fetchBookingById(Long id) {
        BookingDTO booking = restTemplate.getForObject(CORE_SERVICE_URL + "/bookings/" + id, BookingDTO.class);
        if (booking == null) {
            throw new RuntimeException("Booking not found on Core Service: " + id);
        }
        return booking;
    }

    public static class VehicleDTO {
        private Long vehicleId;
        private Double currentLat;
        private Double currentLng;
        private String availabilityStatus;

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
        public Double getCurrentLat() { return currentLat; }
        public void setCurrentLat(Double currentLat) { this.currentLat = currentLat; }
        public Double getCurrentLng() { return currentLng; }
        public void setCurrentLng(Double currentLng) { this.currentLng = currentLng; }
        public String getAvailabilityStatus() { return availabilityStatus; }
        public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    }

    public static class BookingDTO {
        private Long bookingId;
        private Double farmLat;
        private Double farmLng;
        private String bookingStatus;

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public Double getFarmLat() { return farmLat; }
        public void setFarmLat(Double farmLat) { this.farmLat = farmLat; }
        public Double getFarmLng() { return farmLng; }
        public void setFarmLng(Double farmLng) { this.farmLng = farmLng; }
        public String getBookingStatus() { return bookingStatus; }
        public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}