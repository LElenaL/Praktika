import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VotingSystem {
    // Структуры данных для хранения информации
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, Cik> ciks = new HashMap<>();
    private static Map<String, Candidate> candidates = new HashMap<>();
    private static List<Voting> votings = new ArrayList<>();
    private static User currentUser = null;

    // Форматы дат
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void main(String[] args) {
        // Инициализация тестовых данных
        initializeTestData();

        // Главный цикл программы
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (currentUser == null) {
                showLoginMenu(scanner);
            } else {
                showRoleMenu(scanner);
            }
        }
    }

    private static void initializeTestData() {
        // Администратор
        users.put("admin", new User("admin", "admin123", "Администратор", "admin"));

        // Тестовый ЦИК
        ciks.put("cik1", new Cik("cik1", "cik123", "ЦИК Центральный"));

        // Тестовые кандидаты
        candidates.put("candidate1", new Candidate("candidate1", "cand123", "Иванов Иван Иванович"));
        candidates.put("candidate2", new Candidate("candidate2", "cand123", "Петров Петр Петрович"));

        // Тестовые пользователи
        users.put("user1", new User("user1", "user123", "Сидоров Алексей Владимирович", "user", "12345678901", "01.01.1990"));
    }

    private static void showLoginMenu(Scanner scanner) {
        System.out.println("\n=== Система электронного голосования ===");
        System.out.println("1. Вход");
        System.out.println("2. Регистрация");
        System.out.println("3. Выход");
        System.out.print("Выберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // очистка буфера

        switch (choice) {
            case 1:
                login(scanner);
                break;
            case 2:
                registerUser(scanner);
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    private static void login(Scanner scanner) {
        System.out.print("Логин: ");
        String login = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        // Проверка пользователя
        if (users.containsKey(login) && users.get(login).password.equals(password)) {
            currentUser = users.get(login);
            System.out.println("Вход выполнен как " + currentUser.role);
        }
        // Проверка ЦИК
        else if (ciks.containsKey(login) && ciks.get(login).password.equals(password)) {
            currentUser = ciks.get(login);
            System.out.println("Вход выполнен как ЦИК");
        }
        // Проверка кандидата
        else if (candidates.containsKey(login) && candidates.get(login).password.equals(password)) {
            currentUser = candidates.get(login);
            System.out.println("Вход выполнен как кандидат");
        } else {
            System.out.println("Неверный логин или пароль!");
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.println("\n=== Регистрация пользователя ===");
        System.out.print("ФИО: ");
        String fullName = scanner.nextLine();
        System.out.print("Дата рождения (дд.мм.гггг): ");
        String birthDate = scanner.nextLine();
        System.out.print("СНИЛС (11 цифр): ");
        String snils = scanner.nextLine();
        System.out.print("Логин: ");
        String login = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        // Проверка уникальности логина
        if (users.containsKey(login) || ciks.containsKey(login) || candidates.containsKey(login)) {
            System.out.println("Логин уже занят!");
            return;
        }

        // Проверка уникальности СНИЛС
        for (User user : users.values()) {
            if (user.snils != null && user.snils.equals(snils)) {
                System.out.println("Пользователь с таким СНИЛС уже зарегистрирован!");
                return;
            }
        }

        users.put(login, new User(login, password, fullName, "user", snils, birthDate));
        System.out.println("Регистрация успешна!");
    }

    private static void showRoleMenu(Scanner scanner) {
        switch (currentUser.role) {
            case "admin":
                showAdminMenu(scanner);
                break;
            case "cik":
                showCikMenu(scanner);
                break;
            case "candidate":
                showCandidateMenu(scanner);
                break;
            case "user":
                showUserMenu(scanner);
                break;
            default:
                System.out.println("Неизвестная роль!");
                currentUser = null;
        }
    }

    private static void showAdminMenu(Scanner scanner) {
        System.out.println("\n=== Меню администратора ===");
        System.out.println("1. Просмотр списка пользователей");
        System.out.println("2. Удаление пользователя");
        System.out.println("3. Просмотр списка ЦИК");
        System.out.println("4. Удаление ЦИК");
        System.out.println("5. Создание ЦИК");
        System.out.println("6. Просмотр кандидатов");
        System.out.println("7. Удаление кандидата");
        System.out.println("8. Выход");
        System.out.print("Выберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // очистка буфера

        switch (choice) {
            case 1:
                listUsers();
                break;
            case 2:
                deleteUser(scanner);
                break;
            case 3:
                listCiks();
                break;
            case 4:
                deleteCik(scanner);
                break;
            case 5:
                createCik(scanner);
                break;
            case 6:
                listCandidates();
                break;
            case 7:
                deleteCandidate(scanner);
                break;
            case 8:
                currentUser = null;
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    private static void listUsers() {
        System.out.println("\nСписок пользователей:");
        for (User user : users.values()) {
            System.out.println(user);
        }
    }

    private static void deleteUser(Scanner scanner) {
        System.out.print("Введите логин пользователя для удаления: ");
        String login = scanner.nextLine();
        if (users.containsKey(login)) {
            users.remove(login);
            System.out.println("Пользователь удален.");
        } else {
            System.out.println("Пользователь не найден!");
        }
    }

    private static void listCiks() {
        System.out.println("\nСписок ЦИК:");
        for (Cik cik : ciks.values()) {
            System.out.println(cik);
        }
    }

    private static void deleteCik(Scanner scanner) {
        System.out.print("Введите логин ЦИК для удаления: ");
        String login = scanner.nextLine();
        if (ciks.containsKey(login)) {
            ciks.remove(login);
            System.out.println("ЦИК удален.");
        } else {
            System.out.println("ЦИК не найден!");
        }
    }

    private static void createCik(Scanner scanner) {
        System.out.print("Введите логин нового ЦИК: ");
        String login = scanner.nextLine();
        System.out.print("Введите пароль нового ЦИК: ");
        String password = scanner.nextLine();
        System.out.print("Введите название ЦИК: ");
        String name = scanner.nextLine();

        if (ciks.containsKey(login)) {
            System.out.println("ЦИК с таким логином уже существует!");
            return;
        }

        ciks.put(login, new Cik(login, password, name));
        System.out.println("ЦИК создан.");
    }

    private static void listCandidates() {
        System.out.println("\nСписок кандидатов:");
        for (Candidate candidate : candidates.values()) {
            System.out.println(candidate);
        }
    }

    private static void deleteCandidate(Scanner scanner) {
        System.out.print("Введите логин кандидата для удаления: ");
        String login = scanner.nextLine();
        if (candidates.containsKey(login)) {
            candidates.remove(login);
            System.out.println("Кандидат удален.");
        } else {
            System.out.println("Кандидат не найден!");
        }
    }

    private static void showCikMenu(Scanner scanner) {
        System.out.println("\n=== Меню ЦИК ===");
        System.out.println("1. Создать голосование");
        System.out.println("2. Добавить кандидата");
        System.out.println("3. Сохранить результаты (текстовый файл)");
        System.out.println("4. Группировка результатов");
        System.out.println("5. Сортировка результатов");
        System.out.println("6. Выход");
        System.out.print("Выберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // очистка буфера

        switch (choice) {
            case 1:
                createVoting(scanner);
                break;
            case 2:
                addCandidate(scanner);
                break;
            case 3:
                saveResultsToTextFile(scanner);
                break;
            case 4:
                groupResults(scanner);
                break;
            case 5:
                sortResults(scanner);
                break;
            case 6:
                currentUser = null;
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    private static void createVoting(Scanner scanner) {
        System.out.print("Введите название голосования: ");
        String name = scanner.nextLine();
        System.out.print("Введите дату окончания (дд.мм.гггг): ");
        String endDateStr = scanner.nextLine();

        try {
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);
            Voting voting = new Voting(name, endDate);
            votings.add(voting);
            System.out.println("Голосование создано.");
        } catch (Exception e) {
            System.out.println("Неверный формат даты!");
        }
    }

    private static void addCandidate(Scanner scanner) {
        System.out.print("Введите логин нового кандидата: ");
        String login = scanner.nextLine();
        System.out.print("Введите пароль нового кандидата: ");
        String password = scanner.nextLine();
        System.out.print("Введите ФИО кандидата: ");
        String fullName = scanner.nextLine();

        if (candidates.containsKey(login)) {
            System.out.println("Кандидат с таким логином уже существует!");
            return;
        }

        candidates.put(login, new Candidate(login, password, fullName));
        System.out.println("Кандидат добавлен.");
    }

    private static void saveResultsToTextFile(Scanner scanner) {
        System.out.println("Выберите голосования для выгрузки (через запятую):");
        for (int i = 0; i < votings.size(); i++) {
            System.out.println((i+1) + ". " + votings.get(i).name);
        }
        System.out.print("Ваш выбор: ");
        String input = scanner.nextLine();

        String[] indices = input.split(",");
        List<Voting> selectedVotings = new ArrayList<>();

        for (String indexStr : indices) {
            try {
                int index = Integer.parseInt(indexStr.trim()) - 1;
                if (index >= 0 && index < votings.size()) {
                    selectedVotings.add(votings.get(index));
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод: " + indexStr);
            }
        }

        if (selectedVotings.isEmpty()) {
            System.out.println("Не выбрано ни одного голосования!");
            return;
        }

        System.out.print("Выгрузить одним файлом? (да/нет): ");
        boolean singleFile = scanner.nextLine().equalsIgnoreCase("да");

        System.out.print("Введите путь для сохранения (оставьте пустым для папки программы): ");
        String path = scanner.nextLine();

        if (singleFile) {
            exportToTextFile(selectedVotings, path);
        } else {
            for (Voting voting : selectedVotings) {
                exportToTextFile(Collections.singletonList(voting), path);
            }
        }
    }

    private static void exportToTextFile(List<Voting> votings, String path) {
        String fileName;
        if (path == null || path.isEmpty()) {
            fileName = "results_" + timestampFormat.format(new Date()) + ".txt";
        } else {
            fileName = path + File.separator + "results_" + timestampFormat.format(new Date()) + ".txt";
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Voting voting : votings) {
                writer.println("Результаты голосования: " + voting.name);
                writer.println("Дата окончания: " + voting.endDate.format(dateFormatter));
                writer.println();

                for (Map.Entry<Candidate, Integer> entry : voting.results.entrySet()) {
                    writer.println(entry.getKey().fullName + ": " + entry.getValue() + " голосов");
                }

                writer.println();
                writer.println("----------------------------------------");
                writer.println();
            }

            System.out.println("Файл сохранен: " + fileName);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    private static void groupResults(Scanner scanner) {
        System.out.println("Выберите голосование для группировки:");
        for (int i = 0; i < votings.size(); i++) {
            System.out.println((i+1) + ". " + votings.get(i).name);
        }
        System.out.print("Ваш выбор: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (choice < 0 || choice >= votings.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        Voting voting = votings.get(choice);

        System.out.println("1. Группировка по кандидатам");
        System.out.println("2. Группировка по датам");
        System.out.print("Выберите тип группировки: ");
        int groupType = scanner.nextInt();
        scanner.nextLine();

        switch (groupType) {
            case 1:
                System.out.println("\nРезультаты по кандидатам:");
                for (Map.Entry<Candidate, Integer> entry : voting.results.entrySet()) {
                    System.out.println(entry.getKey().fullName + ": " + entry.getValue() + " голосов");
                }
                break;
            case 2:
                System.out.println("\nРезультаты по датам:");
                System.out.println("Группировка по датам пока не реализована.");
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    private static void sortResults(Scanner scanner) {
        System.out.println("Выберите голосование для сортировки:");
        for (int i = 0; i < votings.size(); i++) {
            System.out.println((i+1) + ". " + votings.get(i).name);
        }
        System.out.print("Ваш выбор: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (choice < 0 || choice >= votings.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        Voting voting = votings.get(choice);

        System.out.println("1. Сортировка по имени кандидата");
        System.out.println("2. Сортировка по количеству голосов (по убыванию)");
        System.out.println("3. Сортировка по количеству голосов (по возрастанию)");
        System.out.print("Выберите тип сортировки: ");
        int sortType = scanner.nextInt();
        scanner.nextLine();

        List<Map.Entry<Candidate, Integer>> entries = new ArrayList<>(voting.results.entrySet());

        switch (sortType) {
            case 1:
                entries.sort((e1, e2) -> e1.getKey().fullName.compareTo(e2.getKey().fullName));
                break;
            case 2:
                entries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
                break;
            case 3:
                entries.sort(Comparator.comparing(Map.Entry::getValue));
                break;
            default:
                System.out.println("Неверный выбор!");
                return;
        }

        System.out.println("\nОтсортированные результаты:");
        for (Map.Entry<Candidate, Integer> entry : entries) {
            System.out.println(entry.getKey().fullName + ": " + entry.getValue() + " голосов");
        }
    }

    private static void showCandidateMenu(Scanner scanner) {
        System.out.println("\n=== Меню кандидата ===");
        System.out.println("1. Заполнить данные о себе");
        System.out.println("2. Просмотреть результаты предыдущего голосования");
        System.out.println("3. Просмотреть все голосования, в которых участвовал");
        System.out.println("4. Выход");
        System.out.print("Выберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // очистка буфера

        switch (choice) {
            case 1:
                fillCandidateData(scanner);
                break;
            case 2:
                viewPreviousResults();
                break;
            case 3:
                viewParticipatedVotings();
                break;
            case 4:
                currentUser = null;
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    private static void fillCandidateData(Scanner scanner) {
        Candidate candidate = (Candidate) currentUser;
        System.out.print("Введите биографию: ");
        candidate.bio = scanner.nextLine();
        System.out.print("Введите программу: ");
        candidate.program = scanner.nextLine();
        System.out.println("Данные обновлены.");
    }

    private static void viewPreviousResults() {
        Candidate candidate = (Candidate) currentUser;
        System.out.println("\nРезультаты в предыдущих голосованиях:");

        for (Voting voting : votings) {
            if (voting.results.containsKey(candidate)) {
                System.out.println(voting.name + ": " + voting.results.get(candidate) + " голосов");
            }
        }
    }

    private static void viewParticipatedVotings() {
        Candidate candidate = (Candidate) currentUser;
        System.out.println("\nВсе голосования с участием кандидата:");

        for (Voting voting : votings) {
            if (voting.results.containsKey(candidate)) {
                System.out.println(voting.name + " (до " + voting.endDate.format(dateFormatter) + ")");
            }
        }
    }

    private static void showUserMenu(Scanner scanner) {
        System.out.println("\n=== Меню пользователя ===");
        System.out.println("1. Проголосовать");
        System.out.println("2. Просмотреть список кандидатов");
        System.out.println("3. Просмотреть все голосования");
        System.out.println("4. Выход");
        System.out.print("Выберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // очистка буфера

        switch (choice) {
            case 1:
                vote(scanner);
                break;
            case 2:
                listCandidatesForUser();
                break;
            case 3:
                listVotingsForUser();
                break;
            case 4:
                currentUser = null;
                break;
            default:
                System.out.println("Неверный выбор!");
        }
    }

    private static void vote(Scanner scanner) {
        System.out.println("\nДоступные голосования:");
        List<Voting> activeVotings = new ArrayList<>();

        for (Voting voting : votings) {
            if (LocalDate.now().isBefore(voting.endDate)) {
                activeVotings.add(voting);
                System.out.println((activeVotings.size()) + ". " + voting.name + " (до " + voting.endDate.format(dateFormatter) + ")");
            }
        }

        if (activeVotings.isEmpty()) {
            System.out.println("Нет активных голосований.");
            return;
        }

        System.out.print("Выберите голосование: ");
        int votingChoice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (votingChoice < 0 || votingChoice >= activeVotings.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        Voting selectedVoting = activeVotings.get(votingChoice);

        System.out.println("\nКандидаты:");
        List<Candidate> votingCandidates = new ArrayList<>(selectedVoting.results.keySet());
        for (int i = 0; i < votingCandidates.size(); i++) {
            System.out.println((i+1) + ". " + votingCandidates.get(i).fullName);
        }

        System.out.print("Выберите кандидата: ");
        int candidateChoice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (candidateChoice < 0 || candidateChoice >= votingCandidates.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        Candidate selectedCandidate = votingCandidates.get(candidateChoice);
        selectedVoting.results.put(selectedCandidate, selectedVoting.results.getOrDefault(selectedCandidate, 0) + 1);
        System.out.println("Ваш голос за " + selectedCandidate.fullName + " учтен.");
    }

    private static void listCandidatesForUser() {
        System.out.println("\nСписок кандидатов:");
        for (Candidate candidate : candidates.values()) {
            System.out.println(candidate.fullName);
            if (candidate.bio != null) {
                System.out.println("Биография: " + candidate.bio);
            }
            if (candidate.program != null) {
                System.out.println("Программа: " + candidate.program);
            }
            System.out.println();
        }
    }

    private static void listVotingsForUser() {
        System.out.println("\nВсе голосования:");
        for (Voting voting : votings) {
            System.out.println(voting.name + " (до " + voting.endDate.format(dateFormatter) + ")");
            System.out.println("Кандидаты:");
            for (Candidate candidate : voting.results.keySet()) {
                System.out.println("- " + candidate.fullName);
            }
            System.out.println();
        }
    }

    // Классы для хранения данных
    static class User {
        String login;
        String password;
        String fullName;
        String role;
        String snils;
        String birthDate;

        User(String login, String password, String fullName, String role) {
            this(login, password, fullName, role, null, null);
        }

        User(String login, String password, String fullName, String role, String snils, String birthDate) {
            this.login = login;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
            this.snils = snils;
            this.birthDate = birthDate;
        }

        @Override
        public String toString() {
            return "Логин: " + login + ", ФИО: " + fullName + ", Роль: " + role +
                    (snils != null ? ", СНИЛС: " + snils : "") +
                    (birthDate != null ? ", Дата рождения: " + birthDate : "");
        }
    }

    static class Cik extends User {
        String name;

        Cik(String login, String password, String name) {
            super(login, password, name, "cik");
            this.name = name;
        }

        @Override
        public String toString() {
            return "ЦИК: " + name + " (логин: " + login + ")";
        }
    }

    static class Candidate extends User {
        String bio;
        String program;

        Candidate(String login, String password, String fullName) {
            super(login, password, fullName, "candidate");
        }

        @Override
        public String toString() {
            return "Кандидат: " + fullName + " (логин: " + login + ")" +
                    (bio != null ? "\nБиография: " + bio : "") +
                    (program != null ? "\nПрограмма: " + program : "");
        }
    }

    static class Voting {
        String name;
        LocalDate endDate;
        Map<Candidate, Integer> results;

        Voting(String name, LocalDate endDate) {
            this.name = name;
            this.endDate = endDate;
            this.results = new HashMap<>();

            // Добавляем всех кандидатов в голосование с нулевыми результатами
            for (Candidate candidate : candidates.values()) {
                results.put(candidate, 0);
            }
        }
    }
}
