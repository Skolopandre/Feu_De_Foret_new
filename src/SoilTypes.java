import java.awt.Color;
import java.util.List;

public class SoilTypes {
    public static final SoilType SABLE = new SoilType("SABLE",0.08,0.6,new Color(255, 204, 0,250),0.6);
    public static final SoilType LIMON = new SoilType("LIMON",0.04,0.4,new Color(103, 120, 52,250),1.2);
    public static final SoilType ARGILE = new SoilType("ARGILE",0.01,0.2,new Color(94, 81, 55,250),0.3);
    public static final SoilType ROCHE_GRES = new SoilType("ROCHE_GRES",0.02,0.01,new Color(124, 120, 120,250),0);
    public static final SoilType ROCHE_CALCAIRE = new SoilType("ROCHE_CALCAIRE",0.08,0.8,new Color(191, 191, 191,250),0.3);

    public static final List<SoilType> ALL = List.of(SABLE,LIMON,ARGILE,ROCHE_GRES,ROCHE_CALCAIRE);
    public static final List<SoilType> ROCHE = List.of(ROCHE_GRES,ROCHE_CALCAIRE);
    public static final List<SoilType> SOL = List.of(SABLE,LIMON,ARGILE);
}
