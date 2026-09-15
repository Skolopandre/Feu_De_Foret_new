import java.util.List;
import java.util.ArrayList;

public class Soil extends SoilCell {

    private SoilType type;
    private double waterContent;
    private double waterDepth;
    private boolean isSource;

    public Soil(int x, int y,SoilType type){
        super(x,y);
        this.type=type;
        this.waterContent=0;    //eau infiltrée
        this.waterDepth=0;      //eau de surface
        this.isSource = false;
    }

    public void updateWater(Forest forest, SoilCell[][] newSoilField) {

        int x = this.getX();
        int y = this.getY();

        double newWaterContent = this.waterContent;
        double newWaterDepth = this.waterDepth;

        double infiltration =this.type.getPermeability() * newWaterDepth;

        infiltration = Math.min(infiltration, newWaterDepth);

        newWaterContent += infiltration;
        newWaterDepth -= infiltration;

        double evaporation = 0.003 * newWaterDepth;
        newWaterDepth -= evaporation;

        if (newWaterDepth < 0) {
            newWaterDepth = 0;
        }

        Soil newSoil = new Soil(x, y, this.type);

        newSoil.setWaterContent(newWaterContent, 0);
        newSoil.addSurfaceWater(newWaterDepth);
        newSoil.setSource(this.isSource);


        if (this.isSource) {
            newSoil.addSurfaceWater(10);
        }

        newSoilField[x][y] = newSoil;
    }


    public void redistributeSurplus(
            Forest forest,
            SoilCell[][] newSoilField) {

        int x = this.getX();
        int y = this.getY();

        if (this.waterDepth <= 1) {return;}

        double surplus = this.waterDepth - 1;
        this.waterDepth = 1;
        double[][] elevationMap = forest.getElevationMap();

        int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1},
                {-1,-1},
                {-1,1},
                {1,-1},
                {1,1}
        };

        List<int[]> candidates = new ArrayList<>();
        double minSlope = Double.MAX_VALUE;

        for (int[] dir : directions) {

            int nx = x + dir[0];
            int ny = y + dir[1];

            if (!forest.isInside(nx, ny)) {
                continue;
            }

            SoilCell oldNeighbor = forest.getSoilFieldAt(nx, ny);

            if (oldNeighbor.getWaterDepth() >= 1) {continue;}


            double distance = Math.sqrt((nx-x)^2+(ny-y)^2);
            double slope = (elevationMap[nx][ny]-elevationMap[x][y])/distance;



            if (slope < minSlope) {
                minSlope = slope;
                candidates.clear();
                candidates.add(dir);

            } else if (slope == minSlope) {
                candidates.add(dir);
            }
        }

        if (candidates.isEmpty()) {return;}

        double share = surplus / candidates.size();

        for (int[] candidate : candidates) {
            int nx = x + candidate[0];
            int ny = y + candidate[1];

            SoilCell newNeighbor = newSoilField[nx][ny];

            if (newNeighbor instanceof Soil) {
                Soil neighborSoil = (Soil) newNeighbor;
                neighborSoil.addSurfaceWater(share);
            }
        }
    }




    public SoilType getType(){return type;}
    public double getWaterContent(){return waterContent;}
    public void setWaterContent(double addVolume,double subVolume) {
        this.waterContent += addVolume;
        this.waterContent -= subVolume;
    }
    public int getX(){return this.x;}
    public int getY(){return this.y;}
    public double getWaterDepth(){return this.waterDepth;}
    public void addSurfaceWater(double amount){this.waterDepth+=amount;}
    public void setSource(boolean state){this.isSource = state;}
}
