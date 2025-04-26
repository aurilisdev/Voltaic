package voltaic.datagen.utils.client.model;

import com.google.gson.JsonObject;

import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.client.model.generators.ModelFile;
import voltaic.Voltaic;
import voltaic.client.model.block.bakerytypes.CableModelLoader;
import voltaic.common.block.connect.EnumConnectType;

public class GeneratedBlockStateCableModel implements IGeneratedBlockState {

	private final ModelFile none;
	private final ModelFile wire;
	private final ModelFile inventory;

	public GeneratedBlockStateCableModel(ModelFile none, ModelFile wire, ModelFile inventory) {

		this.none = none;
		this.wire = wire;
		this.inventory = inventory;

	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.addProperty("loader", Voltaic.ID + ":" + CableModelLoader.ID);

		JsonObject noneElement = new JsonObject();
		noneElement.addProperty("parent", none.getLocation().toString());
		
		
		
		json.add(EnumConnectType.NONE.toString(), noneElement);

		JsonObject wireElement = new JsonObject();
		wireElement.addProperty("parent", wire.getLocation().toString());
		json.add(EnumConnectType.WIRE.toString(), wireElement);

		JsonObject inventoryElement = new JsonObject();
		inventoryElement.addProperty("parent", inventory.getLocation().toString());
		json.add(EnumConnectType.INVENTORY.toString(), inventoryElement);

		return json;
	}

}
