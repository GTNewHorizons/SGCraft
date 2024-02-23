// ------------------------------------------------------------------------------------------------
//
// SG Craft - Iris states
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.tileentities;

public enum IrisState {

    Open,
    Closing,
    Closed,
    Opening;

    static IrisState[] VALUES = values();

    public static IrisState valueOf(int i) {
        try {
            return VALUES[i];
        } catch (IndexOutOfBoundsException e) {
            return Open;
        }
    }

}
