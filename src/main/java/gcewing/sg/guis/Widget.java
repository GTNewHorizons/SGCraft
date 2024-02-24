package gcewing.sg.guis;

import gcewing.sg.SGCraft;
import gcewing.sg.guis.screens.Screen;
import gcewing.sg.interfaces.IWidget;
import gcewing.sg.interfaces.IWidgetContainer;

public class Widget implements IWidget {

    public IWidgetContainer parent;

    public int left, top, width, height;

    public Widget() {}

    public IWidgetContainer parent() {
        return parent;
    }

    public void setParent(IWidgetContainer widget) {
        parent = widget;
    }

    public int left() {
        return left;
    }

    public int top() {
        return top;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void setLeft(int x) {
        left = x;
    }

    public void setTop(int y) {
        top = y;
    }

    public void draw(Screen scr, int mouseX, int mouseY) {}

    public void mousePressed(MouseCoords m, int button) {}

    public void mouseMoved(MouseCoords m) {}

    public void mouseReleased(MouseCoords m, int button) {}

    public boolean keyPressed(char c, int key) {
        return false;
    }

    public void focusChanged(boolean state) {}

    public void close() {}

    public void layout() {}

    public IWidget dispatchMousePress(int x, int y, int button) {
        SGCraft.log.trace(
                String.format("BaseGui.Widget.dispatchMousePress: (%s, %s) in %s", x, y, getClass().getSimpleName()));
        return this;
    }

    public boolean dispatchKeyPress(char c, int key) {
        return this.keyPressed(c, key);
    }

}
