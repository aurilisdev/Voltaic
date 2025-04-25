package voltaic.common.condition;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import voltaic.Voltaic;
import voltaic.common.settings.VoltaicConstants;

public class ConfigCondition implements ICondition {

	// make this work for multiple config values
	private static final ResourceLocation NAME = new ResourceLocation(Voltaic.ID, "config");

	public ConfigCondition() {
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test(IContext context) {
		return VoltaicConstants.DISPENSE_GUIDEBOOK;
	}

	public static class Serializer implements IConditionSerializer<ConfigCondition> {

		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ConfigCondition value) {
			// for data gen
		}

		@Override
		public ConfigCondition read(JsonObject json) {
			// specify config fields
			return new ConfigCondition();
		}

		@Override
		public ResourceLocation getID() {
			return ConfigCondition.NAME;
		}
	}
}