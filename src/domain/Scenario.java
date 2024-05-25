package domain;

import java.util.Map;

public class Scenario {
    public String id;
    public int weekAmount;
    public Skill[] skills;
    public Map<String, ShiftType[]> forbiddenSuccession;
    // array index = week index
    // String 1 = Shift name
    // String 2 = Role name
    // MinMaxTuple = minimum and optimal occupation
    public MinMaxTuple[][][] minShiftRequirement;

    public ShiftType[] shiftTypes;
    public Contract[] contracts;

    public int[][] history;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeekAmount() {
        return weekAmount;
    }

    public void setWeekAmount(int weekAmount) {
        this.weekAmount = weekAmount;
    }

    public Skill[] getSkills() {
        return skills;
    }

    public void setSkills(Skill[] skills) {
        this.skills = skills;
    }

    public ShiftType[] getShiftTypes() {
        return shiftTypes;
    }

    public void setShiftTypes(ShiftType[] shiftTypes) {
        this.shiftTypes = shiftTypes;
    }

    public Contract[] getContracts() {
        return contracts;
    }

    public void setContracts(Contract[] contracts) {
        this.contracts = contracts;
    }

    public Nurse[] getNurses() {
        return nurses;
    }

    public void setNurses(Nurse[] nurses) {
        this.nurses = nurses;
    }

    public Nurse[] nurses;

    public void setForbiddenSuccession(Map<String, ShiftType[]> forbiddenSuccession) {
        this.forbiddenSuccession = forbiddenSuccession;
    }

    public Map<String, ShiftType[]> getForbiddenSuccession() {
        return forbiddenSuccession;
    }

    public MinMaxTuple[][][] getMinShiftRequirement() {
        return minShiftRequirement;
    }

    public void setMinShiftRequirement(MinMaxTuple[][][] minShiftRequirement) {
        this.minShiftRequirement = minShiftRequirement;
    }

    public int[][] getHistory() {
        return history;
    }

    public void setHistory(int[][] history) {
        this.history = history;
    }
}
