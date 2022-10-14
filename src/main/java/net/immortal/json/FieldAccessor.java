package net.immortal.json;

public interface FieldAccessor {

    Object get(Object entity);

    void set(Object entity, Object val);

}
