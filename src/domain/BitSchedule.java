package domain;

import Helper.Helper;

import java.lang.ref.Cleaner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BitSchedule implements Comparable<BitSchedule> {
    Map<ShiftType, Integer> shiftTypeIntegerMap;
    Map<Integer, ShiftType> integerShiftTypeMap;
    Scenario scenario;


    int nurseAmount;
    int weekAmount;
    int shiftTypeAmount;
    public int codeLength;

    int daysAWeek = 7;
    String code;
    int score;
    Random random;

    public BitSchedule(Scenario scenario) {
        this.random = new Random();
        this.scenario = scenario;
        this.nurseAmount = scenario.getNurses().length;
        this.weekAmount = scenario.weekAmount;
        this.shiftTypeAmount = scenario.getShiftTypes().length - 1;
        this.codeLength = nurseAmount * daysAWeek * weekAmount * (shiftTypeAmount > 4 ? 3 : 2);

        shiftTypeIntegerMap = new HashMap<>();
        integerShiftTypeMap = new HashMap<>();
        for (int i = 0; i < scenario.shiftTypes.length; i++) {
            shiftTypeIntegerMap.put(scenario.shiftTypes[i], i);
            integerShiftTypeMap.put(i, scenario.shiftTypes[i]);
        }
    }


    public BitSchedule[] generateGenerationFromParents(BitSchedule father, int generationSize, double mutationFactor) {
        return generateGenerationFromParents(father, generationSize, mutationFactor, .7);
    }

    // generate generation from parents
    public BitSchedule[] generateGenerationFromParents(BitSchedule father, int generationSize, double mutationFactor, double leanFactor) {
        BitSchedule[] generation = new BitSchedule[generationSize];
        for (int j = 0; j < generationSize; j++) {
            BitSchedule individual = new BitSchedule(this.scenario);
            String indivCode = "";
            for (int i = 0; i < this.codeLength; i++) {
                double x = this.random.nextDouble();
                char c;
                if (x < leanFactor) {
                    c = this.code.charAt(i);
                } else {
                    c = father.code.charAt(i);
                }
                x = this.random.nextDouble();
                indivCode += x < .01 * mutationFactor ? (c == '0' ? '1' : '0') : c;
            }
            individual.setCode(indivCode);
            generation[j] = individual;
        }
        return generation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static BitSchedule generateRandomSchedule(Scenario scenario) {
        BitSchedule bitSchedule = new BitSchedule(scenario);
        String code = "";
        for (int i = 0; i < bitSchedule.codeLength; i++) {
            code += Math.random() < .2 ? "0" : "1";
        }
        bitSchedule.code = code;
        return bitSchedule;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore() {
        if (this.score != 0) return;
        int score = 0;
        /*
         * hard constraint:
         *   all minimum requirements for shifts are met, for each shift
         */

        // view it week by week
        for (int w = 0; w < weekAmount; w++) {
            for (int d = 0; d < 7; d++) {
                // Keep a count for every role
                int[] count = new int[scenario.skills.length * (shiftTypeAmount)];
                for (int n = 0; n < nurseAmount; n++) {
                    // hardcoded
                    int index = n * 56 + d * 8 + w * 2;
                    String shift = code.substring(index, index + 2);
                    int shiftValue = Helper.skillStringToIndex(shift);

                    // even: type 1
                    // uneven: skill 2
                    // |Early 1| E 2| Late 1| L2| Night 1| N 1|
                    if (shiftValue == 0) continue;
                    count[(scenario.nurses[n].skills.length - 1) + 2 * (shiftValue - 1)]++;
                }
                MinMaxTuple[] minReq = scenario.getMinShiftRequirement()[w][d];

                for (int i = 0; i < count.length; i++) {
                    // HARD constraint: enough workers?
                    if (count[i] < minReq[i].min) {
                        score += 1000000;
                    } else if (count[i] > minReq[i].max) {
                        // SOFT constraint: more than optimal amount of workers?
                        score += (count[i] - minReq[i].max) * 30;
                    }
                }
            }
        }
        // view it nurse by nurse
        for (int n = 0; n < nurseAmount; n++) {
            int cAH = scenario.history[n][4]; // consecutive assignment history
            int dOCAM = 0; // days over (or under) consecutive assignment max

            Pair<ShiftType, Integer> cSH = new Pair<>(
                    integerShiftTypeMap.get(scenario.history[n][2]), // Last shift according to history
                    scenario.history[n][3] // consecutive shift according to history
            );
            int dOCSM = 0;

            int cFDH = scenario.history[n][5]; // consecutive free day history
            int dOCFDM = 0; // days over (or under) consecutive free day max

            int tAIC = scenario.history[n][0]; // total assignments in contract

            boolean worksOnSaturday = false;
            int tMAWW = 0; // total mistakes against working weekends
            int tNOWW = scenario.history[n][1];
            ShiftType lastShift = integerShiftTypeMap.get(scenario.history[n][2]);
            ; // total number of working weekends
            for (int w = 0; w < weekAmount; w++) {
                for (int d = 0; d < daysAWeek; d++) {
                    // index: currently hardcoded
                    int index = n * 56 + d * 8 + w * 2;
                    String shift = code.substring(index, index + 2);
                    int shiftValue = Helper.skillStringToIndex(shift);

                    ShiftType currentShift = integerShiftTypeMap.get(shiftValue);
                    ShiftType[] forbiddenOptions = scenario.forbiddenSuccession.get(lastShift.name);
                    if (forbiddenOptions != null && Arrays.asList(forbiddenOptions).contains(currentShift)) {
                        score += 1000000;
                        continue;
                    }
                    lastShift = currentShift;

                    if (shiftValue == 0) {
                        if (cAH < scenario.nurses[n].contract.minMaxConsecutiveWork.min) {
                            dOCAM += 1;
                        }
                        cAH = 0;
                        cFDH += 1;
                        if (cFDH > scenario.nurses[n].contract.minMaxConsecutiveDaysOff.max) {
                            dOCFDM += 1;
                        }
                        if (d == 6 && scenario.nurses[n].contract.allowCompleteWeekend) {
                            if (worksOnSaturday) {
                                tMAWW += 1;
                            } else {
                                tNOWW += 1;
                            }
                        }

                    } else {
                        if (cFDH < scenario.nurses[n].contract.minMaxConsecutiveDaysOff.min) {
                            dOCAM += 1;
                        }
                        cFDH = 0;
                        cAH += 1;
                        if (cAH < scenario.nurses[n].contract.minMaxConsecutiveWork.max) {
                            dOCAM += 1;
                        }
                        // if the day is Saturday
                        if (d == 5) {
                            worksOnSaturday = true;
                        }
                        if (d == 6 && scenario.nurses[n].contract.allowCompleteWeekend) {
                            if (worksOnSaturday) {
                                tNOWW += 1;
                            } else {
                                tMAWW += 1;
                            }
                        }

                        tAIC += 1;
                    }
                    if (shiftTypeIntegerMap.get(cSH.getLeft()) == shiftValue) {
                        cSH = new Pair<>(cSH.getLeft(), cSH.getRight() + 1);
                    } else {
                        if (cSH.getLeft().minMaxConsecutive.max < cSH.getRight()) {
                            dOCSM += 1;
                        }
                        cSH = new Pair<>(integerShiftTypeMap.get(shiftValue), 1);
                    }

                }
            }
            score += dOCSM * 15 + dOCAM * 30 + dOCFDM * 30 + tMAWW * 30 + tAIC * 20 + tNOWW * 30;
        }
        this.score = score;
    }

    @Override
    public String toString() {
        String hr = "|-----------" + "-----------------------------------".repeat(weekAmount) + "|\n";

        String print = hr;

        print += "| nurses \t" + "| Mo | Tu | We | Th | Fr | Sa | Su ".repeat(weekAmount) + "|\n";
        print += "|===========" + "===================================".repeat(weekAmount) + "|\n";
        // view it nurse by nurse
        for (int n = 0; n < nurseAmount; n++) {
            String name = scenario.nurses[n].name;
            print += "| " + name + (name.length() < 5 ? "\t\t" : "\t");
            for (int w = 0; w < weekAmount; w++) {
                for (int d = 0; d < daysAWeek; d++) {
                    // index: currently hardcoded
                    int index = n * 56 + d * 8 + w * 2;
                    String shift = code.substring(index, index + 2);
                    int shiftValue = Helper.skillStringToIndex(shift);
                    print += "| " + integerShiftTypeMap.get(shiftValue).name.substring(0,2) + " ";
                }
            }
            print += "|\n";
        }
        return print + hr + "Best score: " + score;
    }

    @Override
    public int compareTo(BitSchedule o) {
        return Integer.compare(this.score, o.getScore());
    }
}
