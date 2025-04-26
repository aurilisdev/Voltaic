package voltaic.datagen.utils.client;

import voltaic.api.gas.Gas;
import voltaic.registers.VoltaicGases;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class BaseLangKeyProvider extends LanguageProvider {

	public final Locale locale;
	public final String modID;

	public BaseLangKeyProvider(PackOutput output, Locale locale, String modID) {
		super(output, modID, locale.toString());
		this.locale = locale;
		this.modID = modID;
	}

	public void addItem(DeferredHolder<Item, ? extends Item> item, String translation) {
		add(item.get(), translation);
	}

	public void addItem(Item item, String translation) {
		add(item, translation);
	}

	public void addBlock(DeferredHolder<Block, ? extends Block> block, String translation) {
		add(block.get(), translation);
	}

	public void addBlock(Block block, String translation) {
		add(block, translation);
	}

	public void addTooltip(String key, String translation) {
		add("tooltip." + modID + "." + key, translation);
	}

	public void addFluid(DeferredHolder<Fluid, ? extends Fluid> fluid, String translation){
		addFluid(fluid.get(), translation);
	}
	public void addFluid(Fluid fluid, String translation) {
		add("fluid." + modID + "." + BuiltInRegistries.FLUID.getKey(fluid).getPath(), translation);
	}

	public void addGas(Holder<Gas> gas, String translation) {
		addGas(gas.value(), translation);
	}

	public void addGas(Gas gas, String translation) {
		add("gas." + modID + "." + VoltaicGases.GAS_REGISTRY.getKey(gas).getPath(), translation);
	}

	public void addContainer(String key, String translation) {
		add("container." + key, translation);
	}

	public void addCommand(String key, String translation) {
		add("command." + modID + "." + key, translation);
	}

	public void addSubtitle(String key, String translation) {
		add("subtitles." + modID + "." + key, translation);
	}

	public void addSubtitle(DeferredHolder<SoundEvent, SoundEvent> sound, String translation) {
		addSubtitle(sound.getId().getPath(), translation);
	}

	public void addGuiLabel(String key, String translation) {
		add("gui." + modID + "." + key, translation);
	}

	@Override
	public void addDimension(ResourceKey<Level> dim, String translation) {
		addDimension(dim.location().getPath(), translation);
	}

	public void addDimension(String key, String translation) {
		add("dimension." + modID + "." + key, translation);
	}

	public void addKeyLabel(String key, String translation) {
		add("key." + modID + "." + key, translation);
	}

	public void addJei(String key, String translation) {
		add("jei." + key, translation);
	}

	public void addDamageSource(String key, String translation) {
		add("death.attack." + key, translation);
	}

	public void addChatMessage(String key, String translation) {
		add("chat." + modID + "." + key, translation);
	}

	public void addGuidebook(String key, String translation) {
		add("guidebook." + modID + "." + key, translation);
	}

	public void addAdvancement(String key, String translation) {
		add("advancement." + modID + "." + key, translation);
	}

	public void addCreativeTab(String key, String translation) {
		add("creativetab." + modID + "." + key, translation);
	}

	public static enum Locale {
		EN_US;

		@Override
		public String toString() {
			return super.toString().toLowerCase(java.util.Locale.ROOT);
		}
	}

}
