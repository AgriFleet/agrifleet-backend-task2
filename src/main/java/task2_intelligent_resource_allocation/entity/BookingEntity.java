package task2_intelligent_resource_allocation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "farmer_id")
    private Long farmerId;

    @Column(name = "farm_lat")
    private Double farmLat;

    @Column(name = "farm_lng")
    private Double farmLng;

    @Column(name = "acreage")
    private Double acreage;

    @Column(name = "crop_type")
    private String cropType;

    @Column(name = "required_window_start")
    private String requiredWindowStart;

    @Column(name = "required_window_end")
    private String requiredWindowEnd;

    @Column(name = "booking_status")
    private String bookingStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private String createdAt;

    // --- Standard Getters and Setters ---
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public Double getFarmLat() { return farmLat; }
    public void setFarmLat(Double farmLat) { this.farmLat = farmLat; }

    public Double getFarmLng() { return farmLng; }
    public void setFarmLng(Double farmLng) { this.farmLng = farmLng; }

    public Double getAcreage() { return acreage; }
    public void setAcreage(Double acreage) { this.acreage = acreage; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public String getRequiredWindowStart() { return requiredWindowStart; }
    public void setRequiredWindowStart(String requiredWindowStart) { this.requiredWindowStart = requiredWindowStart; }

    public String getRequiredWindowEnd() { return requiredWindowEnd; }
    public void setRequiredWindowEnd(String requiredWindowEnd) { this.requiredWindowEnd = requiredWindowEnd; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}