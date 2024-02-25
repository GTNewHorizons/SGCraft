package gcewing.sg.packets;

import java.util.HashMap;

class ClassCache extends HashMap<Class, MethodCache> {

    public MethodCache get(Class key) {
        MethodCache result = super.get(key);
        if (result == null) {
            result = new MethodCache();
            put(key, result);
        }
        return result;
    }

}
