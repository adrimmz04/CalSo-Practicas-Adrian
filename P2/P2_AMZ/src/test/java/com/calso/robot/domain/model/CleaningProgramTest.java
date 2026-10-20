package com.calso.robot.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.calso.robot.domain.valueobjects.Energy;

class CleaningProgramTest {

    private static final Energy ENERGY_5 = Energy.of(5);

    // --- Construcción válida y consulta de propiedades ---------------------

    @Test
    void testGivenIdP1NameBasicEnergy5AndWater2WhenCreatingProgramThenGettersReturnThoseValues() {
        CleaningProgram program = new CleaningProgram("P1", "Basic", ENERGY_5, Map.of(CleaningProduct.WATER, 2));

        assertAll(
            () -> assertEquals("P1", program.getId()),
            () -> assertEquals("Basic", program.getName()),
            () -> assertEquals(Energy.of(5), program.getEnergy()),
            () -> assertEquals(Map.of(CleaningProduct.WATER, 2), program.getRequirements()));
    }

    @Test
    void testGivenEmptyRequirementsWhenCreatingProgramThenRequirementsAreEmpty() {
        CleaningProgram program = new CleaningProgram("P1", "Dry", ENERGY_5, Map.of());

        assertTrue(program.getRequirements().isEmpty());
    }

    @Test
    void testGivenRequirementWater0WhenCreatingProgramThenStoresWaterQuantity0() {
        CleaningProgram program = new CleaningProgram("P1", "Basic", ENERGY_5, Map.of(CleaningProduct.WATER, 0));

        assertEquals(Integer.valueOf(0), program.getRequirements().get(CleaningProduct.WATER));
    }

    @Test
    void testGivenAllFourProductsRequiredWhenCreatingProgramThenStoresFourRequirements() {
        Map<CleaningProduct, Integer> requirements = Map.of(
            CleaningProduct.WATER, 3, CleaningProduct.SOAP, 1, CleaningProduct.WAX, 2, CleaningProduct.GLASS_CLEANER, 1);

        CleaningProgram program = new CleaningProgram("P1", "Full", ENERGY_5, requirements);

        assertEquals(requirements, program.getRequirements());
    }

    // --- Validación de id y name --------------------------------------------

    @Test
    void testGivenNullIdWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Map<CleaningProduct, Integer> requirements = Map.of();

