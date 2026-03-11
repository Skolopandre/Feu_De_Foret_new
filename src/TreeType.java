import java.awt.Color;

public class TreeType {
    private final String name;
    private final int inflammability;
    private final int burnTime;
    private final Color color;
    private final int flameSize;
    private final int ecologicalStage;
    private final int maxFlameTemp;
    private final double diffusionFactor;

    public TreeType(String name, int inflammability, int burnTime, Color color,int flameSize,int ecologicalStage,int maxFlameTemp,double diffusionFactor) {
        this.name = name;
        this.inflammability = inflammability;
        this.burnTime = burnTime;
        this.color = color;
        this.flameSize=flameSize;
        this.ecologicalStage=ecologicalStage;
        this.maxFlameTemp=maxFlameTemp;
        this.diffusionFactor=diffusionFactor;
    }

    public int getInflammability() { return inflammability;}
    public int getBurnTime() { return burnTime;}
    public Color getColor() { return color;}
    public int getFlameSize() { return flameSize;}
    public int getEcologicalStage() { return ecologicalStage;}
    public int getMaxTemp(){ return maxFlameTemp;}
    public String getName(){ return name;}
    public double getDiffusionFactor(){ return diffusionFactor;}

}