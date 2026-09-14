import java.util.*;

public class Forest {

    private final int width;
    private final int height;
    private final Cell[][] grid;
    private final Random random;
    private WindCell[][] windField;
    private double[][] ElevationMap;
    private SoilCell[][] soilField;
    private CloudCell[][] cloudField;
    private double[][] atmosphericHumidity;


    public Forest(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];
        this.random = new Random();
        this.windField = new WindCell[width][height];
        this.ElevationMap = new double[width][height];
        this.soilField = new SoilCell[width][height];
        this.cloudField = new CloudCell[width][height];
        this.atmosphericHumidity = new double[width][height];
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                this.atmosphericHumidity[i][j]= 0;
            }
        }
        initializeWind();
        generateValley();
        initializeSoil();
        initializeWater();
    }

    public Cell[][] getGrid() {
        return grid;
    }

    //------------Utilitaires-------------//
    public boolean hasWaterNeighbor(int x, int y) {

        for (Cell neighbor : getNeighbors(x, y)) {
            int nx = neighbor.getX();
            int ny = neighbor.getY();
            double maxWaterContent = this.getSoilFieldAt(nx,ny).getType().getCapacity();
            double waterContent = this.getSoilFieldAt(nx,ny).getWaterDepth();
            if (neighbor != null &&
                waterContent>=0.5*maxWaterContent) {
                return true;
            }
        }

        return false;
    }

    public List<Cell> getNeighbors(int x, int y) {

        List<Cell> neighbors = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {

                if (dx == 0 && dy == 0)
                    continue;

                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width &&
                        ny >= 0 && ny < height) {

                    neighbors.add(grid[nx][ny]);
                }
            }
        }

        return neighbors;
    }

    private int[][] generateUniqueCenters(int numCenters, double minDistance) {
        int[][] centers = new int[numCenters][2];
        for (int i = 0; i < numCenters; i++) {
            int newX, newY;
            boolean valid;
            do {
                newX = random.nextInt(width);
                newY = random.nextInt(height);
                valid = true;
                // Vérifier la distance avec tous les centres déjà placés
                for (int j = 0; j < i; j++) {
                    double distance = Math.sqrt(Math.pow(newX - centers[j][0], 2) + Math.pow(newY - centers[j][1], 2));
                    if (distance < minDistance) {
                        valid = false;
                        break;
                    }
                }
            } while (!valid);
            centers[i][0] = newX;
            centers[i][1] = newY;
        }
        return centers;
    }

    public boolean isInside(int x,int y){
        return(x>=0 && x<width && y>=0 && y<height);
    }

    //-------------PLUVIOMETRIE-----------//
    public void addRain(int nx,int ny,double rainAmount){
        soilField[nx][ny].addSurfaceWater(rainAmount);
    }

    public void updateAtmosphericHumidityAt(int x,int y,double evaporation){
        atmosphericHumidity[x][y] += evaporation;
        if(atmosphericHumidity[x][y]>=0.6){
            atmosphericHumidity[x][y]-=0.6;
            spawnCloud(x,y);
        }
    }

    public void spawnCloud(int x,int y){
        List<CloudType> candidates = new ArrayList<>();
        for(CloudType type : CloudTypes.ALL){
            candidates.add(type);
        }
        CloudType chosen = candidates.get(random.nextInt(candidates.size()));
        cloudField[x][y] = new Cloud(x,y,chosen,chosen.getBaseWaterContent());
    }

    //--------------HYDROLOGIE-------------//
    private static class FlowCell {
        int x;
        int y;
        double elevation;

        FlowCell(int x, int y, double elevation) {
            this.x = x;
            this.y = y;
            this.elevation = elevation;
        }
    }

    private static class Direction {
        int dx;
        int dy;

        Direction(int dx,int dy){
            this.dx = dx;
            this.dy = dy;
        }
    }

    private static final Direction[] DIRECTIONS = {
            new Direction(-1,-1),
            new Direction(-1,0),
            new Direction(-1,1),
            new Direction(0,-1),
            new Direction(0,1),
            new Direction(1,-1),
            new Direction(1,0),
            new Direction(1,1)
    };

    private int[] randomHighPoint(double minElevation){

        while(true){

            int x = random.nextInt(width);
            int y = random.nextInt(height);

            if(ElevationMap[x][y] > minElevation){
                return new int[]{x,y};
            }
        }
    }   


    //--------------PEDOLOGIE & ELEVATION--------------//
    private SoilType determineInitialSoilType(double elevation) {
        if (elevation > 250) {
            return SoilTypes.ROCHE_CALCAIRE;
        } else if (elevation > 200) {
            return SoilTypes.ROCHE_GRES;
        } else if (elevation > 140) {
            return SoilTypes.ARGILE;
        } else if (elevation > 60) {
            return SoilTypes.LIMON;
        } else {
            return SoilTypes.SABLE;
        }
    }

    private double valueNoise(double x, double y) {

        int x0 = (int)Math.floor(x);
        int y0 = (int)Math.floor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        double sx = smoothstep(x - x0);
        double sy = smoothstep(y - y0);

        double n00 = randomValue(x0, y0);
        double n10 = randomValue(x1, y0);
        double n01 = randomValue(x0, y1);
        double n11 = randomValue(x1, y1);

        double ix0 = n00 + sx * (n10 - n00);
        double ix1 = n01 + sx * (n11 - n01);

        return ix0 + sy * (ix1 - ix0);
    }

    private double fractalNoise(double x, double y) {

        double total = 0;
        double frequency = 0.01;
        double amplitude = 1;

        int octaves = 5;

        for (int i = 0; i < octaves; i++) {

            total += valueNoise(x * frequency, y * frequency) * amplitude;

            frequency *= 2;
            amplitude *= 0.5;
        }

        return total;
    }

    private double randomValue(int x, int y) {
        int n = x * 374761393 + y * 668265263;
        n = (n ^ (n >> 13)) * 1274126177;
        return ((n ^ (n >> 16)) & 0xffff) / (double)0xffff;
    }

    private double smoothstep(double t) {
        return t * t * (3 - 2 * t);
    }

    private double valleyEffect(int x, int y, int[] center, double depth, double radius) {
        double dx = x - center[0];
        double dy = y - center[1];
        double distanceSquared = dx * dx + dy * dy;
        // Effet gaussien inversé : plus on est proche du centre, plus l'élévation diminue
        return -depth * Math.exp(-distanceSquared / (2 * radius * radius));
    }

    private double hillEffect(int x, int y, int[] center, double height, double radius) {
        double dx = x - center[0];
        double dy = y - center[1];
        double distanceSquared = dx * dx + dy * dy;
        // Effet gaussien : plus on est proche du centre, plus l'élévation augmente
        return height * Math.exp(-distanceSquared / (2 * radius * radius));
    }

    private double[][] smoothElevation(double[][] elevationMap, int radius) {
        int width = elevationMap.length;
        int height = elevationMap[0].length;
        double[][] smoothed = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double sum = 0.0;
                int count = 0;
                // Parcourir le voisinage
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        // Vérifier les limites de la grille
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            sum += elevationMap[nx][ny];
                            count++;
                        }
                    }
                }
                smoothed[x][y] = sum / count; // Moyenne des voisins
            }
        }
        return smoothed;
    }


    //--------------INITIALISATIONS--------//
    public void initializeWorld() {

        /* 
        //génération des roseaux
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (hasWaterNeighbor(x, y) && grid[x][y]==null) {
                    Biome RoseauBiome = new Biome(
                            CellType.TREE,
                            width,
                            height,
                            0.2,
                            random
                    );
                    RoseauBiome.generateFrom(grid, x, y, TreeTypes.ROSEAU,this);
                }
            }
        }
        */

        //Biomes TREE distincts
        int numTreeBiomes = 20;
        for (int i = 0; i < numTreeBiomes; i++) {
            int x_random = random.nextInt(width);
            int y_random = random.nextInt(height);
            Biome treeBiome = new Biome(
                    CellType.TREE,
                    width,
                    height,
                    0.5,
                    random
            );
            treeBiome.generateFrom(grid, x_random, y_random,this);
        }

        // Remplissage du reste
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] == null) {
                    Biome treeBiome = new Biome(
                            CellType.TREE,
                            width,
                            height,
                            0.5,
                            random
                    );
                    treeBiome.generateFrom(grid, x, y,this);
                }
            }
        }
    }

    private void initializeWind() {

        int startX = random.nextInt(width);
        int startY = random.nextInt(height);

        double baseAngle = random.nextDouble() * 2 * Math.PI;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[width][height];

        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        windField[startX][startY] =
                new WindCell(Math.cos(baseAngle), Math.sin(baseAngle));

        while (!queue.isEmpty()) {

            int[] pos = queue.poll();
            int x = pos[0];
            int y = pos[1];

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {

                    int nx = x + dx;
                    int ny = y + dy;

                    if (nx >= 0 && nx < width &&
                            ny >= 0 && ny < height &&
                            !visited[nx][ny]) {

                        visited[nx][ny] = true;

                        WindCell parent = windField[x][y];

                        // 🔊 Ajout de bruit angulaire
                        double angle =
                                Math.atan2(parent.getDy(), parent.getDx());

                        angle += (random.nextDouble() - 0.5) * 0.3;

                        WindCell child = new WindCell(
                                Math.cos(angle),
                                Math.sin(angle)
                        );

                        child.normalize();

                        windField[nx][ny] = child;
                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }
    }

    public void generateValley() {

        double zMax = 140.0;
        double minDistance = Math.min(width, height) * 0.15;

        int numValleys = 2 + random.nextInt(5);
        int numHills = 2 + random.nextInt(5);

        int[][] valleyCenters = generateUniqueCenters(numValleys, minDistance);
        int[][] hillCenters = generateUniqueCenters(numHills, minDistance);

        double[][] elevationMap = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                double base = fractalNoise(x, y);

                elevationMap[x][y] = base * zMax;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                for (int[] center : valleyCenters) {
                    elevationMap[x][y] += valleyEffect(x, y, center, 120, 25);
                }

                for (int[] center : hillCenters) {
                    elevationMap[x][y] += hillEffect(x, y, center, 150, 20);
                }
            }
        }

        elevationMap = smoothElevation(elevationMap, 2);

        this.ElevationMap = elevationMap;
    }

    public void initializeSoil(){
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                if(soilField[i][j]==null){
                    double elevation = ElevationMap[i][j];
                    SoilType initialSoilType = determineInitialSoilType(elevation);
                    SoilBiome sol = new SoilBiome(
                            initialSoilType,
                            width,
                            height,
                            0.2,
                            random
                    );
                    sol.generateFrom(this,soilField,i,j);
                }
            }
        }
    }

    public void initializeWater(){

        int riverCount = (width + height) / 15;

        double minElevation = 150;

        for(int i=0;i<riverCount;i++){

            int[] source = randomHighPoint(minElevation);

            SoilField[source[0]][source[1]].setSource(true);
        }
    }

    //-------------GETTER/SETTER--------//

    public WindCell getWindAt(int x,int y){return this.windField[x][y];}
    public int getWidth(){return this.width;}
    public int getHeight(){return this.height;}
    public Cell getCellAt(int x,int y){return this.grid[x][y];}
    public double getElevationAt(int x, int y) {return ElevationMap[x][y];}
    public double[][] getElevationMap() {return ElevationMap;}
    public SoilCell[][] getSoilField() {return soilField;}
    public SoilCell getSoilFieldAt(int x, int y) {return soilField[x][y];}
    public CloudCell[][] getCloudField(){return cloudField;}
    public CloudCell getCloudFieldAt(int x,int y){return cloudField[x][y];}
    public int getFlowDirX(int x,int y){return flowDirX[x][y];}
    public int getFlowDirY(int x,int y){return flowDirY[x][y];}

}
