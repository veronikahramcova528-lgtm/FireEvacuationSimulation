import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

class StatisticsWindow extends JFrame {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 800;
    private static final int REPORT_WIDTH = 750;
    private static final int REPORT_HEIGHT = 750;
    private static final int MAP_TEXT_COLS = 35;
    private static final int MAP_TEXT_ROWS = 25;
    private static final int STATS_TEXT_ROWS = 20;
    private static final int STATS_TEXT_COLS = 35;
    private static final int PEOPLE_TEXT_ROWS = 12;
    private static final int PEOPLE_TEXT_COLS = 80;
    private static final int HEALTH_BAR_LENGTH = 10;
    private static final int FIRE_EXPOSURE_MAX = 18;
    private static final int SMOKE_EXPOSURE_MAX = 25;
    private static final int FIRE_EXPOSURE_HEAVY = 12;
    private static final int FIRE_EXPOSURE_MEDIUM = 6;
    private static final int SMOKE_EXPOSURE_HEAVY = 15;

    private final JTextArea mapArea;
    private final JTextArea statsArea;
    private final JTextArea peopleArea;
    private final JLabel stepLabel;
    private final AnimationPanel animationPanel;

    StatisticsWindow() {

        setTitle("Моделирование пожара - Система статистики");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));

        stepLabel = new JLabel("Шаг 0", JLabel.CENTER);
        stepLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        topPanel.add(stepLabel);

        JLabel animLabel = new JLabel("ДИНАМИЧЕСКАЯ ВИЗУАЛИЗАЦИЯ ПОЖАРА", JLabel.CENTER);
        animLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        animLabel.setForeground(new Color(255, 80, 0));
        topPanel.add(animLabel);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 2));

        mapArea = new JTextArea(MAP_TEXT_ROWS, MAP_TEXT_COLS);
        mapArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mapArea.setEditable(false);
        JScrollPane mapScroll = new JScrollPane(mapArea);
        mapScroll.setBorder(BorderFactory.createTitledBorder("Карта помещения"));
        centerPanel.add(mapScroll);

        statsArea = new JTextArea(STATS_TEXT_ROWS, STATS_TEXT_COLS);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setEditable(false);
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsScroll.setBorder(BorderFactory.createTitledBorder("Статистика"));
        centerPanel.add(statsScroll);

        add(centerPanel, BorderLayout.CENTER);

        peopleArea = new JTextArea(PEOPLE_TEXT_ROWS, PEOPLE_TEXT_COLS);
        peopleArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        peopleArea.setEditable(false);
        JScrollPane peopleScroll = new JScrollPane(peopleArea);
        peopleScroll.setBorder(BorderFactory.createTitledBorder("Статус людей"));
        add(peopleScroll, BorderLayout.SOUTH);

        animationPanel = new AnimationPanel();
        animationPanel.setBorder(BorderFactory.createTitledBorder("Анимация пожара"));
        add(animationPanel, BorderLayout.AFTER_LAST_LINE);

        setVisible(true);
    }

    void showAlarmAnimation() {
        animationPanel.startAlarmAnimation();
    }

    void updateDisplay(Simulation sim, int step) {
        String fireStatus;
        if (sim.fireStarted) {
            fireStatus = "АКТИВЕН";
        } else {
            fireStatus = "ОЖИДАНИЕ";
        }

        stepLabel.setText("ШАГ " + step + " | ПОЖАР: " + fireStatus + " | ЛЮДЕЙ В ЗДАНИИ: " + sim.people.size());

        String mapText = buildMap(sim);
        mapArea.setText(mapText);

        String statsText = buildStats(sim);
        statsArea.setText(statsText);

        String peopleText = buildPeopleStatus(sim);
        peopleArea.setText(peopleText);

        animationPanel.updateFireData(sim);
        animationPanel.repaint();
    }

    private String buildMap(Simulation sim) {
        StringBuilder result = new StringBuilder();

        result.append("   ");
        for (int x = 0; x < sim.building.width; x = x + 1) {
            result.append(x % 10).append(" ");
        }
        result.append("\n");

        result.append("   ");
        for (int i = 0; i < sim.building.width; i = i + 1) {
            result.append("--");
        }
        result.append("\n");

        for (int y = 0; y < sim.building.height; y = y + 1) {
            if (y < 10) {
                result.append(" ");
            }
            result.append(y).append("|");

            for (int x = 0; x < sim.building.width; x = x + 1) {
                char symbol = getSymbol(sim, x, y);
                result.append(symbol).append(" ");
            }
            result.append("\n");
        }

        result.append("\nЛегенда:\n");
        result.append(". - проход    * - огонь    ~ - дым    # - стена\n");
        result.append("D - дверь     W - окно     P - человек    H - герой\n");
        result.append("E - огнетушитель    B - кнопка    S - знак выхода\n");

        return result.toString();
    }

    private char getSymbol(Simulation sim, int x, int y) {
        for (int i = 0; i < sim.people.size(); i = i + 1) {
            Person p = sim.people.get(i);
            boolean sameX = (p.x == x);
            boolean sameY = (p.y == y);
            boolean isAlive = p.isAlive;

            if (sameX && sameY && isAlive) {
                if (p.isHero()) {
                    return 'H';
                } else {
                    return 'P';
                }
            }
        }

        RoomCell cell = sim.building.grid[y][x];

        if (cell.isFire()) {
            return '*';
        }
        if (cell.isSmoke()) {
            return '~';
        }
        if (cell.isWall()) {
            return '#';
        }
        if (cell.isExit()) {
            return 'D';
        }
        if (cell.isWindow()) {
            return 'W';
        }
        if (cell.hasFireExtinguisher) {
            return 'E';
        }
        if (cell.hasAlarmButton) {
            return 'B';
        }
        if (cell.hasExitSign) {
            return 'S';
        }

        return '.';
    }

    private String buildStats(Simulation sim) {
        int aliveCount = 0;
        int heroAlive = 0;
        int civilianAlive = 0;

        for (int i = 0; i < sim.people.size(); i = i + 1) {
            Person p = sim.people.get(i);
            if (p.isAlive) {
                aliveCount = aliveCount + 1;
                if (p.isHero()) {
                    heroAlive = heroAlive + 1;
                } else {
                    civilianAlive = civilianAlive + 1;
                }
            }
        }

        int fireCount = 0;
        int smokeCount = 0;

        for (int i = 0; i < sim.building.height; i = i + 1) {
            for (int j = 0; j < sim.building.width; j = j + 1) {
                if (sim.building.grid[i][j].isFire()) {
                    fireCount = fireCount + 1;
                }
                if (sim.building.grid[i][j].isSmoke()) {
                    smokeCount = smokeCount + 1;
                }
            }
        }

        int avgIntensity = 0;
        for (int i = 0; i < sim.fires.size(); i = i + 1) {
            Fire f = sim.fires.get(i);
            avgIntensity = avgIntensity + f.getIntensity();
        }

        boolean hasFires = !sim.fires.isEmpty();
        if (hasFires) {
            avgIntensity = avgIntensity / sim.fires.size();
        }

        int remainingButtons = 0;
        for (int i = 0; i < sim.building.alarmButtons.size(); i = i + 1) {
            Point btn = sim.building.alarmButtons.get(i);
            if (sim.building.grid[btn.y][btn.x].hasAlarmButton) {
                remainingButtons = remainingButtons + 1;
            }
        }

        String result = "";
        result = result + "========== ТЕКУЩАЯ СТАТИСТИКА ==========\n\n";
        result = result + "ПОМЕЩЕНИЕ: " + sim.buildingName + "\n";
        result = result + "----------------------------------------\n\n";

        result = result + "СТАТУС ЛЮДЕЙ:\n";
        result = result + "  Сейчас в здании: " + aliveCount + "\n";
        result = result + "    Героев: " + heroAlive + "\n";
        result = result + "    Гражданских: " + civilianAlive + "\n";
        result = result + "\n";

        result = result + "ИТОГО ЗА ВСЮ СИМУЛЯЦИЮ:\n";
        result = result + "  Всего создано людей: " + sim.totalPeopleEver + "\n";
        result = result + "    Героев: " + sim.totalHeroesEver + "\n";
        result = result + "    Гражданских: " + sim.totalCiviliansEver + "\n";
        result = result + "  Эвакуировалось всего: " + sim.evacuatedCount + "\n";
        result = result + "\n";

        result = result + "КНОПКИ ТРЕВОГИ:\n";
        result = result + "  Нажато кнопок: " + sim.alarmPressed;
        result = result + " из " + sim.building.alarmButtons.size() + "\n";
        result = result + "  Осталось кнопок: " + remainingButtons + "\n";
        result = result + "  Героев нажало: " + sim.heroesPressedAlarmTotal + "\n";
        result = result + "  Гражданских нажало: " + sim.civiliansPressedAlarmTotal + "\n";
        result = result + "\n";

        result = result + "ПОЖАР:\n";
        result = result + "  Очагов огня: " + fireCount + "\n";
        result = result + "  Средняя интенсивность: " + avgIntensity + "/10\n";
        result = result + "  Задымленных клеток: " + smokeCount + "\n";
        result = result + "  Всего очагов за игру: " + sim.fires.size() + "\n";
        result = result + "\n";

        result = result + "ПОМЕЩЕНИЕ:\n";
        result = result + "  Доступных выходов: " + sim.building.exits.size() + "\n";
        result = result + "  Огнетушителей: " + sim.building.fireExtinguishers.size() + "\n";
        result = result + "  Знаков выхода: " + sim.building.exitSigns.size() + "\n";
        result = result + "\n";
        result = result + "=========================================";

        return result;
    }

    private String buildPeopleStatus(Simulation sim) {
        if (sim.people.isEmpty()) {
            return "В помещении нет людей.";
        }

        StringBuilder result = new StringBuilder();
        result.append("ID   Тип         Статус                  Ожоги   Дым    Здоровье\n");
        result.append("----------------------------------------------------------------\n");

        for (int idx = 0; idx < sim.people.size(); idx = idx + 1) {
            Person p = sim.people.get(idx);

            String status;
            if (!p.isAlive) {
                status = "ПОГИБ";
            } else {
                boolean fireMax = (p.fireExposure >= FIRE_EXPOSURE_MAX);
                if (fireMax) {
                    status = "СМЕРТЕЛЬНЫЕ ОЖОГИ";
                } else {
                    boolean fireHeavy = (p.fireExposure >= FIRE_EXPOSURE_HEAVY);
                    if (fireHeavy) {
                        status = "ТЯЖЕЛЫЕ ОЖОГИ";
                    } else {
                        boolean fireMedium = (p.fireExposure >= FIRE_EXPOSURE_MEDIUM);
                        if (fireMedium) {
                            status = "ОЖОГИ";
                        } else {
                            boolean smokeMax = (p.smokeExposure >= SMOKE_EXPOSURE_MAX);
                            if (smokeMax) {
                                status = "СМЕРТЕЛЬНЫЙ ДЫМ";
                            } else {
                                boolean smokeHeavy = (p.smokeExposure >= SMOKE_EXPOSURE_HEAVY);
                                if (smokeHeavy) {
                                    status = "ОТРАВЛЕНИЕ ДЫМОМ";
                                } else {
                                    boolean evacuating = p.isEvacuating;
                                    if (evacuating) {
                                        status = "ЭВАКУАЦИЯ";
                                    } else {
                                        status = "НОРМА";
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String role = p.getRoleName();

            boolean following = p.isFollowingExitSign;
            boolean isAliveFlag = p.isAlive;

            if (following && isAliveFlag) {
                status = status + "→ВЫХОД";
            }

            int healthPercent = (int) p.getHealthPercentage();
            String healthBar = getHealthBar(healthPercent);

            String idStr = String.format("%3d", p.id);
            String roleStr = String.format("%-12s", role);
            String statusStr = String.format("%-18s", status);
            String fireStr = String.format("%3d/18", p.fireExposure);
            String smokeStr = String.format("%3d/25", p.smokeExposure);

            result.append(idStr).append("  ");
            result.append(roleStr).append(" ");
            result.append(statusStr).append(" ");
            result.append(fireStr).append("   ");
            result.append(smokeStr).append("   ");
            result.append(healthBar).append("\n");
        }

        return result.toString();
    }

    private String getHealthBar(int percent) {
        int bars = percent / (100 / HEALTH_BAR_LENGTH);
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < HEALTH_BAR_LENGTH; i = i + 1) {
            if (i < bars) {
                result.append("#");
            } else {
                result.append("-");
            }
        }
        result.append("] ").append(percent).append("%");
        return result.toString();
    }

    void showFinalReport(Simulation sim, int totalPeopleEver, int totalHeroesEver, int totalCiviliansEver, int spreadCount, int fireStartTime) {
        int diedInside = 0;
        int stillInside = 0;
        int heroesSurvived = 0;
        int civiliansSurvived = 0;
        int heroesDied = 0;
        int civiliansDied = 0;

        for (int i = 0; i < sim.people.size(); i = i + 1) {
            Person p = sim.people.get(i);
            if (p.isAlive) {
                stillInside = stillInside + 1;
                if (p.isHero()) {
                    heroesSurvived = heroesSurvived + 1;
                } else {
                    civiliansSurvived = civiliansSurvived + 1;
                }
            } else {
                diedInside = diedInside + 1;
                if (p.isHero()) {
                    heroesDied = heroesDied + 1;
                } else {
                    civiliansDied = civiliansDied + 1;
                }
            }
        }

        int evacuated = sim.evacuatedCount;

        int heroesEvacuated = totalHeroesEver - heroesDied - heroesSurvived;
        int civiliansEvacuated = totalCiviliansEver - civiliansDied - civiliansSurvived;

        System.out.println("=== ОТЛАДОЧНАЯ ИНФОРМАЦИЯ ===");
        System.out.println("Помещение: " + sim.buildingName);
        System.out.println("Всего людей: " + totalPeopleEver);
        System.out.println("Всего героев: " + totalHeroesEver);
        System.out.println("Всего гражданских: " + totalCiviliansEver);
        System.out.println("Эвакуировалось: " + evacuated);
        System.out.println("Нажато кнопок: " + sim.alarmPressed);
        System.out.println("===========================");

        String report = "";
        report = report + "========================================\n";
        report = report + "        ИТОГОВЫЙ ОТЧЕТ ПО ПОЖАРУ        \n";
        report = report + "========================================\n\n";

        report = report + "ПОМЕЩЕНИЕ: " + sim.buildingName + "\n";
        report = report + "----------------------------------------\n\n";

        report = report + "1. ОБЩИЕ ПОКАЗАТЕЛИ\n";
        report = report + "   Продолжительность: " + String.format("%4d", sim.timeStep) + " шагов\n";
        report = report + "   Пожар начался на шаге: " + String.format("%4d", fireStartTime) + "\n";

        boolean fireStarted = (fireStartTime > 0);
        if (fireStarted) {
            report = report + "   Пожар длился: " + String.format("%4d", sim.timeStep - fireStartTime) + " шагов\n";
        }
        report = report + "\n";

        report = report + "   Всего создано людей: " + String.format("%4d", totalPeopleEver) + "\n";
        report = report + "     - Героев: " + String.format("%4d", totalHeroesEver) + "\n";
        report = report + "     - Гражданских: " + String.format("%4d", totalCiviliansEver) + "\n";
        report = report + "\n";

        report = report + "   Эвакуировалось: " + String.format("%4d", evacuated) + "\n";
        report = report + "     - Героев: " + String.format("%4d", heroesEvacuated) + "\n";
        report = report + "     - Гражданских: " + String.format("%4d", civiliansEvacuated) + "\n";
        report = report + "\n";

        report = report + "   Погибло в здании: " + String.format("%4d", diedInside) + "\n";
        report = report + "     - Героев: " + String.format("%4d", heroesDied) + "\n";
        report = report + "     - Гражданских: " + String.format("%4d", civiliansDied) + "\n";
        report = report + "\n";

        report = report + "   Осталось в здании: " + String.format("%4d", stillInside) + "\n";
        report = report + "     - Героев: " + String.format("%4d", heroesSurvived) + "\n";
        report = report + "     - Гражданских: " + String.format("%4d", civiliansSurvived) + "\n";
        report = report + "\n";

        report = report + "   Нажато кнопок тревоги: " + String.format("%4d", sim.alarmPressed);
        report = report + " из " + sim.building.alarmButtons.size() + "\n";
        report = report + "\n";

        report = report + "2. ДЕЙСТВИЯ ЛЮДЕЙ\n";
        report = report + "   Всего очагов пожара: " + sim.fires.size() + "\n";
        report = report + "   Шагов распространения огня: " + spreadCount + "\n";
        report = report + "\n";

        double survivalRate;
        boolean hasPeople = (totalPeopleEver > 0);
        if (hasPeople) {
            survivalRate = (evacuated * 100.0) / totalPeopleEver;
        } else {
            survivalRate = 0;
        }

        report = report + "3. ИТОГОВЫЙ РЕЗУЛЬТАТ\n";
        report = report + "   Процент выживших: " + String.format("%.1f", survivalRate) + "%\n\n";

        boolean allEvacuated = (evacuated == totalPeopleEver && totalPeopleEver > 0);
        if (allEvacuated) {
            report = report + "   РЕЗУЛЬТАТ: ВСЕ ЛЮДИ ЭВАКУИРОВАНЫ УСПЕШНО!\n";
        } else {
            boolean noDeaths = (diedInside == 0 && evacuated > 0);
            if (noDeaths) {
                report = report + "   РЕЗУЛЬТАТ: ЖЕРТВ НЕТ, НО НЕ ВСЕ ЭВАКУИРОВАНЫ\n";
            } else {
                boolean hasDeaths = (diedInside > 0);
                if (hasDeaths) {
                    report = report + "   РЕЗУЛЬТАТ: ЕСТЬ ЖЕРТВЫ. ТРЕБУЕТСЯ УЛУЧШЕНИЕ\n";
                }
            }
        }

        report = report + "\n";
        report = report + "========================================\n";

        JFrame reportFrame = new JFrame("Итоговый отчет по моделированию пожара - " + sim.buildingName);
        reportFrame.setSize(REPORT_WIDTH, REPORT_HEIGHT);
        reportFrame.setLocation(100, 100);
        JTextArea reportArea = new JTextArea(report);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        reportArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(reportArea);
        reportFrame.add(scroll);
        reportFrame.setVisible(true);
    }

    private static class AnimationPanel extends JPanel {
        private static final int ANIMATION_DELAY_MS = 50;
        private static final int FLAME_COUNT = 20;
        private static final int MIN_FLAME_HEIGHT = 10;
        private static final int MAX_FLAME_HEIGHT = 60;
        private static final int MIN_FLAME_WIDTH = 8;
        private static final int MAX_FLAME_WIDTH = 20;
        private static final int ALARM_ANIMATION_DURATION = 20;
        private static final int ALARM_BLINK_INTERVAL = 4;
        private static final int SMOKE_LAYERS = 10;
        private static final int SMOKE_BASE_ALPHA = 40;
        private static final int MAX_SMOKE_ALPHA = 80;
        private static final int FLAME_Y_OFFSET = 25;
        private static final int FLAME_START_X = 30;
        private static final int FLAME_X_STEP = 65;

        private Simulation currentSim;
        private int frameCounter;
        private final int[] flameHeights;
        private final int[] flameWidths;
        private boolean alarmAnimation;
        private int alarmAnimationCounter;

        AnimationPanel() {
            this.currentSim = null;
            this.frameCounter = 0;
            this.alarmAnimation = false;
            this.alarmAnimationCounter = 0;
            this.flameHeights = new int[FLAME_COUNT];
            this.flameWidths = new int[FLAME_COUNT];

            for (int i = 0; i < FLAME_COUNT; i = i + 1) {
                int heightRange = MAX_FLAME_HEIGHT - MIN_FLAME_HEIGHT;
                int randomHeight = MIN_FLAME_HEIGHT + (int) (Math.random() * heightRange);
                this.flameHeights[i] = randomHeight;

                int widthRange = MAX_FLAME_WIDTH - MIN_FLAME_WIDTH;
                int randomWidth = MIN_FLAME_WIDTH + (int) (Math.random() * widthRange);
                this.flameWidths[i] = randomWidth;
            }

            setBackground(new Color(15, 15, 25));
            setPreferredSize(new Dimension(900, 150));

            Timer animationTimer = new Timer(ANIMATION_DELAY_MS, _ -> {
                frameCounter = frameCounter + 1;

                if (alarmAnimation) {
                    alarmAnimationCounter = alarmAnimationCounter + 1;
                    boolean animOver = (alarmAnimationCounter > ALARM_ANIMATION_DURATION);
                    if (animOver) {
                        alarmAnimation = false;
                        alarmAnimationCounter = 0;
                    }
                }

                for (int i = 0; i < FLAME_COUNT; i = i + 1) {
                    int heightRange = MAX_FLAME_HEIGHT - MIN_FLAME_HEIGHT;
                    int randomHeight = MIN_FLAME_HEIGHT + (int) (Math.random() * heightRange);
                    int randomWidth = MIN_FLAME_WIDTH + (int) (Math.random() * (MAX_FLAME_WIDTH - MIN_FLAME_WIDTH));

                    flameHeights[i] = randomHeight + (frameCounter % 10);
                    flameWidths[i] = randomWidth;

                    boolean tooHigh = (flameHeights[i] > MAX_FLAME_HEIGHT);
                    if (tooHigh) {
                        flameHeights[i] = MAX_FLAME_HEIGHT;
                    }
                }
                repaint();
            });
            animationTimer.start();
        }

        void updateFireData(Simulation sim) {
            this.currentSim = sim;
        }

        void startAlarmAnimation() {
            this.alarmAnimation = true;
            this.alarmAnimationCounter = 0;
        }

        private int countSmokeCells() {
            if (this.currentSim == null) {
                return 0;
            }
            int count = 0;
            for (int i = 0; i < this.currentSim.building.height; i = i + 1) {
                for (int j = 0; j < this.currentSim.building.width; j = j + 1) {
                    boolean isSmoke = this.currentSim.building.grid[i][j].isSmoke();
                    if (isSmoke) {
                        count = count + 1;
                    }
                }
            }
            return count;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int w = getWidth();
            int h = getHeight();

            g2d.setColor(new Color(20, 20, 40));
            g2d.fillRect(0, 0, w, h);

            if (this.alarmAnimation) {
                int alpha;
                int halfInterval = ALARM_BLINK_INTERVAL / 2;
                boolean blinkOn = (this.alarmAnimationCounter % ALARM_BLINK_INTERVAL < halfInterval);

                if (blinkOn) {
                    alpha = 150;
                } else {
                    alpha = 50;
                }

                g2d.setColor(new Color(255, 0, 0, alpha));
                g2d.fillRect(0, 0, w, h);

                g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
                g2d.setColor(Color.RED);
                String alarmText = "ТРЕВОГА!";
                int textWidth = g2d.getFontMetrics().stringWidth(alarmText);
                g2d.drawString(alarmText, w / 2 - textWidth / 2, 60);
            }

            if (this.currentSim != null) {
                if (this.currentSim.fireStarted) {
                    int fireCount = this.currentSim.fires.size();
                    int smokeCount = countSmokeCells();

                    boolean aLotOfSmoke = (smokeCount > 5);
                    if (aLotOfSmoke) {
                        int alpha = SMOKE_BASE_ALPHA + smokeCount / 5;
                        boolean alphaTooHigh = (alpha > MAX_SMOKE_ALPHA);
                        if (alphaTooHigh) {
                            alpha = MAX_SMOKE_ALPHA;
                        }
                        g2d.setColor(new Color(80, 80, 80, alpha));
                        for (int i = 0; i < SMOKE_LAYERS; i = i + 1) {
                            int smokeX = (this.frameCounter * 2 + i * 50) % (w + 100) - 50;
                            double sinValue = Math.sin(this.frameCounter * 0.05 + i);
                            int sinOffset = (int) (sinValue * 10);
                            int smokeY = h - 50 - (i * 8) + sinOffset;
                            g2d.fillOval(smokeX, smokeY, 50, 35);
                        }
                    }

                    int visibleFires = fireCount;
                    boolean tooManyFires = (visibleFires > FLAME_COUNT);
                    if (tooManyFires) {
                        visibleFires = FLAME_COUNT;
                    }

                    for (int i = 0; i < visibleFires; i = i + 1) {
                        double sinValue = Math.sin(this.frameCounter * 0.1 + i);
                        int sinOffset = (int) (sinValue * 4);
                        int x = FLAME_START_X + i * FLAME_X_STEP + sinOffset;
                        int height = this.flameHeights[i % FLAME_COUNT];
                        int width = this.flameWidths[i % FLAME_COUNT];

                        g2d.setColor(new Color(180, 30, 0, 180));
                        int[] xOuter = {x, x - width / 2, x + width / 2};
                        int[] yOuter = {h - FLAME_Y_OFFSET, h - FLAME_Y_OFFSET - height, h - FLAME_Y_OFFSET - height};
                        g2d.fillPolygon(xOuter, yOuter, 3);

                        g2d.setColor(new Color(255, 100, 0, 220));
                        int[] xMid = {x, x - width / 3, x + width / 3};
                        int[] yMid = {h - FLAME_Y_OFFSET, h - FLAME_Y_OFFSET - height + 8, h - FLAME_Y_OFFSET - height + 8};
                        g2d.fillPolygon(xMid, yMid, 3);

                        g2d.setColor(new Color(255, 200, 50, 240));
                        int[] xInner = {x, x - width / 5, x + width / 5};
                        int[] yInner = {h - FLAME_Y_OFFSET, h - FLAME_Y_OFFSET - height + 15, h - FLAME_Y_OFFSET - height + 15};
                        g2d.fillPolygon(xInner, yInner, 3);

                        boolean drawSpark = (this.frameCounter % 6 == i % 3);
                        if (drawSpark) {
                            g2d.setColor(new Color(255, 150, 0));
                            double randomSpark = Math.random() * 15;
                            int sparkY = h - FLAME_Y_OFFSET - height + (int) randomSpark;
                            g2d.fillOval(x - 2, sparkY, 3, 3);
                        }
                    }

                    String fireStatus = "ПОЖАР! ОЧАГОВ: " + fireCount;
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
                    g2d.setColor(Color.RED);
                    g2d.drawString(fireStatus, 10, 30);

                } else {
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
                    g2d.setColor(new Color(0, 200, 0));
                    g2d.drawString("ПОЖАР НЕ НАЧАЛСЯ", w / 2 - 90, h / 2);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2d.setColor(Color.GRAY);
                    g2d.drawString("Ожидание возгорания...", w / 2 - 70, h / 2 + 30);
                }
            }

            g2d.setColor(new Color(100, 100, 150));
            g2d.drawRect(3, 3, w - 7, h - 7);
        }
    }
}