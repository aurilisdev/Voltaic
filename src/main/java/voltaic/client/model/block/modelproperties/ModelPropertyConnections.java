package voltaic.client.model.block.modelproperties;

import java.util.function.Supplier;

import net.neoforged.neoforge.client.model.data.ModelProperty;
import voltaic.common.block.connect.EnumConnectType;

public class ModelPropertyConnections extends ModelProperty<Supplier<EnumConnectType[]>> {

    public static final ModelPropertyConnections INSTANCE = new ModelPropertyConnections();

}
