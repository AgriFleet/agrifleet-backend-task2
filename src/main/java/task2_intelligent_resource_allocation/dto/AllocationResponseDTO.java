package task2_intelligent_resource_allocation.dto;

import java.util.List;

public class AllocationResponseDTO {
    private Long batchId;
    private String strategyUsed;
    private int totalMatches;
    private double totalNetworkDistanceKm;
    private double executionTimeMs;
    private List<AssignmentDetail> assignments;

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getStrategyUsed() { return strategyUsed; }
    public void setStrategyUsed(String strategyUsed) { this.strategyUsed = strategyUsed; }

    public int getTotalMatches() { return totalMatches; }
    public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }

    public double getTotalNetworkDistanceKm() { return totalNetworkDistanceKm; }
    public void setTotalNetworkDistanceKm(double totalNetworkDistanceKm) { this.totalNetworkDistanceKm = totalNetworkDistanceKm; }

    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public List<AssignmentDetail> getAssignments() { return assignments; }
    public void setAssignments(List<AssignmentDetail> assignments) { this.assignments = assignments; }

    public static class AssignmentDetail {
        private Long vehicleId;
        private Long bookingId;
        private double distanceKm;

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    }
}