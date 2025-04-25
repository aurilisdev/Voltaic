package voltaic.datagen.utils.client;

import java.util.Locale;

import javax.annotation.Nullable;

import voltaic.client.VoltaicClientRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public abstract class BaseItemModelsProvider extends ItemModelProvider {

	public final String modID;

	public BaseItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper, String modID) {
		super(output, modID, existingFileHelper);
		this.modID = modID;
	}

	public void layeredItem(RegistryObject<? extends Item> item, Parent parent, ResourceLocation... textures) {
		layeredItem(name(item), parent, textures);
	}

	public void layeredItem(Item item, Parent parent, ResourceLocation... textures) {
		layeredItem(name(item), parent, textures);
	}

	public void layeredItem(String name, Parent parent, ResourceLocation... textures) {
		layeredBuilder(name, parent, textures);
	}

	public void toggleableItem(RegistryObject<? extends Item> item, String toggle, Parent parentOff, Parent parentOn, ResourceLocation[] offText, ResourceLocation[] onText) {
		toggleableItem(name(item), toggle, parentOff, parentOn, offText, onText);
	}

	public void toggleableItem(String name, String toggle, Parent parentOff, Parent parentOn, ResourceLocation[] offText, ResourceLocation[] onText) {
		ItemModelBuilder off = layeredBuilder(name, parentOff, offText);
		ItemModelBuilder on = layeredBuilder(name + toggle, parentOn, onText);
		off.override().predicate(VoltaicClientRegister.ON, 1.0F).model(on).end();
	}

	public ItemModelBuilder layeredBuilder(String name, Parent parent, ResourceLocation... textures) {
		if (textures == null || textures.length == 0) {
			throw new UnsupportedOperationException("You need to provide at least one texture");
		}
		ItemModelBuilder builder = withExistingParent(name, parent.loc());
		int counter = 0;
		for (ResourceLocation location : textures) {
			builder.texture("layer" + counter, location);
			counter++;
		}
		return builder;
	}

	public DynamicFluidContainerModelBuilder<ItemModelBuilder> getBucketModel(RegistryObject<? extends Item> item, Parent parent) {
		return getBucketModel(name(item), parent);
	}

	public DynamicFluidContainerModelBuilder<ItemModelBuilder> getBucketModel(String name, Parent parent) {
		return withExistingParent(name, parent.loc).customLoader(DynamicFluidContainerModelBuilder::begin);
	}

	public ItemModelBuilder simpleBlockItem(Block block, ModelFile model) {
		return getBuilder(key(block).getPath()).parent(model);
	}

	public ResourceLocation key(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	public ResourceLocation itemLoc(String texture) {
		return modLoc("item/" + texture);
	}

	public ResourceLocation blockLoc(String texture) {
		return modLoc("block/" + texture);
	}

	public String name(RegistryObject<? extends Item> item) {
		return name(item.get());
	}

	public String name(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).getPath();
	}

	public ExistingModelFile existingBlock(RegistryObject<? extends Block> block) {
		return existingBlock(block.getId());
	}

	public ExistingModelFile existingBlock(Block block) {
		return existingBlock(BuiltInRegistries.BLOCK.getKey(block));
	}

	public ExistingModelFile existingBlock(ResourceLocation loc) {
		return getExistingFile(loc);
	}

	public enum Parent {

		GENERATED(),
		HANDHELD(),
		FORGE_DEFAULT("neoforge", "item/default");

		@Nullable
		private final ResourceLocation loc;

		Parent() {
			loc = null;
		}

		Parent(String id, String loc) {
			this.loc = new ResourceLocation(id, loc);
		}

		public ResourceLocation loc() {
			return loc == null ? new ResourceLocation(toString().toLowerCase(Locale.ROOT)) : loc;
		}
	}

}
