package task2_intelligent_resource_allocation.algorithm;

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
        int n = vehicleIds.length;
        if (n == 0) return null;

        CandidateVehicle[] heap = new CandidateVehicle[n];
        int size = 0;

        for (int i = 0; i < n; i++) {
            CandidateVehicle vehicle = new CandidateVehicle(vehicleIds[i], distances[i]);
            heap[size] = vehicle;
            int current = size;
            size++;

            while (current > 0) {
                int parent = (current - 1) / 2;
                if (heap[current].transitCostKm < heap[parent].transitCostKm) {
                    CandidateVehicle temp = heap[current];
                    heap[current] = heap[parent];
                    heap[parent] = temp;
                    current = parent;
                } else {
                    break;
                }
            }
        }

        CandidateVehicle min = heap[0];

        heap[0] = heap[size - 1];
        size--;

        int index = 0;
        while (true) {
            int smallest = index;
            int left = 2 * index + 1;
            int right = 2 * index + 2;

            if (left < size && heap[left].transitCostKm < heap[smallest].transitCostKm) {
                smallest = left;
            }
            if (right < size && heap[right].transitCostKm < heap[smallest].transitCostKm) {
                smallest = right;
            }

            if (smallest != index) {
                CandidateVehicle temp = heap[index];
                heap[index] = heap[smallest];
                heap[smallest] = temp;
                index = smallest;
            } else {
                break;
            }
        }

        return min;
    }
}