package domain;

public class Contract {
    public String name;
    MinMaxTuple minMaxAssignments;
    MinMaxTuple minMaxConsecutiveWork;
    MinMaxTuple minMaxConsecutiveDaysOff;
    int maxNumberWorkWeekends;
    boolean allowCompleteWeekend;

    public Contract(String name, MinMaxTuple minMaxAssignments, MinMaxTuple minMaxConsecutiveWork, MinMaxTuple minMaxConsecutiveDaysOff, int maxNumberWorkWeekends, boolean allowCompleteWeekend) {
        this.name = name;
        this.minMaxAssignments = minMaxAssignments;
        this.minMaxConsecutiveWork = minMaxConsecutiveWork;
        this.minMaxConsecutiveDaysOff = minMaxConsecutiveDaysOff;
        this.maxNumberWorkWeekends = maxNumberWorkWeekends;
        this.allowCompleteWeekend = allowCompleteWeekend;
    }

    public MinMaxTuple getMinMaxAssignments() {
        return minMaxAssignments;
    }

    public void setMinMaxAssignments(MinMaxTuple minMaxAssignments) {
        this.minMaxAssignments = minMaxAssignments;
    }

    public MinMaxTuple getMinMaxConsecutiveWork() {
        return minMaxConsecutiveWork;
    }

    public void setMinMaxConsecutiveWork(MinMaxTuple minMaxConsecutiveWork) {
        this.minMaxConsecutiveWork = minMaxConsecutiveWork;
    }

    public MinMaxTuple getMinMaxConsecutiveDaysOff() {
        return minMaxConsecutiveDaysOff;
    }

    public void setMinMaxConsecutiveDaysOff(MinMaxTuple minMaxConsecutiveDaysOff) {
        this.minMaxConsecutiveDaysOff = minMaxConsecutiveDaysOff;
    }

    public int getMaxNumberWorkWeekends() {
        return maxNumberWorkWeekends;
    }

    public void setMaxNumberWorkWeekends(int maxNumberWorkWeekends) {
        this.maxNumberWorkWeekends = maxNumberWorkWeekends;
    }

    public boolean isAllowCompleteWeekend() {
        return allowCompleteWeekend;
    }

    public void setAllowCompleteWeekend(boolean allowCompleteWeekend) {
        this.allowCompleteWeekend = allowCompleteWeekend;
    }
}
