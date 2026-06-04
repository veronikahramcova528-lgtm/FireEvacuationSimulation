import java.util.Random;
import java.util.ArrayList;

class Simulation {
    private static final int TIME_BETWEEN_PEOPLE_MIN = 5;
    private static final int TIME_BETWEEN_PEOPLE_MAX = 20;
    private static final int HERO_SPAWN_CHANCE_PERCENT = 15;
    private static final int FIRE_SPREAD_FIRE_CHANCE_PERCENT = 15;
    private static final int FIRE_SPREAD_SMOKE_CHANCE_PERCENT = 20;
    private static final int SPREAD_INTERVAL_STEPS = 4;
    private static final int FORCED_FIRE_STEP = 80;
    private static final int FIRE_START_THRESHOLD_STEP_1 = 30;
    private static final int FIRE_START_CHANCE_1 = 15;
    private static final int FIRE_START_THRESHOLD_STEP_2 = 40;
    private static final int FIRE_START_CHANCE_2 = 30;
    private static final int FIRE_START_THRESHOLD_STEP_3 = 50;
    private static final int FIRE_START_CHANCE_3 = 50;
    private static final int FIRE_START_THRESHOLD_STEP_4 = 60;
    private static final int FIRE_START_CHANCE_4 = 80;
    private static final int RANDOM_RANGE_100 = 100;
    private static final int ANIMATION_DELAY_MS = 200;

    Building building;
    ArrayList<Person> people;
    ArrayList<Fire> fires;
    boolean fireStarted;
    int timeStep;
    int alarmPressed;
    Random random;
    int nextPersonId;
    int timeUntilNextPerson;
    StatisticsWindow window;
    int totalPeopleEver;
    int totalHeroesEver;
    int totalCiviliansEver;
    int spreadCount;
    int fireStartTime;
    int evacuatedCount;
    String buildingName;
    int heroesPressedAlarmTotal;
    int civiliansPressedAlarmTotal;

    Simulation(int width, int height) {
        this.building = new Building(width, height);
        this.people = new ArrayList<>();
        this.fires = new ArrayList<>();
        this.fireStarted = false;
        this.timeStep = 0;
        this.alarmPressed = 0;
        this.random = new Random();
        this.nextPersonId = 1;
        this.timeUntilNextPerson = this.random.nextInt(TIME_BETWEEN_PEOPLE_MAX - TIME_BETWEEN_PEOPLE_MIN + 1) + TIME_BETWEEN_PEOPLE_MIN;
        this.window = new StatisticsWindow();
        this.totalPeopleEver = 0;
        this.totalHeroesEver = 0;
        this.totalCiviliansEver = 0;
        this.spreadCount = 0;
        this.fireStartTime = 0;
        this.evacuatedCount = 0;
        this.heroesPressedAlarmTotal = 0;
        this.civiliansPressedAlarmTotal = 0;

        String[] names = {"Торговый центр", "Офис", "Супермаркет", "Кинотеатр", "Ресторан", "Больница", "Школа", "Университет"};
        this.buildingName = names[this.random.nextInt(names.length)];
    }

    void addPerson(boolean isHero) {
        ArrayList<Point> walkableSpots = new ArrayList<>();
        for (int i = 0; i < this.building.height; i++) {
            for (int j = 0; j < this.building.width; j++) {
                if (this.building.grid[i][j].isEmpty()) {
                    walkableSpots.add(new Point(j, i));
                }
            }
        }
        if (walkableSpots.isEmpty()) {
            return;
        }
        int index = this.random.nextInt(walkableSpots.size());
        Point p = walkableSpots.get(index);
        Person person;
        if (isHero) {
            person = new Hero(this.nextPersonId, p.x, p.y);
        } else {
            person = new Civilian(this.nextPersonId, p.x, p.y);
        }
        this.people.add(person);
        this.nextPersonId++;
        this.totalPeopleEver++;
        if (isHero) {
            this.totalHeroesEver++;
        } else {
            this.totalCiviliansEver++;
        }
        System.out.println("Создан " + person.getRoleName() + " " + person.id + " на позиции (" + p.x + "," + p.y + ")");
    }

    void startFire() {
        if (!this.fireStarted) {
            ArrayList<Point> spots = new ArrayList<>();
            for (int i = 0; i < this.building.height; i++) {
                for (int j = 0; j < this.building.width; j++) {
                    if (this.building.grid[i][j].type == RoomCell.TYPE_EMPTY) {
                        spots.add(new Point(j, i));
                    }
                }
            }
            if (!spots.isEmpty()) {
                int index = this.random.nextInt(spots.size());
                Point p = spots.get(index);
                Fire fire = new Fire(p.x, p.y);
                this.fires.add(fire);
                this.building.setFire(p.x, p.y);
                this.fireStarted = true;
                this.fireStartTime = this.timeStep;
                System.out.println("ПОЖАР НАЧАЛСЯ!");
            }
        }
    }

