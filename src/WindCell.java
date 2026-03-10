public class WindCell {

    private double dx;
    private double dy;

    public WindCell(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public double getDx() { return dx; }
    public double getDy() { return dy; }

    public void set(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void normalize() {
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }
    }


}