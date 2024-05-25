package domain;

public class ShiftType {
    public String name;
    MinMaxTuple minMaxConsecutive;


    public ShiftType(String name, MinMaxTuple minMaxConsecutive) {
        this.name = name;
        this.minMaxConsecutive = minMaxConsecutive;
    }
}
