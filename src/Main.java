import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static void main(String[] args) {
        Forest forest = new Forest(500, 450);
        forest.initializeWorld();

        ForestPanel panel = new ForestPanel(forest);

        JFrame frame = new JFrame("Simulation de forêt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.step();
                panel.repaint();
            }
        });
        timer.start();
    }

    private static void step(Forest forest) {
        Cell[][] grid = forest.getGrid();
        int width = grid.length;
        int height = grid[0].length;
        Cell[][] newGrid = new Cell[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].update(forest, newGrid);
            }
        }

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                grid[x][y] = newGrid[x][y];
    }
}