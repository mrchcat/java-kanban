import java.util.ArrayList;

public class DeepTask {
    private final Type type;
    private String name;
    private String description;
    private Status status;
    private final ArrayList<Integer> subTasks;
    private final Integer epicId;

    public DeepTask(Type type, String name, String description, Status status, ArrayList<Integer> subTasks, Integer epicId) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.subTasks = subTasks;
        this.epicId = epicId;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public Integer getEpicId() {
        return epicId;
    }
}
