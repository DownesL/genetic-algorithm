import domain.BitSchedule;
import domain.MinMaxTuple;
import domain.Scenario;
import data.DataReader;
import framework.GeneticAlgorithm;

import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DataReader dr = new DataReader();
        Scenario scenario = dr.readScenario("dataset/n005w4/Sc-n005w4.txt");
        String[] sources = new String[]{
                "dataset/n005w4/WD-n005w4-5.txt",
                "dataset/n005w4/WD-n005w4-3.txt",
                "dataset/n005w4/WD-n005w4-1.txt",
                "dataset/n005w4/WD-n005w4-0.txt",
        };
        MinMaxTuple[][][] minShiftRequirement = dr.readPeriodData(scenario, sources);
        scenario.setMinShiftRequirement(minShiftRequirement);
        scenario.setHistory(dr.readHistory(scenario, "dataset/n005w4/H0-n005w4-1.txt"));
        BitSchedule bitSchedule = BitSchedule.generateRandomSchedule(scenario);
        bitSchedule.setScore();
        GeneticAlgorithm gen = new GeneticAlgorithm(scenario, 120, 30, 100000);
        BitSchedule mother = gen.run();
        System.out.println(mother.toString());
    }
}