package voltaic.api.network.cable.type;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.SoundType;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag.INamedTag;
import voltaic.prefab.utilities.math.Color;

/**
 * Defines a wire
 *
 *
 * @author skip999
 */
public interface IWire {


    double getResistance();

    long getAmpacity();

    IWireClass getWireClass();

    IInsulationMaterial getInsulation();

    IWireMaterial getWireMaterial();

    IWireColor getWireColor();

    IWireColor getDefaultColor();

    @Nullable
    INamedTag<Item> getItemTag();

    boolean isDefaultColor();



    public static interface IInsulationMaterial {

        boolean insulated();

        boolean fireproof();

        /**
         * The maximum voltage this insulation can shield from
         *
         * @return
         */
        int shockVoltage();

        double wireRadius();

        AbstractBlock.Properties getProperties();

        SoundType getSoundType();

    }


    public static interface IWireClass {

        boolean conductsRedstone();

    }


    public static interface IWireMaterial {

        /**
         * return zero if this material has no resistance
         *
         * @return the resistance of this material
         */
        @Nonnegative
        double resistance();

        @Nonnegative
        long ampacity();

        /**
         * Returns the resistivity (inverse of conductance) of this material in units of ohm * meter
         * It is assumed this value is the value at 20 degrees C
         *
         * @return the material's resistivity
         */
        @Nonnegative
        double materialResistivity();

    }


    public static interface IWireColor {

        @Nonnull
        Color getColor();

        @Nonnull
        INamedTag<Item> getDyeTag();

    }


}
