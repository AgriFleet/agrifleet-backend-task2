package task2_intelligent_resource_allocation.algorithm;

import org.springframework.stereotype.Component;
import task2_intelligent_resource_allocation.entity.BookingEntity;
import task2_intelligent_resource_allocation.entity.VehicleEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class GreedyPriorityAllocator {

    public static class MatchCandidate {
        public VehicleEntity vehicle;
        public BookingEntity booking;
        public double distanceKm;

        public MatchCandidate(VehicleEntity vehicle, BookingEntity booking, double distanceKm) {
            this.vehicle = vehicle;
            this.booking = booking;
            this.distanceKm = distanceKm;
        }
    }

    public List<MatchCandidate> allocateInstantly(List<VehicleEntity> availableVehicles, List<BookingEntity> pendingBookings) {
        List<MatchCandidate> finalAssignments = new ArrayList<>();

        PriorityQueue<MatchCandidate> minHeap = new PriorityQueue<>(
                Comparator.comparingDouble(candidate -> candidate.distanceKm)
        );

        for (BookingEntity booking : pendingBookings) {
            for (VehicleEntity vehicle : availableVehicles) {
                double dist = calculateHaversineDistance(
                        booking.getFarmLat(), booking.getFarmLng(),
                        vehicle.getCurrentLat(), vehicle.getCurrentLng()
                );
                minHeap.add(new MatchCandidate(vehicle, booking, dist));
            }
        }

        List<Long> assignedVehicles = new ArrayList<>();
        List<Long> assignedBookings = new ArrayList<>();

        while (!minHeap.isEmpty() && finalAssignments.size() < Math.min(availableVehicles.size(), pendingBookings.size())) {
            MatchCandidate bestMatch = minHeap.poll();

            if (!assignedVehicles.contains(bestMatch.vehicle.getVehicleId()) &&
                    !assignedBookings.contains(bestMatch.booking.getBookingId())) {

                finalAssignments.add(bestMatch);
                assignedVehicles.add(bestMatch.vehicle.getVehicleId());
                assignedBookings.add(bestMatch.booking.getBookingId());
            }
        }

        return finalAssignments;
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