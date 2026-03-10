public abstract class Cell {
    protected int x, y;
    protected double z;

    public Cell(int x, int y,double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public abstract void update(Forest forest, Cell[][] newGrid);
    public abstract CellType getType(); // pour switch
    public int getX() {return x;}
    public int getY() {return y;}

    public void setZ(double z){this.z=z;}
    public double getZ(){return z;}

}