class Fire {
    private static final int INITIAL_INTENSITY = 5;
    private static final int MAX_INTENSITY = 10;

    final int x;
    final int y;
    private int intensity;

    Fire(int x, int y) {
        this.x = x;
        this.y = y;
        this.intensity = INITIAL_INTENSITY;
    }

    void spread() {
        if (intensity < MAX_INTENSITY) {
            intensity = intensity + 1;
        }
    }

    int getIntensity() {
        return intensity;
    }

    public String toString() {
        return "Fire at (" + x + "," + y + ") | Intensity: " + intensity + "/" + MAX_INTENSITY;
    }
}