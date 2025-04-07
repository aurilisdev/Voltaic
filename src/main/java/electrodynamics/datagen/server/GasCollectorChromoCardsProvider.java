package electrodynamics.datagen.server;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import electrodynamics.api.References;
import electrodynamics.api.gas.Gas;
import electrodynamics.common.item.subtype.SubtypeChromotographyCard;
import electrodynamics.common.reloadlistener.GasCollectorChromoCardsRegister;
import electrodynamics.registers.ElectrodynamicsGases;
import electrodynamics.registers.ElectrodynamicsItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

public class GasCollectorChromoCardsProvider implements DataProvider {

    public static final String LOC = "data/" + References.ID + "/" + GasCollectorChromoCardsRegister.FOLDER + "/";

    private final PackOutput output;

    private final Map<String, JsonObject> jsons = new HashMap<>();

    public GasCollectorChromoCardsProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        addGases();

        Path parent = output.getOutputFolder().resolve(LOC);

        List<CompletableFuture<?>> completed = new ArrayList<>();

        for (Map.Entry<String, JsonObject> json : jsons.entrySet()) {
            completed.add(DataProvider.saveStable(cache, json.getValue(), parent.resolve(json.getKey() + ".json")));
        }

        return CompletableFuture.allOf(completed.toArray(size -> new CompletableFuture[size]));
    }

    public void addGases() {
        jsons.put("nitrogen", toJson(ElectrodynamicsItems.ITEMS_CHROMOTOGRAPHYCARD.getValue(SubtypeChromotographyCard.nitrogen), ElectrodynamicsGases.NITROGEN.value(), 78, Gas.ROOM_TEMPERATURE, Gas.PRESSURE_AT_SEA_LEVEL, null));
        jsons.put("oxygen", toJson(ElectrodynamicsItems.ITEMS_CHROMOTOGRAPHYCARD.getValue(SubtypeChromotographyCard.oxygen), ElectrodynamicsGases.OXYGEN.value(), 21, Gas.ROOM_TEMPERATURE, Gas.PRESSURE_AT_SEA_LEVEL, null));
        jsons.put("argon", toJson(ElectrodynamicsItems.ITEMS_CHROMOTOGRAPHYCARD.getValue(SubtypeChromotographyCard.argon), ElectrodynamicsGases.ARGON.value(), 1, Gas.ROOM_TEMPERATURE, Gas.PRESSURE_AT_SEA_LEVEL, null));
        jsons.put("carbondioxide", toJson(ElectrodynamicsItems.ITEMS_CHROMOTOGRAPHYCARD.getValue(SubtypeChromotographyCard.carbondioxide), ElectrodynamicsGases.CARBON_DIOXIDE.value(), 1, Gas.ROOM_TEMPERATURE, Gas.PRESSURE_AT_SEA_LEVEL, null));
        jsons.put("sulfurdioxide", toJson(ElectrodynamicsItems.ITEMS_CHROMOTOGRAPHYCARD.getValue(SubtypeChromotographyCard.sulfurdioxide), ElectrodynamicsGases.SULFUR_DIOXIDE.value(), 1, 373, Gas.PRESSURE_AT_SEA_LEVEL, BiomeTags.IS_NETHER));
    }

    @SuppressWarnings("unused")
	private JsonObject toJson(TagKey<Item> tag, Gas gas, int amount, int temperature, int pressure, @Nullable TagKey<Biome> biomeTag) {
        return toJson("#" + tag.location().toString(), gas, amount, temperature, pressure, biomeTag);
    }

    private JsonObject toJson(Item item, Gas gas, int amount, int temperature, int pressure, @Nullable TagKey<Biome> biomeTag) {
        return toJson(BuiltInRegistries.ITEM.getKey(item).toString(), gas, amount, temperature, pressure, biomeTag);
    }

    private JsonObject toJson(String item, Gas gas, int amount, int temperature, int pressure, @Nullable TagKey<Biome> biomeTag) {
        JsonObject json = new JsonObject();
        json.addProperty(GasCollectorChromoCardsRegister.ITEM_KEY, item);
        json.addProperty(GasCollectorChromoCardsRegister.GAS_KEY, ElectrodynamicsGases.GAS_REGISTRY.getKey(gas).toString());
        json.addProperty(GasCollectorChromoCardsRegister.AMOUNT_KEY, amount);
        json.addProperty(GasCollectorChromoCardsRegister.TEMPERATURE_KEY, temperature);
        json.addProperty(GasCollectorChromoCardsRegister.PRESSURE_KEY, pressure);
        if(biomeTag != null) {
            json.addProperty(GasCollectorChromoCardsRegister.BIOME_KEY, "#" + biomeTag.location().toString());
        }
        return json;
    }

    @Override
    public String getName() {
        return "Gas Collector Chromotography Cards Provider";
    }
}
