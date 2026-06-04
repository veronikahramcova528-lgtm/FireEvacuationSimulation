void main() {
    System.out.println("========================================");
    System.out.println("ЗАПУСК МОДЕЛИРОВАНИЯ ПОЖАРА");
    System.out.println("========================================");

    Simulation sim = new Simulation(50, 22);

    boolean mapLoaded = false;
    try {
        sim.building.loadFromFile();
        mapLoaded = true;
        System.out.println("Карта успешно загружена из файла: map.txt");
    } catch (Exception e) {
        System.out.println("Не удалось загрузить карту из файла: map.txt");
        System.out.println("Использую ручную настройку карты.");
    }

    if (!mapLoaded) {
        sim.building.addExit(10, 18);
        sim.building.addExit(11, 18);
        sim.building.addExit(26, 18);
        sim.building.addExit(27, 18);
        sim.building.addExit(10, 19);
        sim.building.addExit(11, 19);
        sim.building.addExit(26, 19);
        sim.building.addExit(27, 19);

        sim.building.addWindow(12, 0);
        sim.building.addWindow(12, 17);

        sim.building.addFireExtinguisher(6, 6);
        sim.building.addFireExtinguisher(18, 12);
        sim.building.addFireExtinguisher(12, 8);

        sim.building.addAlarmButton(12, 5);
        sim.building.addAlarmButton(12, 12);
        sim.building.addAlarmButton(5, 9);
        sim.building.addAlarmButton(19, 9);
        sim.building.addAlarmButton(8, 4);
        sim.building.addAlarmButton(16, 14);

        sim.building.addExitSign(8, 4);
        sim.building.addExitSign(16, 14);
        sim.building.addExitSign(12, 2);
    }

    sim.building.printMap();

    for (int i = 0; i < 10; i = i + 1) {
        int remainder = i % 5;
        boolean isHero = (remainder == 0);
        sim.addPerson(isHero);
    }

    sim.run();

    System.out.println("МОДЕЛИРОВАНИЕ ЗАВЕРШЕНО");
}