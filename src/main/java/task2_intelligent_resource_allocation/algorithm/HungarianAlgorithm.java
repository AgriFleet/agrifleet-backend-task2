package task2_intelligent_resource_allocation.algorithm;

import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class HungarianAlgorithm {
    public int[] solve(double[][] costMatrix) {
        int n = costMatrix.length;
        double[] u = new double[n + 1];
        double[] v = new double[n + 1];
        int[] p = new int[n + 1];
        int[] way = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            p[0] = i;
            int j0 = 0;
            double[] minV = new double[n + 1];
            Arrays.fill(minV, Double.MAX_VALUE);
            boolean[] used = new boolean[n + 1];

            do {
                used[j0] = true;
                int i0 = p[j0], j1 = 0;
                double delta = Double.MAX_VALUE;

                for (int j = 1; j <= n; j++) {
                    if (!used[j]) {
                        double cur = costMatrix[i0 - 1][j - 1] - u[i0] - v[j];
                        if (cur < minV[j]) {
                            minV[j] = cur;
                            way[j] = j0;
                        }
                        if (minV[j] < delta) {
                            delta = minV[j];
                            j1 = j;
                        }
                    }
                }

                for (int j = 0; j <= n; j++) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minV[j] -= delta;
                    }
                }
                j0 = j1;
            } while (p[j0] != 0);

            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }

        int[] assignment = new int[n];
        for (int j = 1; j <= n; j++) {
            assignment[p[j] - 1] = j - 1;
        }
        return assignment;
    }
}