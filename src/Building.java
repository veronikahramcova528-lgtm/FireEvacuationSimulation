import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

class Building {
    int width;
    int height;
    RoomCell[][] grid;
    ArrayList<Point> exits;
    ArrayList<Point> windows;
    ArrayList<Point> fireExtinguishers;
    ArrayList<Point> alarmButtons;
    ArrayList<Point> exitSigns;

    Building(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new RoomCell[height][width];

        this.exits = new ArrayList<>();
        this.windows = new ArrayList<>();
        this.fireExtinguishers = new ArrayList<>();
        this.alarmButtons = new ArrayList<>();
        this.exitSigns = new ArrayList<>();

        for (int i = 0; i < height; i = i + 1) {
            for (int j = 0; j < width; j = j + 1) {
                this.grid[i][j] = new RoomCell(RoomCell.TYPE_EMPTY);
            }
        }

        createWalls();
    }

    void createWalls() {
        for (int i = 0; i < height; i = i + 1) {
            grid[i][0].type = RoomCell.TYPE_WALL;
            grid[i][width - 1].type = RoomCell.TYPE_WALL;
        }
        for (int j = 0; j < width; j = j + 1) {
            grid[0][j].type = RoomCell.TYPE_WALL;
            grid[height - 1][j].type = RoomCell.TYPE_WALL;
        }
    }

    void clearAll() {
        exits.clear();
        windows.clear();
        fireExtinguishers.clear();
        alarmButtons.clear();
        exitSigns.clear();

        for (int i = 0; i < height; i = i + 1) {
            for (int j = 0; j < width; j = j + 1) {
                grid[i][j] = new RoomCell(RoomCell.TYPE_EMPTY);
            }
        }
        createWalls();
    }

    void addExit(int x, int y) {
        boolean inside = isInside(x, y);
        boolean notWall = (grid[y][x].type != RoomCell.TYPE_WALL);

        if (inside) {
            if (notWall) {
                grid[y][x].type = RoomCell.TYPE_EXIT;
                exits.add(new Point(x, y));
            }
        }
    }

    void addWindow(int x, int y) {
        boolean inside = isInside(x, y);
        boolean notWall = (grid[y][x].type != RoomCell.TYPE_WALL);

        if (inside) {
            if (notWall) {
                grid[y][x].type = RoomCell.TYPE_WINDOW;
                windows.add(new Point(x, y));
            }
        }
    }

    void addFireExtinguisher(int x, int y) {
        boolean inside = isInside(x, y);
        boolean notWall = (grid[y][x].type != RoomCell.TYPE_WALL);

        if (inside) {
            if (notWall) {
                grid[y][x].hasFireExtinguisher = true;
                fireExtinguishers.add(new Point(x, y));
            }
        }
    }

    void addAlarmButton(int x, int y) {
        boolean inside = isInside(x, y);
        boolean notWall = (grid[y][x].type != RoomCell.TYPE_WALL);

        if (inside) {
            if (notWall) {
                grid[y][x].hasAlarmButton = true;
                alarmButtons.add(new Point(x, y));
            }
        }
    }

    void addExitSign(int x, int y) {
        boolean inside = isInside(x, y);
        boolean notWall = (grid[y][x].type != RoomCell.TYPE_WALL);

        if (inside) {
            if (notWall) {
                grid[y][x].hasExitSign = true;
                exitSigns.add(new Point(x, y));
            }
        }
    }

    void setFire(int x, int y) {
        boolean inside = isInside(x, y);

        if (!inside) {
            return;
        }

        boolean isEmpty = (grid[y][x].type == RoomCell.TYPE_EMPTY);
        boolean isSmoke = (grid[y][x].type == RoomCell.TYPE_SMOKE);

        if (isEmpty) {
            grid[y][x].type = RoomCell.TYPE_FIRE;
            grid[y][x].hasFireExtinguisher = false;
            grid[y][x].hasAlarmButton = false;
            grid[y][x].hasExitSign = false;
        }

        if (isSmoke) {
            grid[y][x].type = RoomCell.TYPE_FIRE;
            grid[y][x].hasFireExtinguisher = false;
            grid[y][x].hasAlarmButton = false;
            grid[y][x].hasExitSign = false;
        }
    }

    void setSmoke(int x, int y) {
        boolean inside = isInside(x, y);

        if (!inside) {
            return;
        }

        boolean isEmpty = (grid[y][x].type == RoomCell.TYPE_EMPTY);

        if (isEmpty) {
            grid[y][x].type = RoomCell.TYPE_SMOKE;
        }
    }

    boolean isInside(int x, int y) {
        boolean inside = true;

        if (x < 0) {
            inside = false;
        } else {
            if (x >= width) {
                inside = false;
            } else {
                if (y < 0) {
                    inside = false;
                } else {
                    if (y >= height) {
                        inside = false;
                    }
                }
            }
        }

        return inside;
    }

    boolean isWalkable(int x, int y) {
        boolean inside = isInside(x, y);
        boolean walkable = false;

        if (inside) {
            walkable = grid[y][x].isWalkable();
        }

        return walkable;
    }

    void loadFromFile() {
        try {
            clearAll();

            File file = new File("map.txt");
            Scanner scanner = new Scanner(file);

            int y = 0;
            while (scanner.hasNextLine()) {
                if (y >= height) {
                    break;
                }

                String line = scanner.nextLine();

                for (int x = 0; x < line.length(); x = x + 1) {
                    if (x >= width) {
                        break;
                    }

                    char c = line.charAt(x);

                    if (c == '#') {
                        grid[y][x].type = RoomCell.TYPE_WALL;
                    }

                    if (c == 'D') {
                        addExit(x, y);
                    }

                    if (c == 'W') {
                        addWindow(x, y);
                    }

                    if (c == 'E') {
                        addFireExtinguisher(x, y);
                    }

                    if (c == 'B') {
                        addAlarmButton(x, y);
                    }

                    if (c == 'S') {
                        addExitSign(x, y);
                    }

                    if (c == '.') {
                        boolean notWall = (grid[y][x].type != RoomCell.TYPE_WALL);
                        if (notWall) {
                            grid[y][x].type = RoomCell.TYPE_EMPTY;
                        }
                    }
                }
                y = y + 1;
            }
            scanner.close();
            System.out.println("Карта загружена из файла: map.txt");
        } catch (Exception e) {
            System.out.println("Ошибка загрузки карты: " + e.getMessage());
            System.out.println("Использую стандартную генерацию стен.");
        }
    }

    void printMap() {
        System.out.println("\n=== ПЛАН ПОМЕЩЕНИЯ ===");
        for (int i = 0; i < height; i = i + 1) {
            for (int j = 0; j < width; j = j + 1) {
                System.out.print(grid[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
        System.out.println("=======================\n");
    }
}