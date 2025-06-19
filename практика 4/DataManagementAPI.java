import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataManagementAPI {
    // Database simulation
    private Map<String, DataItem> database = new HashMap<>();
    // Cache for read-only items
    private Map<String, DataItem> cache = new HashMap<>();
    // Timer for periodic updates
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Flag to simulate single user access
    private boolean isInUse = false;

    // Data item representation
    private static class DataItem {
        String id;
        Object data;
        boolean isReadOnly;
        long lastUpdated;

        DataItem(String id, Object data, boolean isReadOnly) {
            this.id = id;
            this.data = data;
            this.isReadOnly = isReadOnly;
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    public DataManagementAPI() {
        // Инициализация периодического обновления данных
        scheduler.scheduleAtFixedRate(this::updateNonReadOnlyData, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Подключение к базе данных
     */
    public synchronized void connect() {
        if (isInUse) {
            throw new IllegalStateException("База данных уже используется другим пользователем");
        }
        isInUse = true;
        System.out.println("Успешное подключение к БД");
    }

    /**
     * Отключение от базы данных
     */
    public synchronized void disconnect() {
        isInUse = false;
        System.out.println("Отключение от БД выполнено");
    }

    /**
     * Загрузка данных в систему
     * @param id - идентификатор записи
     * @param data - данные для хранения
     * @param isReadOnly - флаг "только для чтения"
     */
    public synchronized void uploadData(String id, Object data, boolean isReadOnly) {
        checkConnection();
        DataItem item = new DataItem(id, data, isReadOnly);
        database.put(id, item);

        if (isReadOnly) {
            cache.put(id, item);
        }

        System.out.println("Данные загружены для ID: " + id);
    }

    /**
     * Получение данных по идентификатору
     * @param id - идентификатор записи
     * @return запрошенные данные
     */
    public synchronized Object getData(String id) {
        checkConnection();

        DataItem item = database.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Данные для указанного ID не найдены: " + id);
        }

        // Для данных только для чтения берем из кэша
        if (item.isReadOnly) {
            System.out.println("Возврат данных только для чтения из кэша (ID: " + id + ")");
            return cache.get(id).data;
        }

        // Для изменяемых данных получаем свежую версию из БД
        System.out.println("Возврат актуальных данных из БД (ID: " + id + ")");
        return item.data;
    }

    /**
     * Формирование отчета по нескольким записям
     * @param ids - массив идентификаторов
     * @return данные отчета
     */
    public synchronized Map<String, Object> generateReport(String[] ids) {
        checkConnection();
        Map<String, Object> report = new HashMap<>();

        for (String id : ids) {
            DataItem item = database.get(id);
            if (item != null) {
                // Для изменяемых данных получаем свежую версию
                if (!item.isReadOnly) {
                    item = database.get(id); // Получаем актуальные данные из БД
                }
                report.put(id, item.data);
            }
        }

        System.out.println("Отчет сформирован для " + ids.length + " записей");
        return report;
    }

    /**
     * Выгрузка результатов
     * @param ids - массив идентификаторов
     * @return результаты
     */
    public synchronized Map<String, Object> downloadResults(String[] ids) {
        return generateReport(ids); // Аналогично формированию отчета
    }

    /**
     * Периодическое обновление изменяемых данных
     */
    private synchronized void updateNonReadOnlyData() {
        if (!isInUse) return;

        System.out.println("Запуск периодического обновления изменяемых данных");
        for (DataItem item : database.values()) {
            if (!item.isReadOnly) {
                // Имитация обновления данных из источника
                item.lastUpdated = System.currentTimeMillis();
                System.out.println("Данные обновлены для ID: " + item.id);
            }
        }
    }

    /**
     * Проверка подключения к БД
     */
    private void checkConnection() {
        if (!isInUse) {
            throw new IllegalStateException("Нет подключения к базе данных");
        }
    }

    /**
     * Завершение работы API
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        System.out.println("Работа API завершена");
    }

    // Пример использования
    public static void main(String[] args) {
        DataManagementAPI api = new DataManagementAPI();

        try {
            // Подключение к БД
            api.connect();

            // Загрузка данных
            api.uploadData("user1", "Данные пользователя 1", true); // только для чтения
            api.uploadData("metrics1", "Метрики системы 1", false); // изменяемые данные
            api.uploadData("config1", "Конфигурация 1", true); // только для чтения

            // Получение данных
            System.out.println("Данные пользователя: " + api.getData("user1"));
            System.out.println("Метрики системы: " + api.getData("metrics1"));

            // Формирование отчета
            String[] ids = {"user1", "metrics1", "config1"};
            Map<String, Object> report = api.generateReport(ids);
            System.out.println("Отчет: " + report);

            // Выгрузка результатов
            Map<String, Object> results = api.downloadResults(ids);
            System.out.println("Результаты: " + results);

        } finally {
            // Отключение от БД
            api.disconnect();
            api.shutdown();
        }
    }
}
