package me.lukeben.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class Accessor {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final Class<?> clazz;
    private final Map<String, FieldAccessor> fieldToAccessor = Maps.newHashMap();

    public FieldAccessor getFieldAccessor(final String fieldName) {
        final FieldAccessor ret = this.fieldToAccessor.get(fieldName);
        if (ret == null) throw new IllegalArgumentException("The field " + fieldName + " is not supported.");
        return ret;
    }

    public void setFieldAccessor(final String fieldName, final FieldAccessor fieldAccessor) {
        this.fieldToAccessor.put(fieldName, fieldAccessor);
    }

    public Collection<String> getFieldNames() {
        return this.fieldToAccessor.keySet();
    }

    // -------------------------------------------- //
    // CONSTRUCT / FACTORY
    // -------------------------------------------- //

    private static final Map<Class<?>, Accessor> classToAccessor = Maps.newHashMap();

    public static Accessor get(final Class<?> clazz) {
        Accessor ret = classToAccessor.get(clazz);
        if (ret != null) return ret;
        ret = new Accessor(clazz);
        classToAccessor.put(clazz, ret);
        return ret;
    }

    private Accessor(final Class<?> clazz) {
        this.clazz = clazz;
    }

    // -------------------------------------------- //
    // POPULATE: REFLECTION
    // -------------------------------------------- //

    public void populate() {
        final Map<String, Field> map = getFieldMap(this.clazz);

        for (final Map.Entry<String, Field> entry : map.entrySet()) {
            final String fieldName = entry.getKey();
            final Field field = entry.getValue();

            final FieldAccessor fieldAccessor = createFieldAccessor(field);
            this.setFieldAccessor(fieldName, fieldAccessor);
        }

    }

    public static FieldAccessor createFieldAccessor(final Field field) {
        return new FieldAccessorSimple(field);
    }

    // -------------------------------------------- //
    // GET & SET & COPY
    // -------------------------------------------- //

    public Object get(final Object object, final String fieldName) {
        final FieldAccessor fieldAccessor = this.getFieldAccessor(fieldName);
        return fieldAccessor.get(object);
    }

    public void set(final Object object, final String fieldName, final Object val) {
        final FieldAccessor fieldAccessor = this.getFieldAccessor(fieldName);
        fieldAccessor.set(object, val);
    }

    // Copy one only!
    public void copy(final Object from, final Object to, final String fieldName) {
        final FieldAccessor fieldAccessor = this.getFieldAccessor(fieldName);
        final Object val = fieldAccessor.get(from);
        fieldAccessor.set(to, val);
    }

    // Copy a few!
    public void copy(final Object from, final Object to, final Collection<String> fieldNames) {
        for (final String fieldName : fieldNames) {
            this.copy(from, to, fieldName);
        }
    }

    // Copy them all!
    public void copy(final Object from, final Object to) {
        for (final FieldAccessor fieldAccessor : this.getFieldToAccessor().values()) {
            final Object val = fieldAccessor.get(from);
            fieldAccessor.set(to, val);
        }
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public static List<Field> getFieldList(final Class<?> clazz) {
        final List<Field> fields = Lists.newArrayList();

        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static Map<String, Field> getFieldMap(final Class<?> clazz) {
        final Map<String, Field> ret = Maps.newHashMap();

        for (final Field field : getFieldList(clazz)) {
            if(Modifier.isTransient(field.getModifiers())) continue;
            if(Modifier.isFinal(field.getModifiers())) continue;
            final String fieldName = field.getName();
            if (ret.containsKey(fieldName)) continue;
            ret.put(fieldName, field);
        }

        return ret;
    }

}

