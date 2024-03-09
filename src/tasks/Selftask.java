package tasks;

public class Selftask extends Task {

    public Selftask(String name, String description) {
        super(name, description);
    }

    @Override
    public Subordination getSubordination() {
        return Subordination.SELF;
    }

    @Override
    public String toString() {
        return "<Standard: id=" + super.getId() + "; name=" + super.getName() + "; desc=" +
                super.getDescription() + "; status=" + super.getStatus() + ">";
    }

    @Override
    public Task copy() {
        Selftask copy = new Selftask(getName(), getDescription());
        copy.setId(getId());
        copy.setStatus(getStatus());
        return copy;
    }

}
