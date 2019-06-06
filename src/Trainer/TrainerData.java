package Trainer;

public class TrainerData {

    private int id;
    private String name;
    private String surName;
    private String email;
    private String branch;

    public TrainerData(int id, String name, String surName, String eMail, String branch) {
        this.id = id;
        this.name = name;
        this.surName = surName;
        this.email = eMail;
        this.branch = branch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
