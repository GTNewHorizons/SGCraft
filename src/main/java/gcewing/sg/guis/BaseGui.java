// ------------------------------------------------------------------------------------------------
//
// Greg's Mod Base - Generic GUI Screen
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.guis;

import gcewing.sg.SGCraft;
import gcewing.sg.interfaces.IWidget;
import gcewing.sg.interfaces.IWidgetContainer;

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
}
