import java.awt.Color;
import java.util.List;

public class TreeTypes {
    public static final TreeType RESINOUS = new TreeType("RESINOUS",315, 10, new Color(34, 139, 34),3,2,700,0.2);
    public static final TreeType OAK      = new TreeType("OAK",325, 50, new Color(00, 100, 0),1,4,1000,0.1);
    public static final TreeType BOULEAU  = new TreeType("BOULEAU",275, 9, new Color(130, 150, 50),2,1,900,0.3);
    public static final TreeType HETRE  = new TreeType("HETRE",305, 9, new Color(130, 150, 50),1,3,900,0.3);
    public static final TreeType DOUGLAS  = new TreeType("DOUGLAS",295, 9, new Color(40, 120, 50),3,2,800,0.4);
    public static final TreeType PIN_MARITIME  = new TreeType("PIN_MARITIME",255, 11, new Color(20, 110, 45),3,1,800,0.4);
    public static final TreeType FRENE  = new TreeType("FRENE",315, 17, new Color(100, 180, 80),2,3,900,0.2);
    public static final TreeType CHATAIGNIER  = new TreeType("CHATAIGNIER",285, 16, new Color(80, 150, 70),1,2,900,0.25);
    public static final TreeType CHARME  = new TreeType("CHARME",345, 40, new Color(120, 190, 110),1,4,1000,0.15);
    public static final TreeType ROSEAU = new TreeType("ROSEAU",100,1,new Color(133, 22, 22, 255),1,10,300,0.5);


    public static final List<TreeType> ALL = List.of(RESINOUS, OAK, BOULEAU,HETRE,DOUGLAS,PIN_MARITIME,FRENE,CHATAIGNIER,CHARME);
}