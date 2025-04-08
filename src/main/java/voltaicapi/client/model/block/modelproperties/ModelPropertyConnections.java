package voltaicapi.client.model.block.modelproperties;

import java.util.function.Supplier;

import voltaicapi.common.block.connect.EnumConnectType;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class ModelPropertyConnections extends ModelProperty<Supplier<EnumConnectType[]>> {

    public static final ModelPropertyConnections INSTANCE = new ModelPropertyConnections();

}
