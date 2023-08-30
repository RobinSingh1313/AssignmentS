import java.util.*;

class FlippingCoins{
    public static void main(String[] args) {
        List<Map.Entry<String, Integer>> outcomes = new ArrayList<>();
        outcomes.add(new AbstractMap.SimpleEntry<>("Head", 35));
        outcomes.add(new AbstractMap.SimpleEntry<>("Tail", 65));

        Map<String, Integer> eventResults = simulateEvent(outcomes, 1000);

        System.out.println("Simulated Results:");
        for (Map.Entry<String, Integer> entry : eventResults.entrySet()) {
            String outcome = entry.getKey();
            int count = entry.getValue();
            double percentage = ((double) count / 1000) * 100;
            System.out.printf("%s: %d times (%.2f%%)%n", outcome, count, percentage);
        }

        System.out.println("\nExpected Biasness:");
        for (Map.Entry<String, Integer> outcome : outcomes) {
            String outcomeName = outcome.getKey();
            int prob = outcome.getValue();
            System.out.printf("%s: %d%%%n", outcomeName, prob);
        }
    }

    public static Map<String, Integer> simulateEvent(List<Map.Entry<String, Integer>> outcomes, int numOccurrences) {
        Map<String, Integer> eventResults = new HashMap<>();
        for (Map.Entry<String, Integer> outcome : outcomes) {
            eventResults.put(outcome.getKey(), 0);
        }

        Random random = new Random();

        for (int i = 0; i < numOccurrences; i++) {
            int randNum = random.nextInt(100) + 1;
            int cumulativeProb = 0;

            for (Map.Entry<String, Integer> outcome : outcomes) {
                String outcomeName = outcome.getKey();
                int prob = outcome.getValue();

                cumulativeProb += prob;
                if (randNum <= cumulativeProb) {
                    eventResults.put(outcomeName, eventResults.get(outcomeName) + 1);
                    break;
                }
            }
        }

        return eventResults;
    }

}
