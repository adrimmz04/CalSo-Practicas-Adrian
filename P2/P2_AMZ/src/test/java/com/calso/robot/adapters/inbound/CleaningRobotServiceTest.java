package com.calso.robot.adapters.inbound;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.calso.robot.application.usecases.StubCleaningInventory;
import com.calso.robot.domain.model.CleaningProduct;
import com.calso.robot.domain.model.CleaningProgram;
import com.calso.robot.domain.model.ExecutionResult;
import com.calso.robot.domain.valueobjects.Energy;

class CleaningRobotServiceTest {

    private static CleaningProgram programRequiring(Map<CleaningProduct, Integer> requirements) {
        return new CleaningProgram("P1", "Program", Energy.of(5), requirements);
    }

    @Test
    void testGivenWater2WhenExecutingProgramRequiringWater2ThroughServiceThenExecutesAndWaterStockBecomes0() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.WATER, 2));
        CleaningRobotService service = new CleaningRobotService(inventory);

        ExecutionResult result = service.executeProgram(programRequiring(Map.of(CleaningProduct.WATER, 2)));

        assertAll(
            () -> assertTrue(result.executed()),
            () -> assertEquals(0, inventory.available(CleaningProduct.WATER)));
    }

    @Test
    void testGivenSoap0WhenExecutingProgramRequiringSoap1ThroughServiceThenReportsSoap1Missing() {
        StubCleaningInventory inventory = new StubCleaningInventory(Map.of(CleaningProduct.SOAP, 0));
        CleaningRobotService service = new CleaningRobotService(inventory);

        ExecutionResult result = service.executeProgram(programRequiring(Map.of(CleaningProduct.SOAP, 1)));

        assertAll(
            () -> assertFalse(result.executed()),
            () -> assertEquals(Map.of(CleaningProduct.SOAP, 1), result.missingProducts()));
    }

    @Test
    void testGivenNullInventoryWhenCreatingServiceThenThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CleaningRobotService(null));
    }
}
