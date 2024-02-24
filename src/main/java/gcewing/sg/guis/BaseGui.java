// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base - Generic GUI Screen
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis;

import static gcewing.sg.utils.BaseUtils.packedColor;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import gcewing.sg.interfaces.IWidgetContainer;
import gcewing.sg.interfaces.Ref;
import gcewing.sg.utils.FieldRef;
import gcewing.sg.utils.MethodAction;
import gcewing.sg.utils.PropertyRef;
import gcewing.sg.interfaces.Action;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import gcewing.sg.BaseMod;
import gcewing.sg.SGCraft;
import gcewing.sg.interfaces.ISetMod;
import gcewing.sg.interfaces.IWidget;

// ------------------------------------------------------------------------------------------------

public class BaseGui {

    public final static int defaultTextColor = 0x404040;

    public static boolean isFocused(IWidget widget) {
        if (widget == null) return false;
        else if (widget instanceof Root) return true;
        else {
            IWidgetContainer parent = widget.parent();
            return (parent != null && parent.getFocus() == widget && isFocused(parent));
        }
    }

    public static void tellFocusChanged(IWidget widget, boolean state) {
        SGCraft.log.trace(String.format("BaseGui.tellFocusChanged: to %s for %s", state, name(widget)));
        if (widget == null) {
            return;
        }

        widget.focusChanged(state);
        if (widget instanceof IWidgetContainer) {
            tellFocusChanged(((IWidgetContainer) widget).getFocus(), state);
        }
    }

    public static String name(Object obj) {
        if (obj != null) return obj.getClass().getSimpleName();
        else return "null";
    }

    public static Ref ref(Object target, String name) {
        return new FieldRef(target, name);
    }

    public static Ref ref(Object target, String getterName, String setterName) {
        return new PropertyRef(target, getterName, setterName);
    }

    public static Action action(Object target, String name) {
        return new MethodAction(target, name);
    }
}
