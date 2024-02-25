package gcewing.sg.guis;

import java.util.ArrayList;
import java.util.List;

import gcewing.sg.guis.screens.Screen;
import gcewing.sg.interfaces.IWidget;

public class Root extends Group {

    public Screen screen;
    public List<IWidget> popupStack;

    public Root(Screen screen) {
        this.screen = screen;
        popupStack = new ArrayList<IWidget>();
    }

    @Override
    public int width() {
        return screen.getWidth();
    }

    @Override
    public int height() {
        return screen.getHeight();
    }

    @Override
    public IWidget dispatchMousePress(int x, int y, int button) {
        IWidget w = topPopup();
        if (w == null) w = super.dispatchMousePress(x, y, button);
        return w;
    }

    @Override
    public void remove(IWidget widget) {
        super.remove(widget);
        popupStack.remove(widget);
        focusTopPopup();
    }

    public IWidget topPopup() {
        int n = popupStack.size();
        if (n > 0) return popupStack.get(n - 1);
        else return null;
    }

    void focusTopPopup() {
        IWidget w = topPopup();
        if (w != null) screen.focusOn(w);
    }

}
