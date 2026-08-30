package task2_intelligent_resource_allocation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "allocated_assignments")
public class AllocatedAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "deadhead_distance_km")
    private Double deadheadDistanceKm;

    @Column(name = "estimated_eta")
    private String estimatedEta;

    @Column(name = "assignment_status")
    private String assignmentStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private String createdAt;

    // --- Standard Getters and Setters ---
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Double getDeadheadDistanceKm() { return deadheadDistanceKm; }
    public void setDeadheadDistanceKm(Double deadheadDistanceKm) { this.deadheadDistanceKm = deadheadDistanceKm; }

    public String getEstimatedEta() { return estimatedEta; }
    public void setEstimatedEta(String estimatedEta) { this.estimatedEta = estimatedEta; }

    public String getAssignmentStatus() { return assignmentStatus; }
    public void setAssignmentStatus(String assignmentStatus) { this.assignmentStatus = assignmentStatus; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}