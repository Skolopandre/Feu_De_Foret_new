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
        this.waterContent=0;
        this.waterDepth=0;
		this.isSource = false;
    }

    public void updateWater(Forest forest, SoilCell[][] newSoilField) {
        int x = this.getX();
        int y = this.getY();  

        double infiltration= this.type.getPermeability() * this.waterDepth;
        infiltration = Math.min(infiltration,waterDepth);

        this.waterContent += infiltration;
        this.waterDepth -= infiltration;
        this.waterContent=Math.min(waterContent,1.0);

        double evaporation = 0.003*waterDepth;
        this.waterDepth-=evaporation;
        forest.updateAtmosphericHumidityAt(x,y,evaporation);

        if(this.getWaterContent()>1){
			double surplus = -(1-this.getWaterContent()) ;
        	redistributeSurplus(forest,newSoilField,surplus,x,y);
			this.setWaterContent(0,surplus);
		}

		if(this.isSource){this.waterDepth+=1;}
		
        if (this.waterDepth < 0){this.waterDepth = 0;}

		newSoilField[x][y] = this;
    }
 
    private void redistributeSurplus(Forest forest, SoilCell[][] newSoilField, double surplus, int x, int y) {

        double[][] elevationMap = forest.getElevationMap();
        int width = forest.getWidth();
        int height = forest.getHeight();

        int[][] directions = {{-1,0},{-1,-1},{-1,1},{1,0},{1,-1},{1,1},{0,-1},{0,1}};

        double currentElevation = elevationMap[x][y];

        List<int[]> candidates = new ArrayList<>();
        Double min_height = Double.MAX_VALUE ;

        for (int[] dir : directions) {
            int nx = x + dir[0];
		    int ny = y + dir[1];
		    double h = elevationMap[nx][ny] ;

            if (nx >= 0 && nx < width && ny >= 0 && ny < height && forest.getSoilAt(x,y).getWaterDepth()<1) {
                if( h<min_height){
				    min_height = h ;
				    candidates.clear() ;
				    candidates.add(dir) ;	
                }else if(h==min_height){
			        candidates.add(dir) ;
                }
            }
        }

        if (candidates.isEmpty()) return;

        int n = candidates.size() ;
        
        for (int i = 0; i < n; i++) {
            int[] c = candidates.get(i);
            double share = surplus/n ;
            SoilCell neighborCell = newSoilField[c[0]][c[1]] ;
	        neighborSoil.addSurfaceWater(share) ;
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
    public double getWaterDepth(){return waterDepth;}
    public void addSurfaceWater(double amount){waterDepth+=amount;}
	public void setSource(boolean state){this.isSource = state;}

}
