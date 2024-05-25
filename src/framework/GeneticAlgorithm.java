package framework;

import domain.BitSchedule;
import domain.Scenario;

import java.util.*;
import java.util.stream.Stream;

public class GeneticAlgorithm  {
    Scenario scenario;
    int generationSize;
    int competitionSize;
    int iterations;

    public GeneticAlgorithm(Scenario scenario, int generationSize, int competitionSize, int iterations) {
        this.scenario = scenario;
        this.generationSize = generationSize;
        this.competitionSize = competitionSize;
        this.iterations = iterations;
    }
    public BitSchedule run() {
        /*
         * Generate random generation of individuals
         *
         * for loop over iteration amount
         *
         *  pick 10 unique random individuals that are at least
         *      almost (score + 100) as good as the original
         *
         *  save the 2 best
         *
         *   generate new generation of 100 individuals
         *      based on parents
         * */
        BitSchedule[] generation = Stream
                .generate(() -> BitSchedule.generateRandomSchedule(scenario))
                .limit(this.generationSize)
                .toArray(BitSchedule[]::new);
        BitSchedule mother = new BitSchedule(scenario);
        BitSchedule father = new BitSchedule(scenario);
        int continueCount = 0;
        int lastUpdate = 0;
        for (int i = 0; i < iterations; i++) {
            double mutationFactor = continueCount % 6;
//            double mutationFactor =  Math.log( i / (lastUpdate + 1.0) * continueCount / competitionSize * generationSize);
            int[] randomIndexes = generateRandomIndexes(this.generationSize, generationSize);

            BitSchedule[] tempGeneration = generation;
            BitSchedule[] parents = getParents(i - lastUpdate > 5000 ? new int[]{} : randomIndexes, tempGeneration);


            // todo fix this
            if (i > 0 && parents[0].getScore() >= mother.getScore()) {
                if ((i - lastUpdate) % 5000 == 0) {
                    father = BitSchedule.generateRandomSchedule(scenario);
                    father.setScore();
                    generation = mother.generateGenerationFromParents(father, this.generationSize, 0, .90);
                } else if (i - lastUpdate > 5000) {
                    continueCount += 1;
                    generation = mother.generateGenerationFromParents(father, this.generationSize, mutationFactor, .95);
                } else {
                    continueCount += 1;
                    generation = mother.generateGenerationFromParents(father, this.generationSize, mutationFactor);
                }
                continue;
            }

            mother = parents[0];
            father = parents[1];
            System.out.println(mother.getScore() + "\t\t" + i);
            continueCount = 0;
            generation = mother.generateGenerationFromParents(father, this.generationSize, mutationFactor);
            lastUpdate = i;
        }

        return mother;
    }

    private static BitSchedule[] getParents(int[] randomIndexes, BitSchedule[] tempGeneration) {
        if (randomIndexes.length == 0) {
            return Arrays
                    .stream(tempGeneration)
                    .peek(BitSchedule::setScore)
                    .sorted()
                    .limit(2)
                    .toArray(BitSchedule[]::new);
        }
        BitSchedule[] parents = Arrays
                .stream(randomIndexes)
                .mapToObj(index -> tempGeneration[index])
                .peek(BitSchedule::setScore)
                .sorted()
                .limit(2)
                .toArray(BitSchedule[]::new);
        return parents;
    }

    private int[] generateRandomIndexes(int numbersNeeded, int max) {
        Random random = new Random();

        Set<Integer> generated = new HashSet<>();
        while (generated.size() < numbersNeeded) {
            Integer next = random.nextInt(max);

            generated.add(next);
        }
        return generated.stream().mapToInt(Number::intValue).toArray();
    }
}
