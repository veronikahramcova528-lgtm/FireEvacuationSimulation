abstract class Person {

    protected static final int SMOKE_DAMAGE_PER_STEP = 2;
    protected static final int MAX_SMOKE_EXPOSURE = 25;
    protected static final int MAX_FIRE_EXPOSURE = 18;
    protected static final int FIRE_DAMAGE_PER_STEP = 3;

    int id;
    int x;
    int y;
    boolean isAlive;
    int smokeExposure;
    int fireExposure;
    boolean isFollowingExitSign;
    boolean isEvacuating;
    boolean hasPressedAlarm;
    int evacuationStartTime;

    Person(int id, int startX, int startY) {
        this.id = id;
        this.x = startX;
        this.y = startY;
        this.isAlive = true;
        this.smokeExposure = 0;
        this.fireExposure = 0;
        this.isFollowingExitSign = false;
        this.isEvacuating = false;
        this.hasPressedAlarm = false;
        this.evacuationStartTime = -1;
    }

    void increaseSmokeExposure() {
        if (!isAlive) {
            return;
        }
        this.smokeExposure = this.smokeExposure + SMOKE_DAMAGE_PER_STEP;
        if (this.smokeExposure >= MAX_SMOKE_EXPOSURE) {
            this.isAlive = false;
        }
    }

    void increaseFireExposure() {
        if (!isAlive) {
            return;
        }
        this.fireExposure = this.fireExposure + FIRE_DAMAGE_PER_STEP;
        if (this.fireExposure >= MAX_FIRE_EXPOSURE) {
            this.isAlive = false;
        }
    }

    double getHealthPercentage() {
        double smokeHealth = 1.0 - (double) this.smokeExposure / (double) MAX_SMOKE_EXPOSURE;
        double fireHealth = 1.0 - (double) this.fireExposure / (double) MAX_FIRE_EXPOSURE;

        if (smokeHealth < 0.0) {
            smokeHealth = 0.0;
        }
        if (fireHealth < 0.0) {
            fireHealth = 0.0;
        }

        if (smokeHealth <= fireHealth) {
            return smokeHealth * 100.0;
        } else {
            return fireHealth * 100.0;
        }
    }

    abstract boolean canTakeFireExtinguisher();
    abstract void onPickUpFireExtinguisher();
    abstract boolean canFightFire();
    abstract void onFightFire(Simulation sim);
    abstract String getRoleName();

    boolean isHero() {
        return (this instanceof Hero);
    }
}