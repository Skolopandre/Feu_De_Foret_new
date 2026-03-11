import java.util.*;
import java.util.Random;

public class Tree extends Cell {

    private TreeType type;
    private TreeState state;
    private int temperature;
    private int time_burning;
    private static final Random random = new Random();
    private double evolutionChances;

    public Tree(int x, int y,double z, TreeType type) {
        super(x, y, z);
        this.type = type;
        this.state = TreeState.HEALTHY;
        this.temperature = 0;
        this.time_burning=0;
        this.evolutionChances = 0.00005;
    }

    @Override
    public void update(Forest forest, Cell[][] newGrid) {

        double maxWaterContent = forest.getSoilFieldAt(x,y).getType().getCapacity();
        double waterContent = forest.getSoilFieldAt(x,y).getWaterDepth();
        if(waterContent>=0.5*maxWaterContent){
            newGrid[x][y] = new Empty(x,y,0);
            return;
        }

        if (state == TreeState.ASH) {
            newGrid[x][y] = this;
            return;
        }

        if (state == TreeState.BURNING) {
            this.time_burning ++;
            this.temperature*=1.1;
            propagateFire(forest);



            if(this.time_burning>this.type.getBurnTime()){
                int ash_temp = this.temperature;
                newGrid[x][y] = new Ash(x,y,forest.getCellAt(x,y).getZ(),ash_temp);
                return;
            }
        }

        if (state == TreeState.HEALTHY &&
                temperature >= type.getInflammability()) {

            state = TreeState.BURNING;
            temperature = type.getBurnTime();
        }
        else{
            if(random.nextDouble()<evolutionChances){
                int eco = type.getEcologicalStage();
                List<TreeType> candidates = new ArrayList<>();
                for(TreeType type : TreeTypes.ALL){
                    if(type.getEcologicalStage()==(eco+1)){
                        candidates.add(type);
                    }
                }
                if(!candidates.isEmpty()){
                    TreeType chosen = candidates.get(random.nextInt(candidates.size()));
                    newGrid[x][y] = new Tree(x,y,forest.getCellAt(x,y).getZ(),chosen);
                    return;
                }
            }
        }

        if (state == TreeState.HEALTHY &&
                temperature <= type.getInflammability()) {

            int currEco = type.getEcologicalStage();
            TreeType currType = getTreeType();

            int similarEco = 0;
            int similarType = 0;

            for (Cell neighbor : forest.getNeighbors(x, y)) {
                if (neighbor instanceof Tree t) {
                    TreeType neighborType = t.getTreeType();

                    if (neighborType.getEcologicalStage() == currEco) {
                        similarEco++;

                        if (neighborType.equals(currType)) {
                            similarType++;
                        }
                    }
                }
            }

            if (similarType +2< similarEco) {
                newGrid[x][y] = new Empty(x, y,0);
                return;
            }
        }

        newGrid[x][y] = this;
    }

    /**
     * Propagation du feu via BFS limité par flameSize du TreeType
     */
    private void propagateFire(Forest forest) {

        int baseFlameSize = type.getFlameSize();

        WindCell wind = forest.getWindAt(x, y);

        Set<Cell> visited = new HashSet<>();
        List<Cell> frontier = new ArrayList<>();

        frontier.add(this);
        visited.add(this);

        for (int distance = 1; distance <= baseFlameSize + 1; distance++) {

            List<Cell> nextFrontier = new ArrayList<>();

            for (Cell current : frontier) {

                for (Cell neighbor :
                        forest.getNeighbors(current.getX(), current.getY())) {

                    if (neighbor == null || visited.contains(neighbor))
                        continue;

                    visited.add(neighbor);

                    if (neighbor instanceof Tree t) {

                        // 🔥 Calcul direction propagation
                        double vx = neighbor.getX() - x;
                        double vy = neighbor.getY() - y;

                        double length = Math.sqrt(vx * vx + vy * vy);
                        if (length == 0) continue;

                        vx /= length;
                        vy /= length;

                        // 🌬 Alignement vent
                        double dot = vx * wind.getDx() + vy * wind.getDy();

                        // Influence sur portée
                        int windBonus = 0;

                        if (dot > 0.5) {            // bien aligné
                            windBonus = 1;
                        } else if (dot < -0.5) {    // contre le vent
                            windBonus = -1;
                        }

                        int effectiveRange = baseFlameSize + windBonus;

                        if (distance <= effectiveRange) {
                            int temp=0;
                            if(t.getState()==TreeState.BURNING){
                                temp=t.temperature;
                            }


                            double heat = Math.max(
                                    1,
                                    type.getDiffusionFactor()*t.temperature/3 / (distance + 1)
                            );

                            t.increaseTemp((int)heat);
                            nextFrontier.add(neighbor);
                        }
                    }
                }
            }

            frontier = nextFrontier;
        }
    }

    public void increaseTemp(int amount) {
        this.temperature =Math.min(this.temperature+amount,this.getTreeType().getMaxTemp());
    }

    public TreeType getTreeType() {
        return type;
    }

    public TreeState getState() {
        return state;
    }

    public void setState(TreeState state) {
        this.state = state;
    }

    @Override
    public CellType getType() {
        return CellType.TREE;
    }

    public int getTemperature(){return temperature;}
    @Override
    public double getZ() {return z;}
}