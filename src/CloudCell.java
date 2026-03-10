public abstract class CloudCell {
    protected int x,y;


    public CloudCell(int x,int y){
        this.x = x;
        this.y = y;
    }

    public abstract void update(Forest forest, CloudCell[][] newCloudGrid);
    public abstract CloudType getType();
    public int getX(){return x;}
    public int getY(){return y;}
}
