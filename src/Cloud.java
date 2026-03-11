import java.util.Random;

public class Cloud extends CloudCell {

    private CloudType type;
    private double waterContent;
    private Random random;

    public Cloud(int x,int y,CloudType type, double waterContent){
        super(x,y);
        this.type = type;
        this.waterContent = waterContent;
        this.random = new Random();
    }

    @Override
    public void update(Forest forest, CloudCell[][] newCloudGrid){

        WindCell wind = forest.getWindAt(x,y);
        double dx = wind.getDx();
        double dy = wind.getDy();
        int nx = x + (int)Math.round(dx) + random.nextInt(3)-1;
        int ny = y + (int)Math.round(dy) + random.nextInt(3)-1;;

        if(nx < 0 || ny < 0 || nx >= forest.getWidth() || ny >= forest.getHeight()){
            nx = x;
            ny = y;
        }

        double newWater = waterContent;
        newWater *= 0.98;

        if(newWater > type.getBaseWaterContent()*1.5){
            double rainAmount = 0.02 * newWater;
            newWater /= 2;
            forest.addRain(nx,ny,rainAmount);
        }

        if(newWater < 0.05){
            return;
        }
        CloudCell existing = newCloudGrid[nx][ny];

        if(existing instanceof Cloud other){
            double mergedWater = other.waterContent + newWater;
            newCloudGrid[nx][ny] =
                    new Cloud(nx,ny,type,mergedWater);
        }else{
            newCloudGrid[nx][ny] =
                    new Cloud(nx,ny,type,newWater);
        }
    }

    public CloudType getType(){return type;}
}
