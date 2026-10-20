package com.calso.robot.application.usecases;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.calso.robot.domain.model.CleaningProduct;
import com.calso.robot.domain.model.CleaningProgram;
import com.calso.robot.domain.model.ExecutionResult;
import com.calso.robot.domain.valueobjects.Energy;

/**
 * Pruebas del caso de uso con el inventario suplantado por
 * {@link StubCleaningInventory}: cada prueba fija sus propias existencias y
 * comprueba tanto el resultado devuelto como las existencias finales.
 */
class ExecuteCleaningProgramUseCaseTest {

    private static CleaningProgram programRequiring(Map<CleaningProduct, Integer> requirements) {
        return new CleaningProgram("P1", "Program", Energy.of(5), requirements);
    }

    // --- Ejecución permitida ------------------------------------------------

    @Test
    void testGivenWater5WhenExecutingProgramRequiringWater2ThenExecutesAndWaterStockBecomes3() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 5));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(programRequiring(Map.of(CleaningProduct.WATER, 2)));

        assertAll(
            () -> assertTrue(result.executed()),
            () -> assertTrue(result.missingProducts().isEmpty()),
            () -> assertEquals(3, inventory.available(CleaningProduct.WATER)));
    }

    @Test
    void testGivenWater2WhenExecutingProgramRequiringWater2ThenExecutesAndWaterStockBecomes0() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 2));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(programRequiring(Map.of(CleaningProduct.WATER, 2)));

        assertAll(
            () -> assertTrue(result.executed()),
            () -> assertTrue(result.missingProducts().isEmpty()),
            () -> assertEquals(0, inventory.available(CleaningProduct.WATER)));
    }

    @Test
    void testGivenWater4AndSoap2WhenExecutingProgramRequiringWater1AndSoap2ThenExecutesAndStocksBecomeWater3AndSoap0() {
        StubCleaningInventory inventory = new StubCleaningInventory(
            Map.of(CleaningProduct.WATER, 4, CleaningProduct.SOAP, 2));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(
            programRequiring(Map.of(CleaningProduct.WATER, 1, CleaningProduct.SOAP, 2)));

        assertAll(
            () -> assertTrue(result.executed()),
            () -> assertEquals(3, inventory.available(CleaningProduct.WATER)),
            () -> assertEquals(0, inventory.available(CleaningProduct.SOAP)));
    }

    // --- Ejecución rechazada: productos insuficientes ----------------------

    @Test
    void testGivenSoap1WhenExecutingProgramRequiringSoap3ThenNotExecutedAndReportsSoap2Missing() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.SOAP, 1));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(programRequiring(Map.of(CleaningProduct.SOAP, 3)));

        assertAll(
            () -> assertFalse(result.executed()),
            () -> assertEquals(Map.of(CleaningProduct.SOAP, 2), result.missingProducts()));
    }

    @Test
    void testGivenSoap1WhenExecutingProgramRequiringSoap3ThenSoapStockRemains1() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.SOAP, 1));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        useCase.execute(programRequiring(Map.of(CleaningProduct.SOAP, 3)));

        assertEquals(1, inventory.available(CleaningProduct.SOAP));
    }

    @Test
    void testGivenEmptyStockWhenExecutingProgramRequiringWax2ThenReportsWax2Missing() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of());
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(programRequiring(Map.of(CleaningProduct.WAX, 2)));

        assertAll(
            () -> assertFalse(result.executed()),
            () -> assertEquals(Map.of(CleaningProduct.WAX, 2), result.missingProducts()));
    }

    @Test
    void testGivenWater1AndSoap0WhenExecutingProgramRequiringWater3AndSoap2ThenReportsWater2AndSoap2Missing() {
        StubCleaningInventory inventory = new StubCleaningInventory(
            Map.of(CleaningProduct.WATER, 1, CleaningProduct.SOAP, 0));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(
            programRequiring(Map.of(CleaningProduct.WATER, 3, CleaningProduct.SOAP, 2)));

        assertAll(
            () -> assertFalse(result.executed()),
            () -> assertEquals(Map.of(CleaningProduct.WATER, 2, CleaningProduct.SOAP, 2), result.missingProducts()));
    }

    @Test
    void testGivenWater5AndSoap0WhenExecutingProgramRequiringWater2AndSoap1ThenWaterStockRemains5() {
        StubCleaningInventory inventory = new StubCleaningInventory(
            Map.of(CleaningProduct.WATER, 5, CleaningProduct.SOAP, 0));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        useCase.execute(programRequiring(Map.of(CleaningProduct.WATER, 2, CleaningProduct.SOAP, 1)));

        assertEquals(5, inventory.available(CleaningProduct.WATER));
    }

    @Test
    void testGivenWater2AndSoap0WhenExecutingProgramRequiringWater2AndSoap1ThenReportsSoap1MissingAndPreservesStock() {
        StubCleaningInventory inventory = new StubCleaningInventory(
            Map.of(CleaningProduct.WATER, 2, CleaningProduct.SOAP, 0));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(
            programRequiring(Map.of(CleaningProduct.WATER, 2, CleaningProduct.SOAP, 1)));

        assertAll(
            () -> assertFalse(result.executed()),
            () -> assertEquals(Map.of(CleaningProduct.SOAP, 1), result.missingProducts()),
            () -> assertEquals(2, inventory.available(CleaningProduct.WATER)),
            () -> assertEquals(0, inventory.available(CleaningProduct.SOAP)));
    }

    // --- Programas sin productos y cantidades cero --------------------------

    @Test
    void testGivenWater2WhenExecutingProgramWithoutRequirementsThenExecutesAndWaterStockRemains2() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 2));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(programRequiring(Map.of()));

        assertAll(
            () -> assertTrue(result.executed()),
            () -> assertTrue(result.missingProducts().isEmpty()),
            () -> assertEquals(2, inventory.available(CleaningProduct.WATER)));
    }

    @Test
    void testGivenEmptyStockWhenExecutingProgramRequiringWater0ThenExecutesWithoutMissingProducts() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of());
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        ExecutionResult result = useCase.execute(programRequiring(Map.of(CleaningProduct.WATER, 0)));

        assertAll(
            () -> assertTrue(result.executed()),
            () -> assertTrue(result.missingProducts().isEmpty()));
    }

    @Test
    void testGivenWater3WhenExecutingProgramRequiringWater0ThenWaterStockRemains3() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 3));
        ExecuteCleaningProgramUseCase useCase = new ExecuteCleaningProgramUseCase(inventory);

        useCase.execute(programRequiring(Map.of(CleaningProduct.WATER, 0)));

        assertEquals(3, inventory.available(CleaningProduct.WATER));
    }

    // --- Argumentos nulos ----------------------------------------------------

    @Test
    void testGivenNullInventoryWhenCreatingUseCaseThenThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExecuteCleaningProgramUseCase(null));
    }

    @Test
    void testGivenWater2WhenExecutingNullProgramThenThrowsNullPointerException() {
        ExecuteCleaningProgramUseCase useCase =
            new ExecuteCleaningProgramUseCase(new StubCleaningInventory(Map.of(CleaningProduct.WATER, 2)));

        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }
}
