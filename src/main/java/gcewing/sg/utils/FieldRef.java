package gcewing.sg.utils;

import gcewing.sg.interfaces.Ref;

import java.lang.reflect.Field;

public class FieldRef implements Ref {

    public Object target;
    public Field field;

    public FieldRef(Object target, String name) {
        try {
            this.target = target;
            this.field = target.getClass().getField(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object get() {
        try {
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object value) {
        try {
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}