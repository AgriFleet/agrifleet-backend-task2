package task2_intelligent_resource_allocation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "allocation_batches")
public class AllocationBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "batch_type", nullable = false)
    private String batchType;

    @Column(name = "matrix_dimensions")
    private String matrixDimensions;

    @Column(name = "cost_matrix_payload")
    private String costMatrixPayload;

    @Column(name = "candidate_vehicle_ids")
    private String candidateVehicleIds;

    @Column(name = "candidate_booking_ids")
    private String candidateBookingIds;

    @Column(name = "total_network_cost")
    private Double totalNetworkCost;

    @Column(name = "execution_time_ms")
    private Double executionTimeMs;

    @Column(name = "created_at", insertable = false, updatable = false)
    private String createdAt;

    // Getters and Setters
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getBatchType() { return batchType; }
    public void setBatchType(String batchType) { this.batchType = batchType; }
    public String getMatrixDimensions() { return matrixDimensions; }
    public void setMatrixDimensions(String matrixDimensions) { this.matrixDimensions = matrixDimensions; }
    public String getCostMatrixPayload() { return costMatrixPayload; }
    public void setCostMatrixPayload(String costMatrixPayload) { this.costMatrixPayload = costMatrixPayload; }
    public String getCandidateVehicleIds() { return candidateVehicleIds; }
    public void setCandidateVehicleIds(String candidateVehicleIds) { this.candidateVehicleIds = candidateVehicleIds; }
    public String getCandidateBookingIds() { return candidateBookingIds; }
    public void setCandidateBookingIds(String candidateBookingIds) { this.candidateBookingIds = candidateBookingIds; }
    public Double getTotalNetworkCost() { return totalNetworkCost; }
    public void setTotalNetworkCost(Double totalNetworkCost) { this.totalNetworkCost = totalNetworkCost; }
    public Double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}