package net.immortalapi.json;

public interface FieldAccessor {

    Object get(Object entity);

    void set(Object entity, Object val);

}