    void spreadFire() {
        this.spreadCount++;
        ArrayList<Fire> newFires = new ArrayList<>();
        for (Fire fire : this.fires) {
            fire.spread();
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }
                    int nx = fire.x + dx;
                    int ny = fire.y + dy;
                    if (this.building.isInside(nx, ny) && this.building.grid[ny][nx].type == RoomCell.TYPE_EMPTY) {
                        if (this.random.nextInt(RANDOM_RANGE_100) < FIRE_SPREAD_FIRE_CHANCE_PERCENT) {
                            this.building.setFire(nx, ny);
                            newFires.add(new Fire(nx, ny));
                        } else if (this.random.nextInt(RANDOM_RANGE_100) < FIRE_SPREAD_SMOKE_CHANCE_PERCENT) {
                            this.building.setSmoke(nx, ny);
                        }
                    }
                }
            }
        }
        this.fires.addAll(newFires);
    }

    Point findNearestExit(int x, int y) {
        Point nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (Point exit : this.building.exits) {
            int d = Math.abs(x - exit.x) + Math.abs(y - exit.y);
            if (d < minDist) {
                minDist = d;
                nearest = exit;
            }
        }
        return nearest;
    }

    Point findNearestButton(int x, int y) {
        Point nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (Point btn : this.building.alarmButtons) {
            if (this.building.grid[btn.y][btn.x].hasAlarmButton) {
                int d = Math.abs(x - btn.x) + Math.abs(y - btn.y);
                if (d < minDist) {
                    minDist = d;
                    nearest = btn;
                }
            }
        }
        return nearest;
    }

    boolean hasUnpressedButtons() {
        for (Point btn : this.building.alarmButtons) {
            if (this.building.grid[btn.y][btn.x].hasAlarmButton) {
                return true;
            }
        }
        return false;
    }

    void movePerson(Person p) {
        if (!this.fireStarted) {
            int dir = Direction.ALL[this.random.nextInt(4)];
            int nx = p.x + Direction.getDx(dir);
            int ny = p.y + Direction.getDy(dir);
            if (this.building.isWalkable(nx, ny)) {
                p.x = nx;
                p.y = ny;
            }
            return;
        }
        if (this.hasUnpressedButtons() && !p.hasPressedAlarm) {
            Point targetButton = this.findNearestButton(p.x, p.y);
            if (targetButton != null) {
                if (p.x == targetButton.x && p.y == targetButton.y) {
                    return;
                }
                int bestDir = Direction.NONE;
                int bestDist = Integer.MAX_VALUE;
                for (int d : Direction.ALL) {
                    int nx = p.x + Direction.getDx(d);
                    int ny = p.y + Direction.getDy(d);
                    if (this.building.isWalkable(nx, ny)) {
                        int dist = Math.abs(nx - targetButton.x) + Math.abs(ny - targetButton.y);
                        if (dist < bestDist) {
                            bestDist = dist;
                            bestDir = d;
                        }
                    }
                }
                if (bestDir != Direction.NONE) {
                    p.x += Direction.getDx(bestDir);
                    p.y += Direction.getDy(bestDir);
                }
                return;
            }
        }
        Point targetExit = this.findNearestExit(p.x, p.y);
        if (targetExit == null) {
            return;
        }
        if (p.x == targetExit.x && p.y == targetExit.y) {
            return;
        }
        int bestDir = Direction.NONE;
        int bestDist = Integer.MAX_VALUE;
        for (int d : Direction.ALL) {
            int nx = p.x + Direction.getDx(d);
            int ny = p.y + Direction.getDy(d);
            if (this.building.isWalkable(nx, ny)) {
                int dist = Math.abs(nx - targetExit.x) + Math.abs(ny - targetExit.y);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestDir = d;
                }
            }
        }
        if (bestDir != Direction.NONE) {
            p.x += Direction.getDx(bestDir);
            p.y += Direction.getDy(bestDir);
        }
    }

    void updatePeople() {
        ArrayList<Person> toRemove = new ArrayList<>();
        for (Person p : this.people) {
            if (!p.isAlive) {
                toRemove.add(p);
                continue;
            }
            RoomCell cell = this.building.grid[p.y][p.x];
            if (this.fireStarted && cell.hasAlarmButton && !p.hasPressedAlarm) {
                this.alarmPressed++;
                cell.hasAlarmButton = false;
                p.hasPressedAlarm = true;
                if (p.isHero()) {
                    this.heroesPressedAlarmTotal++;
                } else {
                    this.civiliansPressedAlarmTotal++;
                }
                this.window.showAlarmAnimation();
                System.out.println("!!! " + p.getRoleName() + " " + p.id + " НАЖАЛ КНОПКУ ТРЕВОГИ !!!");
            }
            if (this.fireStarted && cell.isExit()) {
                toRemove.add(p);
                this.evacuatedCount++;
                System.out.println(p.getRoleName() + " " + p.id + " эвакуировался");
                continue;
            }
            if (this.fireStarted && p.isAlive && p.isEvacuating && !cell.isExit() && p.evacuationStartTime > 0 && this.timeStep - p.evacuationStartTime > 50) {
                Point exit = this.findNearestExit(p.x, p.y);
                if (exit != null && this.building.grid[exit.y][exit.x].isExit()) {
                    p.x = exit.x;
                    p.y = exit.y;
                    System.out.println(p.getRoleName() + " " + p.id + " телепортирован к выходу!");
                }
            }
            if (cell.isFire()) {
                p.increaseFireExposure();
            }
            if (cell.isSmoke()) {
                p.increaseSmokeExposure();
            }
            if (this.fireStarted && this.random.nextInt(100) < 15 && p.smokeExposure < 8) {
                p.smokeExposure += 3;
                System.out.println(p.getRoleName() + " " + p.id + " получил лёгкое отравление дымом!");
            }
            if (this.fireStarted && this.random.nextInt(100) < 10 && p.fireExposure < 6) {
                p.fireExposure += 2;
                System.out.println(p.getRoleName() + " " + p.id + " получил лёгкие ожоги!");
            }
            if (!p.isAlive) {
                System.out.println(p.getRoleName() + " " + p.id + " ПОГИБ!");
                toRemove.add(p);
                continue;
            }
            if (!p.isEvacuating && this.fireStarted) {
                p.isEvacuating = true;
                p.evacuationStartTime = this.timeStep;
            }
            if (cell.hasExitSign && !p.isFollowingExitSign) {
                p.isFollowingExitSign = true;
                System.out.println(p.getRoleName() + " " + p.id + " увидел знак выхода!");
            }
            if (cell.hasFireExtinguisher && p.canTakeFireExtinguisher()) {
                p.onPickUpFireExtinguisher();
                cell.hasFireExtinguisher = false;
                System.out.println(p.getRoleName() + " " + p.id + " взял огнетушитель");
            }
            if (p.canFightFire()) {
                p.onFightFire(this);
            }
            this.movePerson(p);
        }
        this.people.removeAll(toRemove);
        for (int i = this.fires.size() - 1; i >= 0; i--) {
            Fire fire = this.fires.get(i);
            if (!this.building.grid[fire.y][fire.x].isFire()) {
                this.fires.remove(i);
            }
        }
    }

    void addRandomPerson() {
        this.timeUntilNextPerson--;
        if (this.timeUntilNextPerson <= 0) {
            boolean isHero = this.random.nextInt(RANDOM_RANGE_100) < HERO_SPAWN_CHANCE_PERCENT;
            this.addPerson(isHero);
            this.timeUntilNextPerson = this.random.nextInt(TIME_BETWEEN_PEOPLE_MAX - TIME_BETWEEN_PEOPLE_MIN + 1) + TIME_BETWEEN_PEOPLE_MIN;
        }
    }

    void startFireRandomly() {
        if (!this.fireStarted) {
            int chance = 0;
            if (this.timeStep > FORCED_FIRE_STEP) {
                chance = RANDOM_RANGE_100;
            } else if (this.timeStep > FIRE_START_THRESHOLD_STEP_4) {
                chance = FIRE_START_CHANCE_4;
            } else if (this.timeStep > FIRE_START_THRESHOLD_STEP_3) {
                chance = FIRE_START_CHANCE_3;
            } else if (this.timeStep > FIRE_START_THRESHOLD_STEP_2) {
                chance = FIRE_START_CHANCE_2;
            } else if (this.timeStep > FIRE_START_THRESHOLD_STEP_1) {
                chance = FIRE_START_CHANCE_1;
            }
            if (this.random.nextInt(RANDOM_RANGE_100) < chance) {
                this.startFire();
            }
        }
    }

    void step() {
        this.timeStep++;
        this.addRandomPerson();
        this.startFireRandomly();
        if (this.fireStarted && this.timeStep % SPREAD_INTERVAL_STEPS == 0) {
            this.spreadFire();
        }
        this.updatePeople();
        this.window.updateDisplay(this, this.timeStep);
        try {
            Thread.sleep(ANIMATION_DELAY_MS);
        } catch (Exception e) {
            System.out.println("Ошибка задержки анимации: " + e.getMessage());
        }
    }

    void run() {
        for (int i = 0; i < 150; i++) {
            this.step();
        }
        ArrayList<Person> forcedToRemove = new ArrayList<>();
        for (Person p : this.people) {
            if (p.isAlive) {
                forcedToRemove.add(p);
                this.evacuatedCount++;
                System.out.println(p.getRoleName() + " " + p.id + " принудительно эвакуирован");
            }
        }
        this.people.removeAll(forcedToRemove);
        this.window.showFinalReport(this, this.totalPeopleEver, this.totalHeroesEver, this.totalCiviliansEver, this.spreadCount, this.fireStartTime);
    }
}