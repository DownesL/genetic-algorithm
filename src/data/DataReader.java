package data;

import domain.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.regex.Pattern;

public class DataReader {
    public Scenario readScenario(String source) {
        File tsf = new File(source);
        Scanner scenarioScanner;
        Scenario scenario = new Scenario();
        try {
            scenarioScanner = new Scanner(tsf);
            String nextLine = scenarioScanner.nextLine();
            scenario.setId(nextLine.substring(11));

            scenarioScanner.nextLine();

            nextLine = scenarioScanner.nextLine();

            scenario.setWeekAmount(Integer.parseInt(nextLine.substring(8)));

            scenarioScanner.nextLine();
            nextLine = scenarioScanner.nextLine();


            int len = Integer.parseInt(nextLine.substring(9));
            Skill[] skills = new Skill[len];
            for (int i = 0; i < len; i++) {
                nextLine = scenarioScanner.nextLine();
                Skill skill = new Skill(nextLine);
                skills[i] = skill;
            }
            scenario.setSkills(skills);

            scenarioScanner.nextLine();
            nextLine = scenarioScanner.nextLine();

            len = Integer.parseInt(nextLine.substring(14));
            ShiftType[] shiftTypes = new ShiftType[len + 1];
            shiftTypes[0] = new ShiftType("None", new MinMaxTuple("(0,0)"));
            for (int i = 1; i <= len; i++) {
                nextLine = scenarioScanner.nextLine();
                Scanner sc = new Scanner(nextLine);
                String name = sc.next();
                MinMaxTuple minMax = new MinMaxTuple(sc.next());
                ShiftType shiftType = new ShiftType(name, minMax);
                shiftTypes[i] = shiftType;
            }
            scenario.setShiftTypes(shiftTypes);

            scenarioScanner.nextLine();
            nextLine = scenarioScanner.nextLine();

            Map<String, ShiftType[]> forbiddenSuccession = new HashMap<>();
            for (int i = 0; i < len; i++) {
                nextLine = scenarioScanner.nextLine();
                Scanner sc = new Scanner(nextLine);
                String name = sc.next();
                int fsAmount = sc.nextInt();
                ShiftType[] stArr = new ShiftType[fsAmount];
                for (int j = 0; j < fsAmount; j++) {
                    String sht = sc.next();
                    stArr[j] = Arrays.stream(shiftTypes)
                            .filter(shiftType -> Objects.equals(shiftType.name, sht))
                            .toList()
                            .getFirst();
                }
                forbiddenSuccession.put(name, stArr);
            }
            scenario.setForbiddenSuccession(forbiddenSuccession);

            scenarioScanner.nextLine();
            nextLine = scenarioScanner.nextLine();

            len = Integer.parseInt(nextLine.substring(12));
            Contract[] contracts = new Contract[len];
            for (int i = 0; i < len; i++) {
                nextLine = scenarioScanner.nextLine();
                Scanner sc = new Scanner(nextLine);
                String name = sc.next();
                contracts[i] = new Contract(
                        name,
                        new MinMaxTuple(sc.next()),
                        new MinMaxTuple(sc.next()),
                        new MinMaxTuple(sc.next()),
                        sc.nextInt(),
                        sc.nextInt() == 1
                );
            }
            scenario.setContracts(contracts);

            scenarioScanner.nextLine();
            nextLine = scenarioScanner.nextLine();

            len = Integer.parseInt(nextLine.substring(9));
            Nurse[] nurses = new Nurse[len];
            for (int i = 0; i < len; i++) {
                nextLine = scenarioScanner.nextLine();
                Scanner sc = new Scanner(nextLine);
                String name = sc.next();
                String contractName = sc.next();
                Contract nurseContract = Arrays
                        .stream(contracts)
                        .filter(contract -> contract.name.equals(contractName))
                        .toList()
                        .getFirst();
                int nurseSkillsAmount = sc.nextInt();
                Skill[] nurseSkills = new Skill[nurseSkillsAmount];
                for (int j = 0; j < nurseSkillsAmount; j++) {
                    String temp = sc.next();
                    nurseSkills[j] = Arrays
                            .stream(skills)
                            .filter(skill -> skill.name.equals(temp))
                            .toList()
                            .getFirst();
                }
                nurses[i] = new Nurse(
                        i,
                        name,
                        nurseContract,
                        nurseSkills
                );
            }
            scenario.setNurses(nurses);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return scenario;

    }

    public MinMaxTuple[][][] readPeriodData(Scenario scenario, String[] sources) {
        // index of array = index of week
        MinMaxTuple[][][] minimumRequirement = new MinMaxTuple[scenario.weekAmount][7][];

        for (int i = 0; i < scenario.weekAmount; i++) {
            minimumRequirement[i] = readWeekData(scenario, sources[i]);
        }

        return minimumRequirement;
    }

    public MinMaxTuple[][] readWeekData(Scenario scenario, String source) {
        // shift type count
        int sTC = (scenario.shiftTypes.length - 1);
        // skill count
        int sC = scenario.skills.length;
        // minimum requirements per role
        MinMaxTuple[][] minReq = new MinMaxTuple[7][sC * sTC];
        File tsf = new File(source);
        Scanner weekdataScanner;
        try {
            weekdataScanner = new Scanner(tsf);
            weekdataScanner.nextLine();
            weekdataScanner.nextLine();
            weekdataScanner.nextLine();
            weekdataScanner.nextLine();

            for (int i = 0; i < sC * sTC; i++) {
                String shift = weekdataScanner.next();
                // String = role name
                MinMaxTuple[] minMaxTuples = new MinMaxTuple[7];
                String role = weekdataScanner.next();
                for (int j = 0; j < 7; j++) {
                    String next = weekdataScanner.next();
                    minMaxTuples[j] = new MinMaxTuple(next);
                    int dayOffset = j;
                    int roleOffset = sTC * (i % sC);
                    int shiftOffset = Math.floorDiv(i, sC);
                    minReq[dayOffset][roleOffset + shiftOffset] = new MinMaxTuple(next);

                }
            }
            weekdataScanner.nextLine();
            weekdataScanner.nextLine();
//            String line = weekdataScanner.nextLine().strip();
//            int len = Integer.parseInt(line.substring(21));
//            Map<Nurse, String> nurses = new HashMap<>();
//            for (int i = 0; i < len; i++) {
//                String[] els = weekdataScanner.nextLine().split(" ");
//                nurses.put(Arrays.stream(scenario.nurses).filter(nurse -> Objects.equals(nurse.name, els[0])).findFirst().get(), els[2]);
//
//            }


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return minReq;
    }

    public int[][] readHistory(Scenario scenario, String source) {
        // first [] : 1 / nurse
        // second [] : total num of assignments, worked weekends, last shift,
        //          num of consecutive shifts, num of consecutive worked days,
        //          number of consecutive off days
        int[][] history = new int[scenario.nurses.length][6];
        File tsf = new File(source);
        Scanner historyScanner;
        try {
            historyScanner = new Scanner(tsf);
            historyScanner.nextLine();
            historyScanner.nextLine();
            historyScanner.nextLine();
            historyScanner.nextLine();

            for (int i = 0; i < history.length; i++) {
                int[] temp = new int[6];
                historyScanner.next();
                for (int j = 0; j < 6; j++) {
                    if (j == 2) {
                        String shift = historyScanner.next();
                        for (int k = 0; k < scenario.shiftTypes.length; k++) {
                            if (scenario.shiftTypes[k].name.equals(shift)) {
                                temp[j] = k;
                                break;
                            }
                        }
                    } else {
                        temp[j] = historyScanner.nextInt();
                    }
                }
                history[i] = temp;
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return history;
    }

}
