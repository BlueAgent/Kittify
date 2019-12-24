package kittify.common.compat;

import java.util.Map;

public abstract class CompatBase implements Map.Entry<String, CompatBase> {
    private final String id, name;

    public CompatBase(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract void init();

    @Override
    public String getKey() {
        return id;
    }

    @Override
    public CompatBase getValue() {
        return this;
    }

    @Override
    public CompatBase setValue(CompatBase value) {
        throw new UnsupportedOperationException("Value cannot be set");
    }
}
