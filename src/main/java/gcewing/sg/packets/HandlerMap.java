package gcewing.sg.packets;

import java.lang.reflect.Method;

enum HandlerMap {

    SERVER(BaseDataChannel.ServerMessageHandler.class) {

        protected String annotationValue(Object a) {
            return ((BaseDataChannel.ServerMessageHandler) a).value();
        }
    },

    CLIENT(BaseDataChannel.ClientMessageHandler.class) {

        protected String annotationValue(Object a) {
            return ((BaseDataChannel.ClientMessageHandler) a).value();
        }
    };

    protected Class type;
    protected ClassCache classCache = new ClassCache();

    HandlerMap(Class type) {
        this.type = type;
    }

    // This method exists because annotation classes can't be extended. :-(
    protected abstract String annotationValue(Object a);

    public Method get(Object handler, String message) {
        Class cls = handler.getClass();
        MethodCache cache = classCache.get(cls);
        Method meth = cache.get(message);
        if (meth == null) {
            for (Method m : cls.getMethods()) {
                Object a = m.getAnnotation(type);
                if (a != null && annotationValue(a).equals(message)) {
                    cache.put(message, m);
                    meth = m;
                    break;
                }
            }
        }
        return meth;
    }
}