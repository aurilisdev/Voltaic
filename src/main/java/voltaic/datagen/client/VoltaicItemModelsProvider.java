package voltaic.datagen.client;

import voltaic.Voltaic;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.datagen.utils.client.BaseItemModelsProvider;
import voltaic.registers.VoltaicItems;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VoltaicItemModelsProvider extends BaseItemModelsProvider {

    public VoltaicItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, existingFileHelper, Voltaic.ID);
    }

    @Override
    protected void registerModels() {

        layeredItem(VoltaicItems.GUIDEBOOK, Parent.GENERATED, itemLoc("guidebook"));
        layeredItem(VoltaicItems.ITEM_WRENCH, Parent.GENERATED, itemLoc("wrench"));
        //layeredItem(VoltaicAPIItems.ITEM_ANTIDOTE, Parent.GENERATED, itemLoc("antidote"));
        //layeredItem(VoltaicAPIItems.ITEM_IODINETABLET, Parent.GENERATED, itemLoc("iodinetablet"));

        for (SubtypeItemUpgrade upgrade : SubtypeItemUpgrade.values()) {
            layeredBuilder(name(VoltaicItems.ITEMS_UPGRADE.getValue(upgrade)), Parent.GENERATED, itemLoc("upgrade/" + upgrade.tag())).transforms().transform(TransformType.GUI).scale(0.8F).end();
        }

    }


}
