package gcewing.sg.compat.cc;

import java.util.concurrent.Semaphore;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

class CCCall {

    IComputerAccess cpu;
    ILuaContext ctx;
    Object target;
    CCMethod method;
    Object[] args;

    Semaphore lock = new Semaphore(0);
    Object[] result;
    LuaException exception;

    CCCall(IComputerAccess cpu, ILuaContext ctx, Object target, CCMethod method, Object[] args) {
        this.cpu = cpu;
        this.ctx = ctx;
        this.target = target;
        this.method = method;
        this.args = args;
    }

}
