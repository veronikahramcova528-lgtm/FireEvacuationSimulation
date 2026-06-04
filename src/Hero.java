class Hero extends Person {
    private boolean hasFireExtinguisher;

    Hero(int id, int startX, int startY) {
        super(id, startX, startY);
        this.hasFireExtinguisher = false;
    }

    void pickUpFireExtinguisher() {
        if (!this.hasFireExtinguisher) {
            this.hasFireExtinguisher = true;
        }
    }

    void useFireExtinguisher() {
        if (this.hasFireExtinguisher) {
            this.hasFireExtinguisher = false;
        }
    }

    boolean canTakeFireExtinguisher() {
        return true;
    }

    void onPickUpFireExtinguisher() {
        this.pickUpFireExtinguisher();
    }

    boolean canFightFire() {
        return this.hasFireExtinguisher;
    }

    void onFightFire(Simulation sim) {
        if (!this.hasFireExtinguisher) {
            return;
        }

        for (int dy = -1; dy <= 1; dy = dy + 1) {
            for (int dx = -1; dx <= 1; dx = dx + 1) {

                boolean isCenter = (dx == 0);
                if (isCenter) {
                    boolean dyIsZero = (dy == 0);
                    if (dyIsZero) {
                        continue;
                    }
                }

                int nx = this.x + dx;
                int ny = this.y + dy;

                boolean inside = sim.building.isInside(nx, ny);
                if (inside) {
                    boolean isFire = sim.building.grid[ny][nx].isFire();
                    if (isFire) {
                        sim.building.grid[ny][nx].type = RoomCell.TYPE_EMPTY;

                        for (int i = 0; i < sim.fires.size(); i = i + 1) {
                            Fire f = sim.fires.get(i);
                            boolean sameX = (f.x == nx);
                            boolean sameY = (f.y == ny);
                            if (sameX) {
                                if (sameY) {
                                    sim.fires.remove(i);
                                    break;
                                }
                            }
                        }

                        this.useFireExtinguisher();
                        System.out.println("Герой " + this.id + " потушил огонь!");
                        return;
                    }
                }
            }
        }
    }

    String getRoleName() {
        if (this.hasFireExtinguisher) {
            return "ГЕРОЙ(огнет)";
        } else {
            return "ГЕРОЙ";
        }
    }
}