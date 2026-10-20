package com.calso.robot.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ExecutionResultTest {

    @Test
    void testGivenExecutedTrueAndEmptyMapWhenCreatingResultThenIsExecutedWithoutMissingProducts() {
        ExecutionResult result = new ExecutionResult(true, Map.of());

        assertAll(
            () -> assertTrue(result.executed()),
            () -> assertTrue(result.missingProducts().isEmpty()));
    }

    @Test
    void testGivenExecutedFalseAndSoap1MissingWhenCreatingResultThenIsNotExecutedAndReportsSoap1() {
        ExecutionResult result = new ExecutionResult(false, Map.of(CleaningProduct.SOAP, 1));

        assertAll(
            () -> assertFalse(result.executed()),
            () -> assertEquals(Map.of(CleaningProduct.SOAP, 1), result.missingProducts()));
    }

    @Test
    void testGivenExecutedTrueAndSoap1MissingWhenCreatingResultThenThrowsIllegalArgumentException() {
        Map<CleaningProduct, Integer> missing = Map.of(CleaningProduct.SOAP, 1);

        assertThrows(IllegalArgumentException.class, () -> new ExecutionResult(true, missing));
    }

    @Test
    void testGivenNullMissingProductsWhenCreatingResultThenThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExecutionResult(false, null));
    }

    @Test
    void testGivenSoap1MissingWhenAddingWaxToReturnedMissingProductsThenThrowsUnsupportedOperationException() {
        ExecutionResult result = new ExecutionResult(false, Map.of(CleaningProduct.SOAP, 1));
        Map<CleaningProduct, Integer> missing = result.missingProducts();

        assertThrows(UnsupportedOperationException.class, () -> missing.put(CleaningProduct.WAX, 1));
    }

    @Test
    void testGivenOriginalMapWithSoap1ChangedToSoap7AfterCreationWhenGettingMissingProductsThenSoapIsStill1() {
        Map<CleaningProduct, Integer> original = new HashMap<>();
        original.put(CleaningProduct.SOAP, 1);
        ExecutionResult result = new ExecutionResult(false, original);

        original.put(CleaningProduct.SOAP, 7);

        assertEquals(Integer.valueOf(1), result.missingProducts().get(CleaningProduct.SOAP));
    }
}
