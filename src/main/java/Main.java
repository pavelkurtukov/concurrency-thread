import java.util.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time

        // Логика рассчёта одной строки, вынесенная в лямбда-фунцкию для использования в потоке
        Runnable analyzeString = () -> {
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
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
        };

        // Стартуем 25 потоков анализа
        for (int i = 0; i < 25; i++) {
            Thread thread = new Thread(analyzeString);
            threads.add(thread);
            thread.start();
        }

        // Ждём завершения потоков
        for (Thread thread : threads) {
            thread.join();
        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
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
