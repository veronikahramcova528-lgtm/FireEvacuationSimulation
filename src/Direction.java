final class Direction {

    static final int UP = 0;
    static final int RIGHT = 1;
    static final int DOWN = 2;
    static final int LEFT = 3;
    static final int NONE = -1;

    static final int[] ALL = {UP, RIGHT, DOWN, LEFT};

    private Direction() {
    }

    static int getDx(int direction) {
        if (direction == LEFT) {
            return -1;
        }
        if (direction == RIGHT) {
            return 1;
        }
        return 0;
    }

    static int getDy(int direction) {
        if (direction == UP) {
            return -1;
        }
        if (direction == DOWN) {
            return 1;
        }
        return 0;
    }
}