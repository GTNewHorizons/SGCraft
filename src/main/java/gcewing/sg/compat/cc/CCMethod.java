// ------------------------------------------------------------------------------------------------
//
// SG Craft - Computercraft Method
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.cc;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public abstract class CCMethod {

    public String name;
    public int nargs;

    abstract Object[] call(IComputerAccess cpu, ILuaContext ctx, Object target, Object[] args);

    public CCMethod(String name) {
        this(name, 0);
    }

    public CCMethod(String name, int nargs) {
        this.name = name;
        this.nargs = nargs;
    }

    public Object[] invoke(IComputerAccess cpu, ILuaContext ctx, Object target, Object[] args) throws LuaException {
        if (nargs >= 0 && args.length != nargs) throw new LuaException(
                String.format("Wrong number of arguments to %s, expected %s, got %s", name, nargs, args.length));
        return call(cpu, ctx, target, args);
    }

}
