package domain;

public class Nurse {
    public int id;
    public String name;
    public Skill[] skills;
    public Contract contract;

    public Nurse(int id, String name, Contract contract, Skill[] skills) {
        this.id = id;
        this.contract = contract;
        this.name = name;
        this.skills = skills;
    }
}
