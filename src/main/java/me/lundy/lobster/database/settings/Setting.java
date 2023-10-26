package me.lundy.lobster.database.settings;

public class Setting<T> {

    private T type;
    private final String path;
    private final String name;
    private final String description;
    private Object value;

    public Setting(String path, String name, String description) {
        this.path = path;
        this.name = name;
        this.description = description;
    }

    public T getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setType(T type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }
}
