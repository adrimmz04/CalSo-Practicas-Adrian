package com.calso.robot.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.calso.robot.domain.model.CleaningProduct;

/**
 * Pruebas del propio suplantador: comprueban que cumple el contrato de
 * {@code ICleaningInventory} antes de usarlo para probar el caso de uso.
 */
class StubCleaningInventoryTest {

    @Test
    void testGivenWater3WhenQueryingWaterThenReturns3() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        assertEquals(3, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenStockWithoutWaxWhenQueryingWaxThenReturns0() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        assertEquals(0, inventory.available(CleaningProduct.WAX));
    }

    @Test
    void testGivenWater3WhenConsumingWater2ThenWaterBecomes1() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        inventory.consume(CleaningProduct.WATER, 2);

        assertEquals(1, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenWater3WhenConsumingWater3ThenWaterBecomes0() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        inventory.consume(CleaningProduct.WATER, 3);

        assertEquals(0, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenWater3WhenConsumingWater0ThenWaterRemains3() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        inventory.consume(CleaningProduct.WATER, 0);

        assertEquals(3, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenWater3WhenConsumingWater4ThenThrowsIllegalArgumentExceptionAndWaterRemains3() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        assertThrows(IllegalArgumentException.class, () -> inventory.consume(CleaningProduct.WATER, 4));
        assertEquals(3, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenWater3WhenConsumingWaterMinus1ThenThrowsIllegalArgumentExceptionAndWaterRemains3() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        assertThrows(IllegalArgumentException.class, () -> inventory.consume(CleaningProduct.WATER, -1));
        assertEquals(3, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenStockWithoutWaxWhenConsumingWax1ThenThrowsIllegalArgumentException() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        assertThrows(IllegalArgumentException.class, () -> inventory.consume(CleaningProduct.WAX, 1));
    }

    @Test
    void testGivenTwoInventoriesWithWater3WhenConsumingWater2InFirstThenSecondKeepsWater3() {
        StubCleaningInventory first = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));
        StubCleaningInventory second = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));

        first.consume(CleaningProduct.WATER, 2);

        assertEquals(3, second.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenInitialMapChangedFromWater3ToWater9AfterCreationWhenQueryingWaterThenReturns3() {
        Map<CleaningProduct, Integer> initial = new HashMap<>();
        initial.put(CleaningProduct.WATER, 3);
        StubCleaningInventory inventory = new StubCleaningInventory(initial);

        initial.put(CleaningProduct.WATER, 9);

        assertEquals(3, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenInitialStockWaterMinus1WhenCreatingInventoryThenThrowsIllegalArgumentException() {
        Map<CleaningProduct, Integer> initial = Map.of(CleaningProduct.WATER, -1);

        assertThrows(IllegalArgumentException.class, () -> new StubCleaningInventory(initial));
    }
}
