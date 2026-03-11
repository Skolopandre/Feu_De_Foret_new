public abstract class SoilCell {

    public final int x;
    public final int y;

    public SoilCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void updateWater(Forest forest, SoilCell[][] newSoilField);
    public abstract SoilType getType();
    public abstract int getX();
    public abstract int getY();
    public abstract double getWaterContent();
    public abstract void setWaterContent(double addVolume,double subVolume);
    public abstract void addSurfaceWater(double amount);
    public abstract double getWaterDepth();

}
