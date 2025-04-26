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
import net.minecraft.ChatFormatting;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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
	protected List<? extends FormattedCharSequence> getInfo(List<? extends FormattedCharSequence> list) {
		if (infoHandler == EMPTY) {
			return getElectricInformation();
		}
		return super.getInfo(list);
	}

	private List<? extends FormattedCharSequence> getElectricInformation() {
		ArrayList<FormattedCharSequence> list = new ArrayList<>();
		if (gui instanceof GenericScreen<?> menu) {
			if (((GenericContainerBlockEntity<?>) menu.getMenu()).getUnsafeHost() instanceof GenericTile tile) {
				if (tile.getComponent(IComponentType.Electrodynamic) instanceof ComponentElectrodynamic electro) {
					if (tile instanceof IElectricGenerator generator) {
						TransferPack transfer = generator.getProduced();
						list.add(VoltaicTextUtils.gui("machine.current", ChatFormatter.getChatDisplayShort(transfer.getAmps(), DisplayUnits.AMPERE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
						list.add(VoltaicTextUtils.gui("machine.output", ChatFormatter.getChatDisplayShort(transfer.getWatts(), DisplayUnits.WATT).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
						list.add(VoltaicTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(transfer.getVoltage(), DisplayUnits.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
					} else {
						double satisfaction = 0;
						if (wattage == null) {
							double perTick = tile.hasComponent(IComponentType.Processor) ? tile.<ComponentProcessor>getComponent(IComponentType.Processor).getTotalUsage() * tile.<ComponentProcessor>getComponent(IComponentType.Processor).operatingSpeed.getValue() : 0.0;
							list.add(VoltaicTextUtils.gui("machine.usage", ChatFormatter.getChatDisplayShort(perTick * 20, DisplayUnits.WATT).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
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

							list.add(VoltaicTextUtils.gui("machine.usage", ChatFormatter.getChatDisplayShort(watts, DisplayUnits.WATT).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
						}
						list.add(VoltaicTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(electro.getVoltage(), DisplayUnits.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
						list.add(VoltaicTextUtils.gui("machine.satisfaction", ChatFormatter.getChatDisplayShort(satisfaction * 100.0, DisplayUnits.PERCENTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
					}
				}
			}
		}
		return list;
	}
}