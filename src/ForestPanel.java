import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


public class ForestPanel extends JPanel {

    private final Forest forest;
    private final int cellSize = 2;
    private final int bufferRadius = 3;
    private boolean isMousePressed = false;

    private boolean showSubsoil = false;
    private boolean showWaterReserves = false;
    private JCheckBox subsoilCheckbox;
    private JCheckBox waterReservesCheckbox;
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    public ForestPanel(Forest forest) {
        this.forest = forest;
        setLayout(new BorderLayout()); // Layout pour organiser les composants

        // Panneau pour les boutons (en haut)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subsoilCheckbox = new JCheckBox("Afficher le sous-sol");
        subsoilCheckbox.addItemListener(e -> {
            showSubsoil = e.getStateChange() == ItemEvent.SELECTED;
            repaint();
        });

        waterReservesCheckbox = new JCheckBox("Afficher les réserves d'eau souterraines");
        waterReservesCheckbox.addItemListener(e -> {
            showWaterReserves = e.getStateChange() == ItemEvent.SELECTED;
            repaint();
        });

        buttonPanel.add(subsoilCheckbox);
        buttonPanel.add(waterReservesCheckbox);
        add(buttonPanel, BorderLayout.NORTH);


        JPanel forestDrawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
                drawSurfaceWater(g);
                drawClouds(g);
                if (showSubsoil) {
                    drawSubsoil(g);
                }
                if (showWaterReserves) {
                    drawWaterReserves(g);
                }
                drawContourLines(g);
                drawParticles(g);
            }
        };

        forestDrawPanel.setPreferredSize(new Dimension(forest.getWidth() * cellSize, forest.getHeight() * cellSize));
        add(forestDrawPanel, BorderLayout.CENTER);

        // Ajout des écouteurs de souris sur le panneau de dessin
        forestDrawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showCellInfo(e.getX(), e.getY());
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isMousePressed = true;
                    int x = e.getX() / cellSize;
                    int y = e.getY() / cellSize;
                    if (forest.getGrid()[x][y] instanceof Ash) {
                        AshtoTree(x, y);
                    } else {
                        igniteCells(x, y);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isMousePressed = false;
            }
        });

        forestDrawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isMousePressed) {
                    int x = e.getX() / cellSize;
                    int y = e.getY() / cellSize;
                    if (forest.getGrid()[x][y] instanceof Ash) {
                        AshtoTree(x, y);
                    } else {
                        igniteCells(x, y);
                    }
                }
            }
        });
    }

    private void AshtoTree(int mouseX, int mouseY){

        int clickedX = mouseX;
        int clickedY = mouseY;

        for(int dx = -bufferRadius; dx <= bufferRadius; dx++){
            for(int dy = -bufferRadius; dy <= bufferRadius; dy++){

                int nx = clickedX + dx;
                int ny = clickedY + dy;

                // Vérification des bornes
                if (nx >= 0 && nx < forest.getWidth() &&
                        ny >= 0 && ny < forest.getHeight()) {

                    if(dx*dx + dy*dy <= bufferRadius*bufferRadius){

                        Cell cell = forest.getGrid()[nx][ny];

                        if(cell instanceof Ash ash){
                            ash.forceRegenerate(forest,forest.getGrid());
                        }
                    }
                }
            }
        }

        repaint();
    }

    private void igniteCells(int mouseX, int mouseY) {
        int clickedX = mouseX ;
        int clickedY = mouseY ;

        for (int dx = -bufferRadius; dx <= bufferRadius; dx++) {
            for (int dy = -bufferRadius; dy <= bufferRadius; dy++) {
                int nx = clickedX + dx;
                int ny = clickedY + dy;

                if (nx >= 0 && nx < forest.getWidth() &&
                        ny >= 0 && ny < forest.getHeight()) {

                    if (dx*dx + dy*dy <= bufferRadius*bufferRadius) {
                        Cell cell = forest.getGrid()[nx][ny];
                        if (cell instanceof Tree tree && tree.getState() == TreeState.HEALTHY) {
                            tree.setState(TreeState.BURNING);
                            tree.increaseTemp(100);
                        }
                    }
                }
            }
        }

        repaint();
    }

    public void step() {
        Cell[][] grid = forest.getGrid();
        SoilCell[][] soilField = forest.getSoilField();
        CloudCell[][] cloudField = forest.getCloudField();

        int width = grid.length;
        int height = grid[0].length;
        SoilCell[][] newSoilField = new SoilCell[width][height];
        Cell[][] newGrid = new Cell[width][height];
        CloudCell[][] newCloudField = new CloudCell[width][height];


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                soilField[x][y].updateWater(forest, newSoilField);
                grid[x][y].update(forest, newGrid);
                if(cloudField[x][y]!=null){
                    cloudField[x][y].update(forest, newCloudField);
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                if (newSoilField[x][y] instanceof Soil) {

                    Soil soil = (Soil) newSoilField[x][y];

                    soil.redistributeSurplus(
                            forest,
                            newSoilField
                    );
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                soilField[x][y] = newSoilField[x][y];
                grid[x][y] = newGrid[x][y];
                cloudField[x][y] = newCloudField[x][y];
            }
        }

        spawnParticles();
        updateParticles();
    }

    private void drawClouds(Graphics g) {
        CloudCell[][] cloudField = forest.getCloudField();
        for (int x = 0; x < forest.getWidth(); x++) {
            for (int y = 0; y < forest.getHeight(); y++) {
                CloudCell cloudCell = cloudField[x][y];
                if (cloudCell != null) {
                    Color cloudColor = cloudCell.getType().getColor();
                    g.setColor(cloudColor);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    private void drawSubsoil(Graphics g) {
        SoilCell[][] soilField = forest.getSoilField();
        for (int x = 0; x < forest.getWidth(); x++) {
            for (int y = 0; y < forest.getHeight(); y++) {
                SoilCell soilCell = soilField[x][y];
                if (soilCell != null) {
                    Color soilColor = soilCell.getType().getColor();
                    g.setColor(soilColor);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    private void drawWaterReserves(Graphics g) {
        SoilCell[][] soilField = forest.getSoilField();
        for (int x = 0; x < forest.getWidth(); x++) {
            for (int y = 0; y < forest.getHeight(); y++) {
                SoilCell soilCell = soilField[x][y];
                if (soilCell instanceof Soil) {
                    Soil soil = (Soil) soilCell;
                    double waterContent = soil.getWaterContent();
                    int alpha = 255;
                    int blueValue = Math.min(250, Math.max(100,100 + (int)(waterContent * 155)));
                    Color waterColor = new Color(100, 100, blueValue, alpha);
                    g.setColor(waterColor);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    private void drawSurfaceWater(Graphics g) {
        SoilCell[][] soilField = forest.getSoilField();
        for (int x=0;x<forest.getWidth();x++){
            for(int y=0;y<forest.getHeight();y++){
                SoilCell soilCell = soilField[x][y];
                double waterLevel = soilCell.getWaterDepth();
                int alpha = Math.min(255, Math.max(50, (int)(waterLevel * 2000)));
                int blueValue = Math.min(255, Math.max(100, 100 + (int)(waterLevel * 155)));
                Color waterColor = new Color(100, 100, blueValue, alpha);
                g.setColor(waterColor);
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawContourLines(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(50,50,50,80));
        g2.setStroke(new BasicStroke(0.5f));

        int step = 10;

        int w = forest.getWidth();
        int h = forest.getHeight();

        for (double level = 0; level < 200; level += step) {

            for (int x = 0; x < w - 1; x++) {
                for (int y = 0; y < h - 1; y++) {

                    double a = forest.getElevationAt(x, y);
                    double b = forest.getElevationAt(x + 1, y);
                    double c = forest.getElevationAt(x + 1, y + 1);
                    double d = forest.getElevationAt(x, y + 1);

                    int state = 0;

                    if (a > level) state |= 1;
                    if (b > level) state |= 2;
                    if (c > level) state |= 4;
                    if (d > level) state |= 8;

                    if (state == 0 || state == 15) continue;

                    double px = x * cellSize;
                    double py = y * cellSize;

                    double half = cellSize / 2.0;

                    double ax = px;
                    double ay = py;

                    double bx = px + cellSize;
                    double by = py;

                    double cx = px + cellSize;
                    double cy = py + cellSize;

                    double dx = px;
                    double dy = py + cellSize;

                    double mx1 = px + half;
                    double my1 = py;

                    double mx2 = px + cellSize;
                    double my2 = py + half;

                    double mx3 = px + half;
                    double my3 = py + cellSize;

                    double mx4 = px;
                    double my4 = py + half;

                    switch (state) {

                        case 1,14 -> g2.drawLine((int)mx4,(int)my4,(int)mx1,(int)my1);

                        case 2,13 -> g2.drawLine((int)mx1,(int)my1,(int)mx2,(int)my2);

                        case 3,12 -> g2.drawLine((int)mx4,(int)my4,(int)mx2,(int)my2);

                        case 4,11 -> g2.drawLine((int)mx2,(int)my2,(int)mx3,(int)my3);

                        case 5 -> {
                            g2.drawLine((int)mx4,(int)my4,(int)mx1,(int)my1);
                            g2.drawLine((int)mx2,(int)my2,(int)mx3,(int)my3);
                        }

                        case 6,9 -> g2.drawLine((int)mx1,(int)my1,(int)mx3,(int)my3);

                        case 7,8 -> g2.drawLine((int)mx4,(int)my4,(int)mx3,(int)my3);

                        case 10 -> {
                            g2.drawLine((int)mx1,(int)my1,(int)mx2,(int)my2);
                            g2.drawLine((int)mx3,(int)my3,(int)mx4,(int)my4);
                        }
                    }
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
        Cell[][] grid = forest.getGrid();
        for (int x = 0; x < forest.getWidth(); x++) {
            for (int y = 0; y < forest.getHeight(); y++) {
                Cell cell = grid[x][y];
                Color color = Color.BLACK;

                switch (cell.getType()) {
                    case TREE -> {
                        Tree tree = (Tree) cell;
                        int green=Math.max(170-tree.getTemperature()/5,0);
                        color = switch (tree.getState()) {
                            case HEALTHY -> tree.getTreeType().getColor();
                            case BURNING -> new Color(255,green,0);
                            case ASH -> new Color(green,50,50);
                        };
                    }
                    case ASH -> color = new Color(85, 56, 29,180);
                    case EMPTY -> color = new Color(85, 56, 29);
                }

                g.setColor(color);
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawParticles(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(100, 100, 100, 255)); // alpha léger

        for (Particle p : particles) {
            int px = (int)(p.getDrawX() * cellSize);
            int py = (int)(p.getDrawY() * cellSize);
            g2.fillOval(px, py, 2, 2);
        }
    }

    private void spawnParticles() {
        int width = forest.getWidth();
        int height = forest.getHeight();

        for (int i = 0; i < 5; i++) { // 5 particules par tick
            int xCell = random.nextInt(width);
            int yCell = random.nextInt(height);

            WindCell wind = forest.getWindAt(xCell, yCell); // direction locale du vent

            double speed = 0.1 + random.nextDouble() * 0.2;
            double vx = wind.getDx() * speed*5;
            double vy = wind.getDy() * speed*5;

            double px = xCell + random.nextDouble();
            double py = yCell + random.nextDouble();

            particles.add(new Particle(px, py, vx, vy, 20 + random.nextInt(30)));
        }
    }

    private void updateParticles() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (!p.isAlive()) it.remove();
        }
    }

    private void showCellInfo(int mouseX, int mouseY) {

        int cellX = mouseX / cellSize;
        int cellY = mouseY / cellSize;

        if (!forest.isInside(cellX,cellY)) {
            return;
        }

        Cell cell = forest.getGrid()[cellX][cellY];

        if (cell instanceof Tree tree) {
            showTreePopup(tree,cellX,cellY);
        }
    }

    private void showTreePopup(Tree tree,int x,int y) {

        String message =
                "Type : " + tree.getTreeType().getName() + "\n" +
                        "État : " + tree.getState() + "\n" +
                        "Température : " + tree.getTemperature() + "\n" +
                        "Stade écologique : " + tree.getTreeType().getEcologicalStage() + "\n" +
                        "élévation : " +  (int)forest.getElevationAt(x,y);


        JOptionPane.showMessageDialog(
                this,
                message,
                "Informations arbre",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
