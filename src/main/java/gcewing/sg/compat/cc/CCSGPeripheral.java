// ------------------------------------------------------------------------------------------------
//
// SG Craft - Computercraft Interface Peripheral
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.cc;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import gcewing.sg.SGCraft;
import gcewing.sg.tileentities.SGInterfaceTE;
import gcewing.sg.utils.CIStargateState;

public class CCSGPeripheral implements IPeripheral {

    static CCMethod[] methods = {

            new SGMethod("stargateState") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    CIStargateState result = te.ciStargateState();
                    return new Object[] { result.state, result.chevrons, result.direction };
                }
            },

            new SGMethod("energyAvailable") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    return new Object[] { te.ciEnergyAvailable() };
                }
            },

            new SGMethod("energyToDial", 1) {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    return new Object[] { te.ciEnergyToDial((String) args[0]) };
                }
            },

            new SGMethod("localAddress") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    return new Object[] { te.ciLocalAddress() };
                }
            },

            new SGMethod("remoteAddress") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    return new Object[] { te.ciRemoteAddress() };
                }
            },

            new SGMethod("dial", 1) {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    te.ciDial((String) args[0]);
                    return null;
                }
            },

            new SGMethod("disconnect") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    te.ciDisconnect();
                    return null;
                }
            },

            // new SGMethod("direction") {
            // Object[] call(SGInterfaceTE te, Object[] args) {
            // return new Object[] {te.ciDirection()};
            // }
            // },

            new SGMethod("irisState") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    return new Object[] { te.ciIrisState() };
                }
            },

            new SGMethod("openIris") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    te.ciOpenIris();
                    return null;
                }
            },

            new SGMethod("closeIris") {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    te.ciCloseIris();
                    return null;
                }
            },

            new SGMethod("sendMessage", -1) {

                Object[] call(SGInterfaceTE te, Object[] args) {
                    te.ciSendMessage(args);
                    return null;
                }
            },

    };

    World worldObj;
    int xCoord, yCoord, zCoord;

    public CCSGPeripheral(TileEntity te) {
        worldObj = te.getWorldObj();
        xCoord = te.xCoord;
        yCoord = te.yCoord;
        zCoord = te.zCoord;
    }

    CCInterfaceTE getInterfaceTE() {
        TileEntity te = worldObj.getTileEntity(xCoord, yCoord, zCoord);
        if (te instanceof CCInterfaceTE) return (CCInterfaceTE) te;
        else return null;
    }

    public String getType() {
        return "stargate";
    }

    public String[] getMethodNames() {
        String[] result = new String[methods.length];
        for (int i = 0; i < methods.length; i++) result[i] = methods[i].name;
        return result;
    }

    public Object[] callMethod(IComputerAccess cpu, ILuaContext ctx, int method, Object[] args)
            throws LuaException, InterruptedException {
        if (method >= 0 && method < methods.length)
            return CCMethodQueue.instance.invoke(cpu, ctx, this, methods[method], args);
        else throw new LuaException(String.format("Invalid method index %s", method));
    }

    public void attach(IComputerAccess cpu) {
        SGCraft.log.debug(String.format("CCSGPeripheral.attach: to %s", cpu));
        CCInterfaceTE te = getInterfaceTE();
        if (te != null) te.attachedComputers.add(cpu);
    }

    public void detach(IComputerAccess cpu) {
        SGCraft.log.debug(String.format("CCSGPeripheral.detach: from %s", cpu));
        CCInterfaceTE te = getInterfaceTE();
        if (te != null) te.attachedComputers.remove(cpu);
    }

    public boolean equals(IPeripheral other) {
        return this == other;
    }

}
