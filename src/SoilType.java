import java.awt.Color;

public class SoilType {
    private final String name;
    private final double permeability;
    private final double capacity;
    private final Color color;
    private final double fertility;

    public SoilType(String name, double permeability, double capacity, Color color, double fertility){
        this.name=name;
        this.permeability=permeability;
        this.capacity=capacity;
        this.color=color;
        this.fertility=fertility;
    }
    public Color getColor(){return color;}
    public double getPermeability(){return permeability;}
    public double getCapacity(){return capacity;}
}
