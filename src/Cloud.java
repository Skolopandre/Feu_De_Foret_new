public class Cloud extends CloudCell {

    private CloudType type;
    private double waterContent;

    public Cloud(int x,int y,CloudType type, double waterContent){
        super(x,y);
        this.type = type;
        this.waterContent = waterContent;
    }

    @Override
    public void update(Forest forest, CloudCell[][] newCloudGrid){

        WindCell wind = forest.getWindAt(x,y);
        double dx = wind.getDx();
        double dy = wind.getDy();
        int nx = x + (int)Math.round(dx);
        int ny = y + (int)Math.round(dy);

        if(nx < 0 || ny < 0 || nx >= forest.getWidth() || ny >= forest.getHeight()){
            nx = x;
            ny = y;
        }

        double newWater = waterContent;
        newWater *= 0.995;

        if(newWater > type.getBaseWaterContent()*1.5){
            double rainAmount = 0.1 * newWater;
            newWater -= rainAmount;
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
