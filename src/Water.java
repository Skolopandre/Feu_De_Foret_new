public class Water extends Cell {

    public Water(int x, int y,double z) {
        super(x, y,z);
    }

    @Override
    public void update(Forest forest, Cell[][] newGrid) {

        double maxWaterContent = forest.getSoilFieldAt(x,y).getType().getCapacity();
        double waterContent = forest.getSoilFieldAt(x,y).getWaterContent();
        if(waterContent<0.2*maxWaterContent){
            newGrid[x][y] = new Empty(x,y,0);
            return;
        }

        newGrid[this.x][this.y] = this;
    }

    @Override
    public CellType getType() {
        return CellType.WATER;
    }
    @Override
    public double getZ() {return z;}
}