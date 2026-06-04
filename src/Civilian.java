class Civilian extends Person {
    Civilian(int id, int startX, int startY) {
        super(id, startX, startY);
    }

    boolean canTakeFireExtinguisher() {
        return false;
    }

    void onPickUpFireExtinguisher() {

    }

    boolean canFightFire() {
        return false;
    }

    void onFightFire(Simulation sim) {

    }

    String getRoleName() {
        return "ГРАЖДАН";
    }
}