        assertThrows(IllegalArgumentException.class,
            () -> new CleaningProgram(null, "Basic", ENERGY_5, requirements));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "\t\n" })
    void testGivenBlankIdWhenCreatingProgramThenThrowsIllegalArgumentException(String id) {
        Map<CleaningProduct, Integer> requirements = Map.of();

        assertThrows(IllegalArgumentException.class,
            () -> new CleaningProgram(id, "Basic", ENERGY_5, requirements));
    }

    @Test
    void testGivenNullNameWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Map<CleaningProduct, Integer> requirements = Map.of();

        assertThrows(IllegalArgumentException.class,
            () -> new CleaningProgram("P1", null, ENERGY_5, requirements));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "\t\n" })
    void testGivenBlankNameWhenCreatingProgramThenThrowsIllegalArgumentException(String name) {
        Map<CleaningProduct, Integer> requirements = Map.of();

        assertThrows(IllegalArgumentException.class,
            () -> new CleaningProgram("P1", name, ENERGY_5, requirements));
    }

    // --- Validación de energy y requirements -------------------------------

    @Test
    void testGivenNullEnergyWhenCreatingProgramThenThrowsNullPointerException() {
        Map<CleaningProduct, Integer> requirements = Map.of();

        assertThrows(NullPointerException.class,
            () -> new CleaningProgram("P1", "Basic", null, requirements));
    }

    @Test
    void testGivenNullRequirementsWhenCreatingProgramThenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
            () -> new CleaningProgram("P1", "Basic", ENERGY_5, null));
    }

    @Test
    void testGivenRequirementWithNullProductWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Map<CleaningProduct, Integer> requirements = new HashMap<>();
        requirements.put(null, 1);

        assertThrows(IllegalArgumentException.class,
            () -> new CleaningProgram("P1", "Basic", ENERGY_5, requirements));
    }

    @Test
    void testGivenRequirementWaterWithNullQuantityWhenCreatingProgramThenThrowsIllegalArgumentException() {
        Map<CleaningProduct, Integer> requirements = new HashMap<>();
        requirements.put(CleaningProduct.WATER, null);

        assertThrows(IllegalArgumentException.class,
            () -> new CleaningProgram("P1", "Basic", ENERGY_5, requirements));
    }

    @Test
    void testGivenRequirementWaterMinus1WhenCreatingProgramThenThrowsIllegalArgumentException() {
        Map<CleaningProduct, Integer> requirements = Map.of(CleaningProduct.WATER, -1);

        assertThrows(IllegalArgumentException.class,
            () -> new CleaningProgram("P1", "Basic", ENERGY_5, requirements));
    }

    // --- Copia defensiva y mapa no modificable ------------------------------

    @Test
    void testGivenOriginalMapChangedFromWater2ToWater5AfterCreationWhenGettingRequirementsThenWaterIsStill2() {
        Map<CleaningProduct, Integer> original = new HashMap<>();
        original.put(CleaningProduct.WATER, 2);
        CleaningProgram program = new CleaningProgram("P1", "Basic", ENERGY_5, original);

        original.put(CleaningProduct.WATER, 5);

        assertEquals(Integer.valueOf(2), program.getRequirements().get(CleaningProduct.WATER));
    }

    @Test
    void testGivenSoapAddedToOriginalMapAfterCreationWhenGettingRequirementsThenSoapIsAbsent() {
        Map<CleaningProduct, Integer> original = new HashMap<>();
        original.put(CleaningProduct.WATER, 2);
        CleaningProgram program = new CleaningProgram("P1", "Basic", ENERGY_5, original);

        original.put(CleaningProduct.SOAP, 1);

        assertFalse(program.getRequirements().containsKey(CleaningProduct.SOAP));
    }

    @Test
    void testGivenProgramWithWater2WhenAddingSoapToReturnedRequirementsThenThrowsUnsupportedOperationException() {
        Map<CleaningProduct, Integer> original = new HashMap<>();
        original.put(CleaningProduct.WATER, 2);
        CleaningProgram program = new CleaningProgram("P1", "Basic", ENERGY_5, original);
        Map<CleaningProduct, Integer> requirements = program.getRequirements();

        assertThrows(UnsupportedOperationException.class, () -> requirements.put(CleaningProduct.SOAP, 1));
    }

    // --- Igualdad basada únicamente en id -----------------------------------

    @Test
    void testGivenProgramsWithSameIdP1AndDifferentNameEnergyAndRequirementsWhenComparingThenTheyAreEqual() {
        CleaningProgram first = new CleaningProgram("P1", "Basic", Energy.of(5), Map.of(CleaningProduct.WATER, 2));
        CleaningProgram second = new CleaningProgram("P1", "Deep", Energy.of(9), Map.of(CleaningProduct.WAX, 1));

        assertEquals(first, second);
    }

    @Test
    void testGivenProgramsWithSameIdP1AndDifferentNamesWhenComputingHashCodeThenHashCodesAreEqual() {
        CleaningProgram first = new CleaningProgram("P1", "Basic", ENERGY_5, Map.of());
        CleaningProgram second = new CleaningProgram("P1", "Deep", ENERGY_5, Map.of());

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void testGivenProgramsWithIdsP1AndP2AndSameNameBasicWhenComparingThenTheyAreNotEqual() {
        CleaningProgram first = new CleaningProgram("P1", "Basic", ENERGY_5, Map.of());
        CleaningProgram second = new CleaningProgram("P2", "Basic", ENERGY_5, Map.of());

        assertNotEquals(first, second);
    }

    @Test
    void testGivenProgramP1WhenComparingWithNullThenTheyAreNotEqual() {
        CleaningProgram program = new CleaningProgram("P1", "Basic", ENERGY_5, Map.of());

        assertFalse(program.equals(null));
    }

    @Test
    void testGivenProgramP1WhenComparingWithStringP1ThenTheyAreNotEqual() {
        CleaningProgram program = new CleaningProgram("P1", "Basic", ENERGY_5, Map.of());
        Object other = "P1";

        assertFalse(program.equals(other));
    }
}
