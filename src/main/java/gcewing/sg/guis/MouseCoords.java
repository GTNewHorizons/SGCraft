package gcewing.sg.guis;

import gcewing.sg.interfaces.IWidget;

public class MouseCoords {

    int x, y;

    public MouseCoords(IWidget widget, int x, int y) {
        while (widget != null) {
            x -= widget.left();
            y -= widget.top();
            widget = widget.parent();
        }
        this.x = x;
        this.y = y;
    }

}