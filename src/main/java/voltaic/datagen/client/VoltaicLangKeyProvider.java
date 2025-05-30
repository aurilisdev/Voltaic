package voltaic.datagen.client;

import voltaic.Voltaic;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.datagen.utils.client.BaseLangKeyProvider;
import voltaic.registers.*;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.Level;

public class VoltaicLangKeyProvider extends BaseLangKeyProvider {

    public VoltaicLangKeyProvider(PackOutput output, Locale local) {
        super(output, local, Voltaic.ID);
    }

    @Override
    protected void addTranslations() {

        switch (locale) {
            case EN_US:
            default:

                addCreativeTab("main", "Voltaic");

                addItem(VoltaicItems.GUIDEBOOK, "VEC 1st Edition");
                addItem(VoltaicItems.ITEM_WRENCH, "Wrench");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.advancedcapacity), "Advanced Capacity Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.advancedspeed), "Advanced Speed Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basiccapacity), "Basic Capacity Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.basicspeed), "Basic Speed Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.experience), "Experience Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.fortune), "Fortune Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.improvedsolarcell), "Solar Cell Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.iteminput), "Auto-Injector Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemoutput), "Auto-Ejector Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.itemvoid), "Void Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.range), "Range Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.silktouch), "Silk Touch Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.stator), "Stator Upgrade");
                addItem(VoltaicItems.ITEMS_UPGRADE.getValue(SubtypeItemUpgrade.unbreaking), "Unbreaking Upgrade");
                //addItem(VoltaicItems.ITEM_ANTIDOTE, "Antidote");
                //addItem(VoltaicItems.ITEM_IODINETABLET, "Iodine Tablet");

                addGas(VoltaicGases.EMPTY, "Empty");

                addContainer("guidebook", "Guidebook");

                addTooltip("info.upgradecapacity", "Capacity: %s");
                addTooltip("info.upgradeenergytransfer", "Energy Transfer: %s");
                addTooltip("info.upgradevoltage", "Voltage: %s");
                addTooltip("info.upgradespeed", "Speed: %s");
                addTooltip("info.upgradeenergyusage", "Usage: %s");
                addTooltip("info.itemoutputupgrade", "Ejects items into adjacent inventories");
                addTooltip("info.iteminputupgrade", "Injects items from adjacent inventories");
                addTooltip("info.dirlist", "Current Directions:");
                addTooltip("info.nodirs", "Shift+Right-Click to add a direction");
                addTooltip("info.cleardirs", "Shift+Left-Click to clear directions");
                addTooltip("info.insmartmode", "Smart Mode");
                addTooltip("info.togglesmart", "Right-Click to toggle smart mode.");
                addTooltip("info.guidebookuse", "Shift+Right-Click to open wiki");
                addTooltip("info.guidebooktemp", "Book Coming Soon");
                addTooltip("info.xpstored", "Stored XP: %s");
                addTooltip("info.xpusage", "Shift+Right-Click to dispense");
                addTooltip("info.range", "Increases raduis by 1");
                addTooltip("info.broken", "NOT WORKING");
                addTooltip("transformer.energyloss", "Efficiency: %s");
                addTooltip("machine.voltage", "Voltage: %s");
                addTooltip("item.electric.info", "Stored: %s");
                addTooltip("item.electric.voltage", "Voltage: %s");

                addTooltip("currentgas", "Gas: %s");
                addTooltip("gasamount", "Amount: %1$s / %2$s");
                addTooltip("gastemperature", "Temp: %s");
                addTooltip("gaspressure", "Pressure: %s");
                addTooltip("maxpressure", "Max Pressure: %s");
                addTooltip("maxtemperature", "Max Temp: %s");

                addTooltip("tankmaxin", "In %1$s : %2$s");
                addTooltip("tankmaxout", "Out %1$s : %2$s");

                addTooltip("guidebookjeiuse", "Press 'U' for uses");
                addTooltip("guidebookjeirecipe", "Press 'R' for recipes");

                addTooltip("guidebookname", "AKA Guidebook");

                addTooltip("radiationshieldingamount", "Shielding Amount: %s");
                addTooltip("radiationshieldinglevel", "Shielding Level: %s");

                addTooltip("inventoryio", "Inventory I/O");
                addTooltip("inventoryio.presstoshow", "press to show");
                addTooltip("inventoryio.presstohide", "press to hide");
                addTooltip("scannerpattern", "Sonar Pattern");
                addTooltip("inventoryio.top", "Top");
                addTooltip("inventoryio.bottom", "Bottom");
                addTooltip("inventoryio.left", "Left");
                addTooltip("inventoryio.right", "Right");
                addTooltip("inventoryio.front", "Front");
                addTooltip("inventoryio.back", "Back");
                addTooltip("inventoryio.slotmap", "Slot Map");

                addTooltip("validupgrades", "Valid Upgrades:");
                addTooltip("upgrade.basiccapacity", "Basic Capacity");
                addTooltip("upgrade.basicspeed", "Basic Speed");
                addTooltip("upgrade.advancedcapacity", "Advanced Capacity");
                addTooltip("upgrade.advancedspeed", "Advanced Speed");
                addTooltip("upgrade.iteminput", "Auto-Injector");
                addTooltip("upgrade.itemoutput", "Auto-Ejector");
                addTooltip("upgrade.improvedsolarcell", "Improved Solar Cell");
                addTooltip("upgrade.stator", "Improved Stator");
                addTooltip("upgrade.range", "Range");
                addTooltip("upgrade.experience", "Experience");
                addTooltip("upgrade.itemvoid", "Item Void");
                addTooltip("upgrade.silktouch", "Silk Touch");
                addTooltip("upgrade.fortune", "Fortune");
                addTooltip("upgrade.unbreaking", "Unbreaking");


                addGuiLabel("machine.usage", "Usage: %s");
                addGuiLabel("machine.voltage", "Voltage: %s");
                addGuiLabel("machine.current", "Current: %s");
                addGuiLabel("machine.output", "Output: %s");
                addGuiLabel("machine.transfer", "Transfer: %s");
                addGuiLabel("machine.stored", "Stored: %s");
                addGuiLabel("machine.temperature", "Temperature: %s");
                addGuiLabel("machine.heat", "Heat: %s");
                addGuiLabel("machine.satisfaction", "Satisfaction: %s");

                addGuiLabel("displayunit.infinity.name", "Infinite");

                addGuiLabel("displayunit.ampere.name", "Ampere");
                addGuiLabel("displayunit.ampere.nameplural", "Amperes");
                addGuiLabel("displayunit.ampere.symbol", "A");
                addGuiLabel("displayunit.amphour.name", "Amp Hour");
                addGuiLabel("displayunit.amphour.nameplural", "Amp Hours");
                addGuiLabel("displayunit.amphour.symbol", "Ah");
                addGuiLabel("displayunit.voltage.name", "Volt");
                addGuiLabel("displayunit.voltage.nameplural", "Volts");
                addGuiLabel("displayunit.voltage.symbol", "V");
                addGuiLabel("displayunit.watt.name", "Watt");
                addGuiLabel("displayunit.watt.nameplural", "Watts");
                addGuiLabel("displayunit.watt.symbol", "W");
                addGuiLabel("displayunit.watthour.name", "Watt Hour");
                addGuiLabel("displayunit.watthour.nameplural", "Watt Hours");
                addGuiLabel("displayunit.watthour.symbol", "Wh");
                addGuiLabel("displayunit.resistance.name", "Ohm");
                addGuiLabel("displayunit.resistance.nameplural", "Ohms");
                addGuiLabel("displayunit.resistance.symbol", "" + '\u03A9');
                addGuiLabel("displayunit.conductance.name", "Siemen");
                addGuiLabel("displayunit.conductance.nameplural", "Siemens");
                addGuiLabel("displayunit.conductance.symbol", "S");
                addGuiLabel("displayunit.joules.name", "Joule");
                addGuiLabel("displayunit.joules.nameplural", "Joules");
                addGuiLabel("displayunit.joules.symbol", "J");
                addGuiLabel("displayunit.buckets.name", "Bucket");
                addGuiLabel("displayunit.buckets.nameplural", "Buckets");
                addGuiLabel("displayunit.buckets.symbol", "B");
                addGuiLabel("displayunit.tempkelvin.name", "Kelvin");
                addGuiLabel("displayunit.tempkelvin.nameplural", "Kelvin");
                addGuiLabel("displayunit.tempkelvin.symbol", "K");
                addGuiLabel("displayunit.tempcelcius.name", "Celcius");
                addGuiLabel("displayunit.tempcelcius.nameplural", "Celcius");
                addGuiLabel("displayunit.tempcelcius.symbol", "C");
                addGuiLabel("displayunit.tempfahrenheit.name", "Fahrenheit");
                addGuiLabel("displayunit.tempfahrenheit.nameplural", "Fahrenheit");
                addGuiLabel("displayunit.tempfahrenheit.symbol", "F");
                addGuiLabel("displayunit.timeseconds.name", "Second");
                addGuiLabel("displayunit.timeseconds.nameplural", "Seconds");
                addGuiLabel("displayunit.timeseconds.symbol", "s");
                addGuiLabel("displayunit.pressureatm.name", "Atmosphere");
                addGuiLabel("displayunit.pressureatm.nameplural", "Atmospheres");
                addGuiLabel("displayunit.pressureatm.symbol", "ATM");
                addGuiLabel("displayunit.percentage.name", "Percent");
                addGuiLabel("displayunit.percentage.nameplural", "Percent");
                addGuiLabel("displayunit.percentage.symbol", "%");
                addGuiLabel("displayunit.radsymbol", "Rad");
                addGuiLabel("displayunit.radname", "Rad");
                addGuiLabel("displayunit.radnameplural", "Rads");

                addGuiLabel("displayunit.timeticks.name", "Tick");
                addGuiLabel("displayunit.timeticks.nameplural", "Ticks");
                addGuiLabel("displayunit.timeticks.symbol", "t");

                addGuiLabel("displayunit.forgeenergyunit.name", "Forge Energy Unit");
                addGuiLabel("displayunit.forgeenergyunit.nameplural", "Forge Energy Units");
                addGuiLabel("displayunit.forgeenergyunit.symbol", "FE");

                addGuiLabel("measurementunit.pico.name", "Pico");
                addGuiLabel("measurementunit.pico.symbol", "p");
                addGuiLabel("measurementunit.nano.name", "Nano");
                addGuiLabel("measurementunit.nano.symbol", "n");
                addGuiLabel("measurementunit.micro.name", "Micro");
                addGuiLabel("measurementunit.micro.symbol", "" + '\u00B5');
                addGuiLabel("measurementunit.milli.name", "Milli");
                addGuiLabel("measurementunit.milli.symbol", "m");
                addGuiLabel("measurementunit.none.name", "");
                addGuiLabel("measurementunit.none.symbol", "");
                addGuiLabel("measurementunit.kilo.name", "Kilo");
                addGuiLabel("measurementunit.kilo.symbol", "k");
                addGuiLabel("measurementunit.mega.name", "Mega");
                addGuiLabel("measurementunit.mega.symbol", "M");
                addGuiLabel("measurementunit.giga.name", "Giga");
                addGuiLabel("measurementunit.giga.symbol", "G");

                addChatMessage("guidebookclick", "Click Here");

                add("effect.voltaic.radiation", "Radiation");
                add("effect.voltaic.radiationresistance", "Radiation Resistance");

                addSubtitle(VoltaicSounds.SOUND_BATTERY_SWAP, "Battery is swapped");
                addSubtitle(VoltaicSounds.SOUND_PRESSURERELEASE, "Gas hisses");

                addDimension(Level.OVERWORLD, "The Overworld");
                addDimension(Level.NETHER, "The Nether");
                addDimension(Level.END, "The End");

                addAdvancement("multimeter.title", "Multimetering");
                addAdvancement("multimeter.desc", "Make a Multimeter!");

                addGuidebook("title", "Voltaic Electric Code 1st Edition");
                addGuidebook("titlequote", "\"There is nothing more permanent than a temporary solution.\"");

                addGuidebook("availablemodules", "Available Modules");
                addGuidebook("chapters", "Chapters");

                addGuidebook("searchparameters", "Parameters");
                addGuidebook("selectall", "All");
                addGuidebook("selectnone", "None");
                addGuidebook("casesensitive", "Case-Sensitive");

        }
    }


}
