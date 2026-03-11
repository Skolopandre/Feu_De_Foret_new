import java.util.*;


public class Ash extends Cell {
    private int temp;
    private int timeSinceAsh;
    private double regenerateChances;
    private static final Random random = new Random();
    public Ash(int x, int y,double z,int temp) {
        super(x, y,z);
        this.temp=temp;
        timeSinceAsh=0;
        regenerateChances = 0.001;
    }

    @Override
    public void update(Forest forest, Cell[][] newGrid) {
        double maxWaterContent = forest.getSoilFieldAt(x,y).getType().getCapacity();
        double waterContent = forest.getSoilFieldAt(x,y).getWaterDepth();
        if(waterContent>=0.5*maxWaterContent){
            newGrid[x][y] = new Empty(x,y,0);
            return;
        }

        if (temp > 0) {
            temp = temp-3;
            newGrid[x][y] = this;
            return;
        }

        timeSinceAsh++;

        if (timeSinceAsh <= 30) {
            newGrid[x][y] = this;
            return;
        }
        newGrid[x][y] = new Empty(x,y,0);
    }

    @Override
    public CellType getType() {
        return CellType.ASH;
    }

    public void forceRegenerate(Forest forest,Cell[][] newGrid){
        // Construire liste des espèces stade 1
        List<TreeType> candidates = new ArrayList<>();

        for (TreeType type : TreeTypes.ALL) {
            if (type.getEcologicalStage() == 1) {
                candidates.add(type);
            }
        }
        if (!candidates.isEmpty()) {
            TreeType chosen = candidates.get(random.nextInt(candidates.size()));

            newGrid[x][y] = new Tree(x, y, forest.getCellAt(x,y).getZ(),chosen);
        }
    }
    public int get_temp(){return this.temp;}
    public int getTimeSinceAsh(){return this.timeSinceAsh;}

    @Override
    public double getZ() {return z;}
}