class RoomCell {

    static final int TYPE_EMPTY = 0;
    static final int TYPE_WALL = 1;
    static final int TYPE_EXIT = 2;
    static final int TYPE_WINDOW = 3;
    static final int TYPE_FIRE = 4;
    static final int TYPE_SMOKE = 5;

    int type;
    boolean hasFireExtinguisher;
    boolean hasAlarmButton;
    boolean hasExitSign;

    RoomCell(int type) {
        this.type = type;
        this.hasFireExtinguisher = false;
        this.hasAlarmButton = false;
        this.hasExitSign = false;
    }

    boolean isWalkable() {
        if (type == TYPE_EMPTY) {
            return true;
        }
        if (type == TYPE_EXIT) {
            return true;
        }
        if (type == TYPE_WINDOW) {
            return true;
        }
        if (type == TYPE_SMOKE) {
            return true;
        }
        return false;
    }

    boolean isFire() {
        if (type == TYPE_FIRE) {
            return true;
        }
        return false;
    }

    boolean isSmoke() {
        if (type == TYPE_SMOKE) {
            return true;
        }
        return false;
    }

    boolean isExit() {
        if (type == TYPE_EXIT) {
            return true;
        }
        return false;
    }

    boolean isWall() {
        if (type == TYPE_WALL) {
            return true;
        }
        return false;
    }

    boolean isEmpty() {
        if (type == TYPE_EMPTY) {
            return true;
        }
        return false;
    }

    boolean isWindow() {
        if (type == TYPE_WINDOW) {
            return true;
        }
        return false;
    }

    char getSymbol() {
        if (hasFireExtinguisher) {
            return 'E';
        }
        if (hasAlarmButton) {
            return 'B';
        }
        if (hasExitSign) {
            return 'S';
        }
        if (type == TYPE_EMPTY) {
            return '.';
        }
        if (type == TYPE_WALL) {
            return '#';
        }
        if (type == TYPE_EXIT) {
            return 'D';
        }
        if (type == TYPE_WINDOW) {
            return 'W';
        }
        if (type == TYPE_FIRE) {
            return '*';
        }
        if (type == TYPE_SMOKE) {
            return '~';
        }
        return '?';
    }

    public String toString() {
        String result = "RoomCell[";

        if (type == TYPE_EMPTY) {
            result = result + "EMPTY";
        } else {
            if (type == TYPE_WALL) {
                result = result + "WALL";
            } else {
                if (type == TYPE_EXIT) {
                    result = result + "EXIT";
                } else {
                    if (type == TYPE_WINDOW) {
                        result = result + "WINDOW";
                    } else {
                        if (type == TYPE_FIRE) {
                            result = result + "FIRE";
                        } else {
                            if (type == TYPE_SMOKE) {
                                result = result + "SMOKE";
                            } else {
                                result = result + "UNKNOWN";
                            }
                        }
                    }
                }
            }
        }

        if (hasFireExtinguisher) {
            result = result + ",EXTINGUISHER";
        }
        if (hasAlarmButton) {
            result = result + ",ALARM";
        }
        if (hasExitSign) {
            result = result + ",EXIT_SIGN";
        }
        result = result + "]";
        return result;
    }
}