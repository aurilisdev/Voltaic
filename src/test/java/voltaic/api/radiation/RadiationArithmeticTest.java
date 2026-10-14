package voltaic.api.radiation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import voltaic.prefab.utilities.RadiationUtils;
import voltaic.prefab.utilities.RadiationUtils.ItemEmission;

class RadiationArithmeticTest {
    @Test
    void quantityScalesRangeUntilTheCap() {
        assertEquals(5, RadiationUtils.getItemRadiationRange(1000, 256));
        assertEquals(44, RadiationUtils.getItemRadiationRange(64000, 256));
        assertEquals(176, RadiationUtils.getItemRadiationRange(1000000, 256));
        assertEquals(256, RadiationUtils.getItemRadiationRange(1000000000, 256));
        assertEquals(0, RadiationUtils.getItemRadiationRange(Double.NaN, 256));
        assertEquals(0, RadiationUtils.getItemRadiationRange(1000, 0));
    }

    @Test
    void extremeTotalsAndZeroContainmentStayFinite() {
        assertEquals(Double.MAX_VALUE, RadiationUtils.saturatedMultiply(Double.MAX_VALUE, Long.MAX_VALUE));
        assertEquals(Double.MAX_VALUE, RadiationUtils.saturatedAdd(Double.MAX_VALUE, Double.MAX_VALUE));
        assertEquals(0, RadiationUtils.saturatedMultiply(Double.POSITIVE_INFINITY, 0));
        ItemEmission huge = new ItemEmission(Double.MAX_VALUE, 1, 256);
        assertFalse(RadiationUtils.applyContainment(huge, 0, 256).isRadioactive());
        assertEquals(Double.MAX_VALUE, RadiationUtils.applyContainment(huge, Double.NaN, 256).amount());
    }

    @Test
    void containmentIsClampedAndRecalculatesRange() {
        ItemEmission source = new ItemEmission(1000000, 2, 176);
        ItemEmission half = RadiationUtils.applyContainment(source, 0.5, 256);
        assertEquals(500000, half.amount());
        assertEquals(2, half.strength());
        assertEquals(125, half.range());
        assertEquals(source, RadiationUtils.applyContainment(source, 2, 256));
        assertEquals(ItemEmission.NONE, RadiationUtils.applyContainment(source, -1, 256));
    }

    @Test
    void effectArithmeticSaturatesRatherThanWrapping() {
        assertEquals(Integer.MAX_VALUE, CapabilityRadiationRecipient.getDurationFromRadiation(Double.MAX_VALUE));
        assertEquals(40, CapabilityRadiationRecipient.getAmplitudeFromRadiation(Double.MAX_VALUE, 1));
        assertEquals(0, CapabilityRadiationRecipient.getAmplitudeFromRadiation(Double.NaN, 1));
    }
}
