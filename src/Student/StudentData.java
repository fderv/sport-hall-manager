package Student;

public class StudentData {

    private int id;
    private String name;
    private String surName;
    private String email;

    public StudentData(int id, String name, String surName, String eMail) {
        this.id = id;
        this.name = name;
        this.surName = surName;
        this.email = eMail;
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
}
