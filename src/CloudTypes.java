import java.awt.*;
import java.util.List;

public class CloudTypes {
    public static final CloudType CUMULONIMBUS = new CloudType("CUMULONIMBUS",5,new Color(50,50,50, 158));
    public static final CloudType CIRROSTRATUS = new CloudType("CIRROSTRATUS",3,new Color(78, 78, 78, 119));
    public static final CloudType STRATUS = new CloudType("STRATUS",2,new Color(191, 191, 191, 79));
    public static final List<CloudType> ALL = List.of(CUMULONIMBUS,CIRROSTRATUS,STRATUS);
}
