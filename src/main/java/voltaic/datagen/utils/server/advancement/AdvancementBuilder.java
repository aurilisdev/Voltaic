package voltaic.datagen.utils.server.advancement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.extensions.IAdvancementBuilderExtension;
import voltaic.Voltaic;

public class AdvancementBuilder implements IAdvancementBuilderExtension {
    private AdvancementRewards rewards = AdvancementRewards.EMPTY;
    private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
    private final Map<String, Criterion<?>> criteria = Maps.newLinkedHashMap();

    public final ResourceLocation id;
    private @Nullable ResourceLocation parentId;
    private @Nullable AdvancementHolder parent;
    private @Nullable DisplayInfo display;
    private @Nullable AdvancementRequirements requirements;
    private @Nullable String comment;
    private @Nullable String author;
    private @Nullable AdvancementHolder holder;
    private @Nullable List<ICondition> conditions;

    private AdvancementBuilder(ResourceLocation id) {
	this.id = id;
    }

    public static AdvancementBuilder create(ResourceLocation id) {
	return new AdvancementBuilder(id);
    }

    public AdvancementBuilder parent(AdvancementHolder parent) {
	this.parent = parent;
	return this;
    }

    public AdvancementBuilder parent(ResourceLocation parentId) {
	this.parentId = parentId;
	return this;
    }

    public AdvancementBuilder display(Item item, Component title, Component description,
	    AdvancementBackgrounds background, AdvancementType frame, boolean showToast, boolean announceToChat,
	    boolean hidden) {
	return this.display(new DisplayInfo(new ItemStack(item), title, description,
		Optional.ofNullable(background.loc), frame, showToast, announceToChat, hidden));
    }

    public AdvancementBuilder display(ItemStack stack, Component title, Component description,
	    AdvancementBackgrounds background, AdvancementType frame, boolean showToast, boolean announceToChat,
	    boolean hidden) {
	return this.display(new DisplayInfo(stack, title, description, Optional.of(background.loc), frame, showToast,
		announceToChat, hidden));
    }

    public AdvancementBuilder display(ItemStack stack, Component title, Component description,
	    @Nullable ResourceLocation background, AdvancementType frame, boolean showToast, boolean announceToChat,
	    boolean hidden) {
	return this.display(new DisplayInfo(stack, title, description, Optional.of(background), frame, showToast,
		announceToChat, hidden));
    }

    public AdvancementBuilder display(ItemLike item, Component title, Component description,
	    @Nullable ResourceLocation background, AdvancementType frame, boolean showToast, boolean announceToChat,
	    boolean hidden) {
	return this.display(new DisplayInfo(new ItemStack(item.asItem()), title, description, Optional.of(background),
		frame, showToast, announceToChat, hidden));
    }

    public AdvancementBuilder display(DisplayInfo display) {
	this.display = display;
	return this;
    }

    public AdvancementBuilder rewards(AdvancementRewards.Builder rewardsBuilder) {
	return this.rewards(rewardsBuilder.build());
    }

    public AdvancementBuilder rewards(AdvancementRewards rewards) {
	this.rewards = rewards;
	return this;
    }

    public AdvancementBuilder addCriterion(String key, Criterion<?> criterion) {
	if (criteria.containsKey(key))
	    throw new IllegalArgumentException("Duplicate criterion " + key);
	criteria.put(key, criterion);
	return this;
    }

    public AdvancementBuilder requirements(AdvancementRequirements.Strategy strategy) {
	requirementsStrategy = strategy;
	return this;
    }

    public AdvancementBuilder requirements(AdvancementRequirements requirements) {
	this.requirements = requirements;
	return this;
    }

    public AdvancementBuilder condition(ICondition condition) {
	if (conditions == null) {
	    conditions = new ArrayList<>();
	}
	Objects.requireNonNull(conditions).add(condition);
	return this;
    }

    public AdvancementBuilder comment(String comment) {
	this.comment = comment;
	return this;
    }

    public AdvancementBuilder author(String author) {
	this.author = author;
	return this;
    }

    /**
     * Tries to resolve the parent of this advancement, if possible. Returns
     * {@code true} on success.
     */
    public boolean canBuild(Function<ResourceLocation, AdvancementHolder> parentLookup) {
	if (parentId == null)
	    return true;
	if (parent == null) {
	    parent = parentLookup.apply(parentId);
	}

	return parent != null;
    }

    public AdvancementHolder build() {
	if (!canBuild(resourceLocation -> null))
	    throw new IllegalStateException("Tried to build incomplete advancement!");
	AdvancementRequirements requirements = this.requirements;
	if (requirements == null) {
	    requirements = requirementsStrategy.create(criteria.keySet());
	    this.requirements = requirements;
	}

	return holder = new AdvancementHolder(id,
		new Advancement(Optional.ofNullable(parent == null ? parentId : parent.id()),
			Optional.ofNullable(display), rewards, criteria, requirements, false));
    }

    public JsonObject serializeToJson(HolderLookup.Provider registries) {
	AdvancementHolder holder = this.holder;
	if (holder == null) {
	    holder = build();
	}
	RegistryOps<JsonElement> registryops = registries.createSerializationContext(JsonOps.INSTANCE);

	JsonElement jsonElement = Advancement.CODEC.encodeStart(registryops, holder.value()).getOrThrow();

	if (!jsonElement.isJsonObject())
	    throw new UnsupportedOperationException("Advancement " + holder.id().toString() + " is not a Json Object!");

	JsonObject jsonObject = jsonElement.getAsJsonObject();

	if (author != null) {
	    jsonObject.addProperty("__author", author);
	}

	if (comment != null) {
	    jsonObject.addProperty("__comment", comment);
	}

	return jsonObject;
    }

    public static enum AdvancementBackgrounds {

	NONE(null),
	// Vanilla
	ADVENTURE(Voltaic.vanillarl("textures/gui/advancements/backgrounds/adventure.png")), //
	END(Voltaic.vanillarl("textures/gui/advancements/backgrounds/end.png")), //
	HUSBANDRY(Voltaic.vanillarl("textures/gui/advancements/backgrounds/husbandry.png")), //
	NETHER(Voltaic.vanillarl("textures/gui/advancements/backgrounds/nether.png")), //
	STONE(Voltaic.vanillarl("textures/gui/advancements/backgrounds/stone.png")); //

	@Nullable
	public final ResourceLocation loc;

	private AdvancementBackgrounds(@Nullable ResourceLocation loc) {
	    this.loc = loc;
	}

    }

}
