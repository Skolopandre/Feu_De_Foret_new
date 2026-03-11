import java.awt.Color;

public class CloudType {

    private final String name;
    private final double baseWaterContent;
    private final Color color;

    public CloudType(String name, double baseWaterContent, Color color){
        this.name = name;
        this.baseWaterContent = baseWaterContent;
        this.color = color;
    }

    public String getName(){ return name; }
    public double getBaseWaterContent(){ return baseWaterContent; }
    public Color getColor(){ return color; }
}