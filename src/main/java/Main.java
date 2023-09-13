import java.util.*;
import java.util.concurrent.*;

public class Main {
    final static int STRING_COUNT = 25;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int[] results = new int[STRING_COUNT]; // Массив с результатами анализа
        int totalMax = 0; // Максимальная длина интервала значений
        List<Future<Integer>> tasks = new ArrayList<>(); // Список заданий для отправки в пул потоков


        long startTs = System.currentTimeMillis(); // start time

        final ExecutorService threadPool = Executors.newFixedThreadPool(STRING_COUNT); // Определяем пул потоков

        // Логика рассчёта одной строки, вынесенная в лямбда-функцию для использования в потоке
        Callable<Integer> analyzeString = () -> {
            String text = generateText("aab", 30_000);
            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            // Всё равно будем выводить результат анализа на экран для проверки результата
            System.out.println(text.substring(0, 100) + " -> " + maxSize);

            return maxSize;
        };

        // Стартуем 25 процессов анализа, добавляя их в пул потоков
        for (int i = 0; i < STRING_COUNT; i++) {
            tasks.add(threadPool.submit(analyzeString));
        }

        // Ожидаем, читаем результат работы заданий и определяем максимальный интервал
        for (Future<Integer> task : tasks) {
            int currentTaskMaxSize = task.get();
            if (currentTaskMaxSize > totalMax) totalMax = currentTaskMaxSize;
        }

        // Завершаем работу пула потоков
        threadPool.shutdown();

        // Выводим результат на экран
        System.out.println("Максимальная длина интервала значений равна " + totalMax);

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + " ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
