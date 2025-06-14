import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Main {


    public static void main(String[] args) {

        DataBase dataBase = new DataBase();
        System.out.println(DataBase.getWorkbook());



        // Создаем latch для ожидания завершения всех потоков
        CountDownLatch latch = new CountDownLatch(DataBase.getArrayEmployees().size());

        // Запуск работников
        List<Thread> threads = new ArrayList<>();
        for (Employee employee : DataBase.getArrayEmployees()) {
            Thread thread = new Thread(() -> {
                employee.run();
                latch.countDown();
            });
            threads.add(thread);
            thread.start();
        }

        // Ожидаем завершения всех потоков
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        DataBase.saveToExcel();
    }


}