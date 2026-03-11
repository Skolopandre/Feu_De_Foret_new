import java.util.List;
import java.util.ArrayList;

public class Soil extends SoilCell {

    private SoilType type;
    private double waterContent;
    private double waterDepth;

    public Soil(int x, int y,SoilType type){
        super(x,y);
        this.type=type;
        this.waterContent=0;
        this.waterDepth=0;
    }

    public void updateWater(Forest forest, SoilCell[][] newSoilField) {
        int x = this.getX();
        int y = this.getY();

        Soil newSoil = new Soil(x, y, this.type);
        newSoil.setWaterContent(this.waterContent, 0); // Copier le contenu en eau
        newSoil.addSurfaceWater(this.getWaterDepth());
        newSoilField[x][y] = newSoil;

        double infiltration= this.type.getPermeability() * newSoil.waterDepth;
        infiltration = Math.min(infiltration,waterDepth);

        newSoil.waterContent += infiltration;
        newSoil.waterDepth -= infiltration;
        newSoil.waterContent=Math.min(waterContent,1.0);

        double evaporation = 0.003*waterDepth;
        newSoil.waterDepth-=evaporation;
        forest.updateAtmosphericHumidityAt(x,y,evaporation);

        int nx = x+forest.getFlowDirX(x,y);
        int ny = y+forest.getFlowDirY(x,y);

        if (forest.isInside(nx, ny)) {

            SoilCell target = forest.getSoilFieldAt(nx, ny);

            double flow = 0.1 * waterDepth;

            waterDepth -= flow;
            target.addSurfaceWater(flow);
        }
        if (waterDepth < 0)
            waterDepth = 0;
    }

    /*Legacy code
    private void redistributeSurplus(Forest forest, SoilCell[][] newSoilField, double surplus, int x, int y) {

        double[][] elevationMap = forest.getElevationMap();
        int width = forest.getWidth();
        int height = forest.getHeight();

        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};

        double currentElevation = elevationMap[x][y];

        List<int[]> neighbors = new ArrayList<>();
        List<Double> slopes = new ArrayList<>();

        double totalSlope = 0;

        for (int[] dir : directions) {

            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {

                double neighborElevation = elevationMap[nx][ny];
                double slope = currentElevation - neighborElevation;

                if (slope > 0) {

                    neighbors.add(new int[]{nx,ny});
                    slopes.add(slope);
                    totalSlope += slope;

                }
            }
        }

        if (neighbors.isEmpty()) return;

        for (int i = 0; i < neighbors.size(); i++) {

            int[] n = neighbors.get(i);
            double slope = slopes.get(i);

            double share = surplus * (slope / totalSlope);

            SoilCell neighborCell = newSoilField[n[0]][n[1]];

            if (neighborCell instanceof Soil) {
                Soil neighborSoil = (Soil) neighborCell;
                neighborSoil.waterContent += share;
            }
        }
    }
    */


    public SoilType getType(){return type;}
    public double getWaterContent(){return waterContent;}
    public void setWaterContent(double addVolume,double subVolume) {
        this.waterContent += addVolume;
        this.waterContent -= subVolume;
    }
    public int getX(){return this.x;}
    public int getY(){return this.y;}
    public double getWaterDepth(){return waterDepth;}
    public void addSurfaceWater(double amount){waterDepth+=amount;}

}
