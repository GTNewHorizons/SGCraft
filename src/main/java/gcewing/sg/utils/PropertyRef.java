package gcewing.sg.utils;

import gcewing.sg.interfaces.Ref;

import java.lang.reflect.Method;

public class PropertyRef implements Ref {

    public Object target;
    public Method getter, setter;

    public PropertyRef(Object target, String getterName, String setterName) {
        this.target = target;
        try {
            Class cls = target.getClass();
            getter = cls.getMethod(getterName);
            setter = cls.getMethod(setterName, getter.getReturnType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object get() {
        try {
            return getter.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object value) {
        try {
            setter.invoke(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
