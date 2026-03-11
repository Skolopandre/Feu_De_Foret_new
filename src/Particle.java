public class Particle {
    private double x, y;       // Position flottante pour animation fluide
    private double vx, vy;     // Vitesse selon le vent
    private int lifetime;      // Durée de vie en ticks

    public Particle(double x, double y, double vx, double vy, int lifetime) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lifetime = lifetime;
    }

    public void update() {
        x += vx;
        y += vy;
        lifetime--;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }

    public int getDrawX() {
        return (int)x;
    }

    public int getDrawY() {
        return (int)y;
    }
}