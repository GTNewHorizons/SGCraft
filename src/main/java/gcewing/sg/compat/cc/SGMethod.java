package gcewing.sg.compat.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import gcewing.sg.tileentities.SGInterfaceTE;

abstract class SGMethod extends CCMethod {

    private static final Object[] success = { true };

    public SGMethod(String name) {
        super(name);
    }

    public SGMethod(String name, int nargs) {
        super(name, nargs);
    }

    @Override
    Object[] call(IComputerAccess cpu, ILuaContext ctx, Object target, Object[] args) {
        try {
            SGInterfaceTE te = ((CCSGPeripheral) target).getInterfaceTE();
            if (te != null) {
                Object[] result = call(te, args);
                if (result == null) result = success;
                return result;
            } else throw new IllegalArgumentException("Stargate interface failed internal diagnostics");
        } catch (Exception e) {
            return new Object[] { null, e.getMessage() };
        }
    }

    abstract Object[] call(SGInterfaceTE te, Object[] args);

}
