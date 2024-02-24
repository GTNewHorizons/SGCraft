package gcewing.sg.interfaces;

public interface IWidgetContainer extends IWidget {

    IWidget getFocus();

    void setFocus(IWidget widget);
}
