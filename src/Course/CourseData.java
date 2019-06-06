package Course;

public class CourseData {
    private int id;
    private String title;
    private String description;
    private int quota;

    public CourseData(int id, String title, String description, int quota) {
        this.id = id;
        this.title = title;
        this.quota = quota;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
