package voltaic.api.electricity.generator;

import voltaic.prefab.utilities.object.TransferPack;

public interface IElectricGenerator {

    void setMultiplier(double val);

    double getMultiplier();

    TransferPack getProduced();
}
