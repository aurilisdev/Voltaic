package voltaic.datagen.utils.server.advancement;

import static voltaic.datagen.utils.server.advancement.AdvancementBuilder.create;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseAdvancementProvider implements DataProvider {

    public final String modID;

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    private final List<AdvancementBuilder> builders = new ArrayList<>();

    public BaseAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modID) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        this.registries = registries;
        this.modID = modID;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose(provider -> {
            generate(provider);
            //

            List<CompletableFuture<?>> list = new ArrayList<>();

            Set<ResourceLocation> advancementIds = new HashSet<>();

            for (AdvancementBuilder builder : builders) {

                AdvancementHolder holder = builder.build();

                if (!advancementIds.add(holder.id())) {
                    throw new IllegalStateException("Duplicate advancement " + holder.id());
                }

                Path path = this.pathProvider.json(holder.id());

                list.add(DataProvider.saveStable(output, builder.serializeToJson(provider), path));

            }

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));

        });
    }

    @Override
    public String getName() {
        return modID + " Advancement Provider";
    }

    public AdvancementBuilder advancement(String name) {
        AdvancementBuilder builder = create(ResourceLocation.fromNamespaceAndPath(modID, name));
        builders.add(builder);
        return builder;
    }

    public abstract void generate(HolderLookup.Provider provider);

}
