package com.calso.robot.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EnergyTest {

    // --- Creación y valor -------------------------------------------------

    @Test
    void testGivenUnits1WhenCreatingEnergyThenStoresValue1() {
        Energy energy = Energy.of(1);

        assertEquals(1, energy.getValue());
    }

    @Test
    void testGivenUnits10WhenCreatingEnergyThenStoresValue10() {
        Energy energy = Energy.of(10);

        assertEquals(10, energy.getValue());
    }

    @Test
    void testGivenMaxIntegerUnitsWhenCreatingEnergyThenStoresMaxIntegerValue() {
        Energy energy = Energy.of(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, energy.getValue());
    }

    @Test
    void testGivenUnits0WhenCreatingEnergyThenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Energy.of(0));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, -10, Integer.MIN_VALUE })
    void testGivenNegativeUnitsWhenCreatingEnergyThenThrowsIllegalArgumentException(int units) {
        assertThrows(IllegalArgumentException.class, () -> Energy.of(units));
    }

    // --- Igualdad -----------------------------------------------------------

    @Test
    void testGivenTwoEnergiesWithUnits10WhenComparingThenTheyAreEqual() {
        Energy first = Energy.of(10);
        Energy second = Energy.of(10);

        assertEquals(first, second);
    }

    @Test
    void testGivenTwoEnergiesWithUnits10WhenComputingHashCodeThenHashCodesAreEqual() {
        Energy first = Energy.of(10);
        Energy second = Energy.of(10);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void testGivenEnergiesWithUnits10And11WhenComparingThenTheyAreNotEqual() {
        Energy first = Energy.of(10);
        Energy second = Energy.of(11);

        assertNotEquals(first, second);
    }

    @Test
    void testGivenEnergyWithUnits10WhenComparingWithNullThenTheyAreNotEqual() {
        Energy energy = Energy.of(10);

        assertFalse(energy.equals(null));
    }

    @Test
    void testGivenEnergyWithUnits10WhenComparingWithIntegerValue10ThenTheyAreNotEqual() {
        Energy energy = Energy.of(10);
        Object other = Integer.valueOf(10);

        assertFalse(energy.equals(other));
    }
}
