// ------------------------------------------------------------------------------------------------
//
// SG Craft - Computercraft Interface Tile Entity
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.compat.cc;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;

import dan200.computercraft.api.peripheral.IComputerAccess;
import gcewing.sg.interfaces.IComputerInterface;
import gcewing.sg.tileentities.SGInterfaceTE;

public class CCInterfaceTE extends SGInterfaceTE implements IComputerInterface {

    Set<IComputerAccess> attachedComputers = new HashSet<>();

    public void postEvent(TileEntity source, String name, Object... args) {
        for (IComputerAccess cpu : attachedComputers) cpu.queueEvent(name, prependArgs(cpu.getAttachmentName(), args));
    }

}
