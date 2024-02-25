// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate energy source interface
//
// ------------------------------------------------------------------------------------------------

package gcewing.sg.interfaces;

public interface ISGEnergySource {

    double availableEnergy();

    double drawEnergy(double amount);

}
