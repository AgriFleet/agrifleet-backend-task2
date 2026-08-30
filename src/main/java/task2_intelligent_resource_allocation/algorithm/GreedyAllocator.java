package task2_intelligent_resource_allocation.algorithm;

import java.util.Comparator;
import java.util.PriorityQueue;

public class GreedyAllocator {

    public static class CandidateVehicle {
        public Long vehicleId;
        public double transitCostKm;

        public CandidateVehicle(Long vehicleId, double transitCostKm) {
            this.vehicleId = vehicleId;
            this.transitCostKm = transitCostKm;
        }
    }

    public static CandidateVehicle findBestVehicle(long[] vehicleIds, double[] distances) {
        PriorityQueue<CandidateVehicle> minHeap = new PriorityQueue<>(Comparator.comparingDouble(v -> v.transitCostKm));

        for (int i = 0; i < vehicleIds.length; i++) {
            minHeap.add(new CandidateVehicle(vehicleIds[i], distances[i]));
        }

        return minHeap.poll();
    }
}