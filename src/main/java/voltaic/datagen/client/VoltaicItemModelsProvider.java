package voltaic.datagen.client;

import voltaic.Voltaic;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.datagen.utils.client.BaseItemModelsProvider;
import voltaic.registers.VoltaicItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class VoltaicItemModelsProvider extends BaseItemModelsProvider {

    public VoltaicItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, existingFileHelper, Voltaic.ID);
    }

    @Override
    protected void registerModels() {

        layeredItem(VoltaicItems.GUIDEBOOK, Parent.GENERATED, itemLoc("guidebook"));
        layeredItem(VoltaicItems.ITEM_WRENCH, Parent.GENERATED, itemLoc("wrench"));
        //layeredItem(VoltaicAPIItems.ITEM_ANTIDOTE, Parent.GENERATED, itemLoc("antidote"));
        //layeredItem(VoltaicAPIItems.ITEM_IODINETABLET, Parent.GENERATED, itemLoc("iodinetablet"));

        for (SubtypeItemUpgrade upgrade : SubtypeItemUpgrade.values()) {
            //layeredBuilder(name(VoltaicAPIItems.ITEMS_UPGRADE.getValue(upgrade)), Parent.GENERATED, itemLoc("upgrade/" + upgrade.tag())).transforms().transform(ItemDisplayContext.GUI).scale(0.8F).end();
        }

    }


}
