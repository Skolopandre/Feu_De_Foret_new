import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Empty extends Cell {
    private double regenerateChances;
    private static final Random random = new Random();

    public Empty(int x, int y,double z) {
        super(x, y,z);
        regenerateChances = 0.001;
    }

    @Override
    public void update(Forest forest, Cell[][] newGrid) {
        double maxWaterContent = forest.getSoilFieldAt(x,y).getType().getCapacity();
        double waterContent = forest.getSoilFieldAt(x,y).getWaterContent();
        if(waterContent>=maxWaterContent){
            newGrid[x][y] = new Water(x,y,0);
            return;
        }

        int nbWaterNeighbor = 0;
        int nbTreeNeighbor = 0;
        for (Cell neighbor : forest.getNeighbors(x, y)) {
            if (neighbor != null && neighbor.getType() == CellType.TREE) {
                nbTreeNeighbor++;
            }
            if (neighbor != null && neighbor.getType() == CellType.WATER){
                nbWaterNeighbor++;
            }
        }

        if(nbWaterNeighbor>0 && random.nextDouble()<0.1){
            newGrid[x][y] = new Tree(x,y,forest.getCellAt(x,y).getZ(),TreeTypes.ROSEAU);
            return;
        }

        if(nbTreeNeighbor==0){
            newGrid[x][y] = this;
            return;
        }

        double probability = regenerateChances * nbTreeNeighbor;
        if (random.nextDouble() < probability) {

            List<TreeType> candidates = new ArrayList<>();

            for (TreeType type : TreeTypes.ALL) {
                if (type.getEcologicalStage() == 1) {
                    candidates.add(type);
                }
            }

            if (!candidates.isEmpty()) {
                TreeType chosen = candidates.get(random.nextInt(candidates.size()));

                newGrid[x][y] = new Tree(x, y, forest.getCellAt(x,y).getZ(),chosen);
                return;
            }
        }
        newGrid[x][y] = this;
    }

    @Override
    public CellType getType() {
        return CellType.EMPTY;
    }

    @Override
    public double getZ() {return z;}
}
