import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MathExpressionEvaluator {

    private static final String API_ENDPOINT = "http://api.mathjs.org/v4/";
    private static final int MAX_REQUESTS_PER_SECOND = 50;

    public static void main(String[] args) {
        List<String> expressions = new ArrayList<>();

        try (Scanner scanner = new Scanner(System.in)) {
            String expression;
            while (scanner.hasNextLine() && !(expression = scanner.nextLine()).equals("end")) {
                expressions.add(expression);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Queue<String> requestQueue = new ArrayDeque<>(expressions);
        ExecutorService executorService = Executors.newFixedThreadPool(expressions.size());

        long startTime = System.currentTimeMillis();
        int requestsThisSecond = 0;

        while (!requestQueue.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= 1000) {
                startTime = currentTime;
                requestsThisSecond = 0;
            }

            if (requestsThisSecond < MAX_REQUESTS_PER_SECOND) {
                String expression = requestQueue.poll();
                if (expression != null) {
                    executorService.execute(new ExpressionEvaluatorTask(expression));
                    requestsThisSecond++;
                }
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class ExpressionEvaluatorTask implements Runnable {
        private final String expression;

        ExpressionEvaluatorTask(String expression) {
            this.expression = expression;
        }

        @Override
        public void run() {
            try {
                String encodedExpression = URLEncoder.encode(expression, StandardCharsets.UTF_8);
                URL url = new URL(API_ENDPOINT + "?expr=" + encodedExpression);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String result = reader.readLine();
                    System.out.println(expression + " => " + result);
                    reader.close();
                } else {
                    System.err.println("Error for expression: " + expression);
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
