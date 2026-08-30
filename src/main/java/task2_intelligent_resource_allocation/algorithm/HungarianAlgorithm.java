package task2_intelligent_resource_allocation.algorithm;

import java.util.Arrays;

public class HungarianAlgorithm {

    public static int[] findOptimalAssignments(double[][] costMatrix) {
        int rows = costMatrix.length;
        int cols = costMatrix[0].length;
        int n = Math.max(rows, cols);
        double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(matrix[i], 1e9);
            if (i < rows) {
                System.arraycopy(costMatrix[i], 0, matrix[i], 0, cols);
            }
        }

        double[] u = new double[n + 1], v = new double[n + 1];
        int[] p = new int[n + 1], way = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            p[0] = i;
            int j0 = 0;
            double[] minv = new double[n + 1];
            Arrays.fill(minv, Double.MAX_VALUE);
            boolean[] flagged = new boolean[n + 1];

            do {
                flagged[j0] = true;
                int i0 = p[j0], j1 = 0;
                double delta = Double.MAX_VALUE;

                for (int j = 1; j <= n; j++) {
                    if (!flagged[j]) {
                        double cur = matrix[i0 - 1][j - 1] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }

                for (int j = 0; j <= n; j++) {
                    if (flagged[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
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

        int[] result = new int[rows];
        for (int i = 0; i < rows; i++) {
            result[i] = -1;
            for (int j = 0; j < cols; j++) {
                if (p[j + 1] == i + 1) {
                    result[i] = j;
                }
            }
        }
        return result;
    }
}