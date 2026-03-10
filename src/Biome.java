import java.util.LinkedList;
import java.util.Random;

public class Biome {

    private final CellType type;
    private final int width;
    private final int height;
    private final double spreadProbability;
    private final Random random;

    public Biome(CellType type,
                 int width,
                 int height,
                 double spreadProbability,
                 Random random) {

        this.type = type;
        this.width = width;
        this.height = height;
        this.spreadProbability = spreadProbability;
        this.random = random;
    }


    private TreeType getRandomTreeType() {
        return TreeTypes.ALL.get(random.nextInt(TreeTypes.ALL.size()));
    }

    //Génération sans type imposé
    public void generateFrom(Cell[][] grid, int startX, int startY,Forest forest) {
        TreeType chosenType = null;

        if (type == CellType.TREE) {
            chosenType = getRandomTreeType();
        }

        generateInternal(grid, startX, startY, chosenType, forest);
    }

    // Génération avec type imposé
    public void generateFrom(Cell[][] grid,
                             int startX,
                             int startY,
                             TreeType forcedType,
                             Forest forest) {

        generateInternal(grid, startX, startY, forcedType,forest);
    }

    private void generateInternal(Cell[][] grid,
                                  int startX,
                                  int startY,
                                  TreeType treeType,
                                  Forest forest) {

        LinkedList<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});

        while (!queue.isEmpty()) {

            int[] pos = queue.removeFirst();
            int x = pos[0];
            int y = pos[1];

            if (x < 0 || x >= width || y < 0 || y >= height)
                continue;

            if (grid[x][y] != null)
                continue;

            switch (type) {
                case TREE -> grid[x][y] = new Tree(x, y,0, treeType);
                case WATER -> grid[x][y] = new Water(x, y,0);
                case EMPTY -> grid[x][y] = new Empty(x, y,0);
            }

            int[][] neighbors = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] n : neighbors) {
                if (random.nextDouble() < spreadProbability) {
                    queue.add(new int[]{x + n[0], y + n[1]});
                }
            }
        }
    }
}