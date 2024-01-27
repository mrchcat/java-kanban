public class Task {
    public final Integer id;
    public final Type type;
    public final String name;
    public final String description;
    public final Status status;
    public final Integer epicId;

    public Task(Integer id, Type type, String name, String description, Status status, Integer epicId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.epicId = epicId;
    }

    public static Task makeStandardTask(String name, String description) {
        return new Task(null, Type.STANDARD, name, description, null, null);
    }

    public static Task makeStandardTask(String name, String description, Status status) {
        return new Task(null, Type.STANDARD, name, description, status, null);
    }


    public static Task makeEpicTask(String name, String description) {
        return new Task(null, Type.EPIC, name, description, null, null);
    }


    public static Task makeSubTask(String name, String description, Integer epicId) {
        return new Task(null, Type.SUB, name, description, null, epicId);
    }

    public static Task makeSubTask(String name, String description, Status status, Integer epicId) {
        return new Task(null, Type.SUB, name, description, status, epicId);
    }


    @Override
    public String toString() {
        return "<id=" + id + "; type=" + type + "; name=" + name + "; desc=" + description + "; status=" + status + "; epic=" + epicId + ">";
    }
}
