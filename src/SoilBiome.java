import java.util.Random;
import java.awt.Color;
import java.util.*;
import java.awt.*;

public class SoilBiome {

    private final SoilType type;
    private final int width;
    private final int height;
    private final double spreadProbability;
    private final Random random;

    public SoilBiome(SoilType type, int width, int height, double spreadProbability, Random random) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.spreadProbability = spreadProbability;
        this.random = random;
    }

    public void generateFrom(Forest forest, SoilCell[][] grid, int startX, int startY) {
        double[][] elevationMap = forest.getElevationMap();
        double elevation = elevationMap[startX][startY];
        SoilType initialSoil = determineInitialSoil(elevation);
        grid[startX][startY] = new Soil(startX, startY, initialSoil);

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            propagateToNeighbors(forest, grid, queue, x, y, initialSoil);
        }
    }

    private void propagateToNeighbors(Forest forest, SoilCell[][] grid, Queue<int[]> queue, int x, int y, SoilType soilType) {
        double[][] elevationMap = forest.getElevationMap();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Haut, Bas, Gauche, Droite

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                if (grid[newX][newY] == null) {
                    double elevation = elevationMap[newX][newY];
                    double deltaAltitude = elevationMap[x][y] - elevation;
                    double pente = Math.abs(deltaAltitude);
                    double probability = calculateSpreadProbability(soilType, deltaAltitude, pente);

                    if (random.nextDouble() < probability) {
                        grid[newX][newY] = new Soil(newX, newY, soilType);
                        queue.add(new int[]{newX, newY});
                    }
                }
            }
        }
    }

    private SoilType determineInitialSoil(double elevation) {
        if (elevation > 150) {
            return SoilTypes.ROCHE_CALCAIRE;
        } else if (elevation > 100) {
            return SoilTypes.ROCHE_GRES;
        } else if (elevation > 60) {
            return SoilTypes.ARGILE;
        } else if (elevation > 30) {
            return SoilTypes.LIMON;
        } else {
            return SoilTypes.SABLE;
        }
    }

    //Finalement pas utilisé, mais je garde au cas ou
    private void propagateSoil(Forest forest, SoilCell[][] grid, int x, int y, SoilType soilType) {
        double[][] elevationMap = forest.getElevationMap();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];


            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                SoilCell currentCell = grid[newX][newY];
                if (currentCell == null) {
                    double elevation = elevationMap[newX][newY];
                    double deltaAltitude = elevationMap[x][y] - elevation;
                    double pente = Math.abs(deltaAltitude);

                    double probability = calculateSpreadProbability(soilType, deltaAltitude, pente);

                    if (random.nextDouble() < probability) {
                        grid[newX][newY] = new Soil(newX, newY, soilType);
                        propagateSoil(forest, grid, newX, newY, soilType);
                    }
                }
            }
        }
    }


    private double calculateSpreadProbability(SoilType soilType, double deltaAltitude, double pente) {
        double baseProbability = spreadProbability;


        if (soilType == SoilTypes.SABLE) {
            if (deltaAltitude < 0) {
                baseProbability += 0.3;
            }
        } else if (soilType == SoilTypes.ARGILE) {
            if (pente < 0.1) { //
                baseProbability += 0.2;
            }
        } else if (SoilTypes.ROCHE.contains(soilType)) {
            if (pente > 0.2) { //
                baseProbability += 0.1;
            }
        }

        return Math.min(1.0, Math.max(0.0, baseProbability));
    }
}
