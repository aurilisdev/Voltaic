package voltaic.prefab.screen.component.types.guitab;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnits;
import voltaic.api.electricity.generator.IElectricGenerator;
import voltaic.api.screen.component.TextPropertySupplier;
import voltaic.prefab.inventory.container.types.GenericContainerBlockEntity;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentProcessor;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.object.TransferPack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenComponentElectricInfo extends ScreenComponentGuiTab {

	private Function<ComponentElectrodynamic, Double> wattage = null;

	public ScreenComponentElectricInfo(TextPropertySupplier infoHandler, int x, int y) {
		super(GuiInfoTabTextures.REGULAR, IconType.ENERGY_GREEN, infoHandler, x, y);
	}

	public ScreenComponentElectricInfo(int x, int y) {
		this(AbstractScreenComponentInfo.EMPTY, x, y);
	}

	public ScreenComponentElectricInfo wattage(double wattage) {
		return wattage(e -> wattage);
	}

	public ScreenComponentElectricInfo wattage(Function<ComponentElectrodynamic, Double> wattage) {
		this.wattage = wattage;
		return this;
	}

	@Override
	protected List<? extends IReorderingProcessor> getInfo(List<? extends IReorderingProcessor> list) {
		if (infoHandler == EMPTY) {
			return getElectricInformation();
		}
		return super.getInfo(list);
	}

	private List<? extends IReorderingProcessor> getElectricInformation() {
		ArrayList<IReorderingProcessor> list = new ArrayList<>();
		if (gui instanceof GenericScreen<?>) {
			GenericScreen<?> menu = (GenericScreen<?>) gui;
			if (((GenericContainerBlockEntity<?>) menu.getMenu()).getUnsafeHost() instanceof GenericTile) {
				GenericTile tile = (GenericTile) ((GenericContainerBlockEntity<?>) menu.getMenu()).getUnsafeHost();
				if (tile.hasComponent(IComponentType.Electrodynamic)) {
					ComponentElectrodynamic electro = tile.getComponent(IComponentType.Electrodynamic);
					if (tile instanceof IElectricGenerator) {
						TransferPack transfer = ((IElectricGenerator) tile).getProduced();
						list.add(VoltaicTextUtils.gui("machine.current", ChatFormatter.getChatDisplayShort(transfer.getAmps(), DisplayUnits.AMPERE).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
						list.add(VoltaicTextUtils.gui("machine.output", ChatFormatter.getChatDisplayShort(transfer.getWatts(), DisplayUnits.WATT).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
						list.add(VoltaicTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(transfer.getVoltage(), DisplayUnits.VOLTAGE).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
					} else {
						double satisfaction = 0;
						if (wattage == null) {
							double perTick = tile.hasComponent(IComponentType.Processor) ? tile.<ComponentProcessor>getComponent(IComponentType.Processor).getTotalUsage() * tile.<ComponentProcessor>getComponent(IComponentType.Processor).operatingSpeed.getValue() : 0.0;
							list.add(VoltaicTextUtils.gui("machine.usage", ChatFormatter.getChatDisplayShort(perTick * 20, DisplayUnits.WATT).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
							if(perTick == 0) {
								satisfaction = 1;
							} else if(electro.getJoulesStored() > 0) {
								satisfaction = electro.getJoulesStored() >= perTick ? 1 : electro.getJoulesStored() / perTick;
							}
						} else {
							double watts = wattage.apply(electro);
							double perTick = watts / 20.0;

							if(perTick == 0) {
								satisfaction = 1;
							} else if(electro.getJoulesStored() > 0) {
								satisfaction = electro.getJoulesStored() >= perTick ? 1 : electro.getJoulesStored() / perTick;
							}

							list.add(VoltaicTextUtils.gui("machine.usage", ChatFormatter.getChatDisplayShort(watts, DisplayUnits.WATT).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
						}
						list.add(VoltaicTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(electro.getVoltage(), DisplayUnits.VOLTAGE).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
						list.add(VoltaicTextUtils.gui("machine.satisfaction", ChatFormatter.getChatDisplayShort(satisfaction * 100.0, DisplayUnits.PERCENTAGE).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY).getVisualOrderText());
					}
				}
			}
		}
		return list;
	}
}