package gcewing.sg.utils;

import gcewing.sg.guis.BaseGui;
import gcewing.sg.interfaces.Action;

import java.lang.reflect.Method;

public class MethodAction implements Action {

    Object target;
    Method method;

    public MethodAction(Object target, String name) {
        try {
            this.target = target;
            method = target.getClass().getMethod(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void perform() {
        try {
            method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
