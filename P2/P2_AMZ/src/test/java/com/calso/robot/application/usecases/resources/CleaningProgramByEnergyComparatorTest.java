package com.calso.robot.application.usecases.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.calso.robot.domain.model.CleaningProgram;
import com.calso.robot.domain.valueobjects.Energy;

class CleaningProgramByEnergyComparatorTest {

    private final CleaningProgramByEnergyComparator comparator = new CleaningProgramByEnergyComparator();

    private static CleaningProgram program(String id, int energy) {
        return new CleaningProgram(id, "Program " + id, Energy.of(energy), Map.of());
    }

    // --- Criterio principal: energía ascendente -----------------------------

    @Test
    void testGivenEnergies3And5WhenComparingProgramsThenReturnsNegative() {
        assertTrue(comparator.compare(program("A", 3), program("B", 5)) < 0);
    }

    @Test
    void testGivenEnergies5And3WhenComparingProgramsThenReturnsPositive() {
        assertTrue(comparator.compare(program("A", 5), program("B", 3)) > 0);
    }

    @Test
    void testGivenEnergy1WithIdZAndEnergy2WithIdAWhenComparingProgramsThenLowerEnergyGoesFirst() {
        assertTrue(comparator.compare(program("Z", 1), program("A", 2)) < 0);
    }

    // --- Criterio de desempate: id lexicográfico ascendente ----------------

    @Test
    void testGivenSameEnergy4AndIdsAAndBWhenComparingProgramsThenReturnsNegative() {
        assertTrue(comparator.compare(program("A", 4), program("B", 4)) < 0);
    }

    @Test
    void testGivenSameEnergy4AndIdsBAndAWhenComparingProgramsThenReturnsPositive() {
        assertTrue(comparator.compare(program("B", 4), program("A", 4)) > 0);
    }

    @Test
    void testGivenSameEnergy4AndSameIdAWithDifferentNamesWhenComparingProgramsThenReturns0() {
        CleaningProgram first = new CleaningProgram("A", "Basic", Energy.of(4), Map.of());
        CleaningProgram second = new CleaningProgram("A", "Deep", Energy.of(4), Map.of());

        assertEquals(0, comparator.compare(first, second));
    }

    // --- Ordenación de listas -----------------------------------------------

    @Test
    void testGivenProgramsWithEnergies5And1And3WhenSortingThenOrdersEnergies1And3And5() {
        CleaningProgram energy5 = program("P5", 5);
        CleaningProgram energy1 = program("P1", 1);
        CleaningProgram energy3 = program("P3", 3);
        List<CleaningProgram> programs = new ArrayList<>(List.of(energy5, energy1, energy3));

        programs.sort(comparator);

        assertIterableEquals(List.of(energy1, energy3, energy5), programs);
    }

    @Test
    void testGivenEnergy2IdsCAndAAndEnergy1IdBWhenSortingThenOrdersBThenAThenC() {
        CleaningProgram energy2IdC = program("C", 2);
        CleaningProgram energy2IdA = program("A", 2);
        CleaningProgram energy1IdB = program("B", 1);
        List<CleaningProgram> programs = new ArrayList<>(List.of(energy2IdC, energy2IdA, energy1IdB));

        programs.sort(comparator);

        assertIterableEquals(List.of(energy1IdB, energy2IdA, energy2IdC), programs);
    }
}
