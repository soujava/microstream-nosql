package os.expert.integration.microstream;

import jakarta.data.exceptions.MappingException;

import java.lang.reflect.Field;
import java.util.Objects;

final class FieldMetadata {

    private final Field field;
    private final String name;

    private FieldMetadata(Field field, String name) {
        this.field = field;
        this.name = name;
    }

    Field field() {
        return field;
    }

    String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldMetadata that = (FieldMetadata) o;
        return Objects.equals(field, that.field) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, name);
    }

    @Override
    public String toString() {
        return "FieldMetadata{" +
                "field=" + field +
                ", name='" + name + '\'' +
                '}';
    }

    <T> Object get(T entity) {
        try {
            return this.field.get(entity);
        } catch (IllegalAccessException e) {
            throw new MappingException("It cannot access the value from the field " + field + " at the entity "
            + entity.getClass());
        }
    }

    static FieldMetadata of(Field field) {
        return new FieldMetadata(field, field.getName());
    }
}