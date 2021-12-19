package me.lukeben.json;

import java.lang.reflect.Field;

public class FieldAccessorSimple implements FieldAccessor {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final Field field;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public FieldAccessorSimple(final Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    @Override
    public Object get(final Object entity) {
        try {
            return this.field.get(entity);
        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    public void set(final Object entity, final Object val) {
        try {
            this.field.set(entity, val);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
