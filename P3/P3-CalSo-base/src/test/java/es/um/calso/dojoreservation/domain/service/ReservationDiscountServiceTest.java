package es.um.calso.dojoreservation.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import es.um.calso.dojoreservation.domain.model.Discount;
import es.um.calso.dojoreservation.domain.model.Fighter;
import es.um.calso.dojoreservation.domain.model.UnemploymentPeriod;

/**
 * Caja blanca de ReservationDiscountService (apartados 2.3 y 2.4 del README).
 * FYF = findYoungestFighter, CBD = calculateBestDiscount.
 * Fecha de referencia / entrenamiento de todas las pruebas: 2026-11-20.
 */
class ReservationDiscountServiceTest {

    private static final LocalDate TRAINING_DATE = LocalDate.of(2026, 11, 20);

    private final ReservationDiscountService service = new ReservationDiscountService();

    // Luchador sin paro
    private static Fighter fighter(String name, LocalDate birthDate) {
        return new Fighter(name, birthDate, null);
    }

    // Luchador en paro desde la fecha indicada
    private static Fighter unemployedFighter(String name, LocalDate birthDate, LocalDate unemploymentStart) {
        return new Fighter(name, birthDate, new UnemploymentPeriod(unemploymentStart));
    }

    // ------------------------------------------------------------------
    // findYoungestFighter
    // ------------------------------------------------------------------

    // FYF-C1: 1 -> F
    @Test
    void testGivenNullFightersWhenFindingYoungestFighterThenThrowsFightersAreRequired() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.findYoungestFighter(null, TRAINING_DATE));
        assertEquals("Fighters are required", exception.getMessage());
    }

    // FYF-C2: 1 -> 2 -> F
    @Test
    void testGivenAnaBorn20000510AndNullReferenceDateWhenFindingYoungestFighterThenThrowsReferenceDateIsRequired() {
        List<Fighter> fighters = List.of(fighter("Ana", LocalDate.of(2000, 5, 10)));
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.findYoungestFighter(fighters, null));
        assertEquals("Reference date is required", exception.getMessage());
    }

    // FYF-C3: 1 -> 2 -> 3 -> 4 -> 8 -> F
    @Test
    void testGivenEmptyFighterListWhenFindingYoungestFighterThenReturnsEmpty() {
        List<Fighter> fighters = List.of();
        Optional<Fighter> result = service.findYoungestFighter(fighters, TRAINING_DATE);
        assertEquals(Optional.empty(), result);
    }

    // FYF-C4: 1 -> 2 -> 3 -> 4 -> 5 -> F
    @Test
    void testGivenListWithNullFighterWhenFindingYoungestFighterThenThrowsNullFighter() {
        List<Fighter> fighters = Arrays.asList((Fighter) null);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.findYoungestFighter(fighters, TRAINING_DATE));
        assertEquals("Null fighter", exception.getMessage());
    }

    // FYF-C5: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 4 -> 8 -> F
    // Condición A (youngest == null) = verdadero; B no se evalúa
    @Test
    void testGivenOnlyAnaBorn20000510WhenFindingYoungestFighterThenReturnsAna() {
        Fighter ana = fighter("Ana", LocalDate.of(2000, 5, 10));
        List<Fighter> fighters = List.of(ana);
        Optional<Fighter> result = service.findYoungestFighter(fighters, TRAINING_DATE);
        assertEquals(Optional.of(ana), result);
    }

    // FYF-C6: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 4 -> 5 -> 6 -> 4 -> 8 -> F (código original)
    // Segunda vuelta: A = falso, B = falso
    @Test
    void testGivenAnaBorn20000510ThenBeaBorn20050315WhenFindingYoungestFighterThenReturnsBea() {
        Fighter ana = fighter("Ana", LocalDate.of(2000, 5, 10));
        Fighter bea = fighter("Bea", LocalDate.of(2005, 3, 15));
        List<Fighter> fighters = List.of(ana, bea);
        Optional<Fighter> result = service.findYoungestFighter(fighters, TRAINING_DATE);
        assertEquals(Optional.of(bea), result);
    }

    // CC2 (cobertura de condiciones): segunda vuelta con A = falso, B = verdadero
    @Test
    void testGivenBeaBorn20050315ThenAnaBorn20000510WhenFindingYoungestFighterThenReturnsBea() {
        Fighter bea = fighter("Bea", LocalDate.of(2005, 3, 15));
        Fighter ana = fighter("Ana", LocalDate.of(2000, 5, 10));
        List<Fighter> fighters = List.of(bea, ana);
        Optional<Fighter> result = service.findYoungestFighter(fighters, TRAINING_DATE);
        assertEquals(Optional.of(bea), result);
    }

    // ------------------------------------------------------------------
    // calculateBestDiscount
    // Ana nace el 1996-01-10 (30 años), Bea el 2009-06-01 (17 años), Carlos el 1990-02-02
    // ------------------------------------------------------------------

    // CBD-C1: 1 -> F
    @Test
    void testGivenNullFightersWhenCalculatingBestDiscountThenThrowsFightersAreRequired() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.calculateBestDiscount(null, TRAINING_DATE));
        assertEquals("Fighters are required", exception.getMessage());
    }

    // CBD-C2: 1 -> 2 -> F
    @Test
    void testGivenAnaBorn19960110AndNullTrainingDateWhenCalculatingBestDiscountThenThrowsTrainingDateIsRequired() {
        List<Fighter> fighters = List.of(fighter("Ana", LocalDate.of(1996, 1, 10)));
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.calculateBestDiscount(fighters, null));
        assertEquals("Training date is required", exception.getMessage());
    }

    // CBD-C3: 1 -> 2 -> 3 -> 4 -> 8 -> 20 -> F
    @Test
    void testGivenEmptyFighterListWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0() {
        List<Fighter> fighters = List.of();
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(0), result);
    }

    // CBD-C5: P -> 8 -> 9 -> 10 -> 8 -> 20 -> F
    @Test
    void testGivenAnaAged30WithoutUnemploymentWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0() {
        List<Fighter> fighters = List.of(fighter("Ana", LocalDate.of(1996, 1, 10)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(0), result);
    }

    // CBD-C6: P -> 7 -> 8 -> 9 -> 10 -> 8 -> 20 -> F
    @Test
    void testGivenBeaAged17WithoutUnemploymentWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15() {
        List<Fighter> fighters = List.of(fighter("Bea", LocalDate.of(2009, 6, 1)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(15), result);
    }

    // CBD-C9: P -> 8 -> 9 -> 10 -> 11 -> 12 -> 13 -> 18 -> 19 -> 8 -> 20 -> F (3 años justos)
    @Test
    void testGivenAnaUnemployedSince20231120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount30() {
        List<Fighter> fighters = List.of(
                unemployedFighter("Ana", LocalDate.of(1996, 1, 10), LocalDate.of(2023, 11, 20)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(30), result);
    }

    // CBD-C10: P -> 8 -> ... -> 12 -> 14 -> 16 -> 18 -> 8 -> 20 -> F (0 años)
    @Test
    void testGivenAnaUnemployedSince20260501WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0() {
        List<Fighter> fighters = List.of(
                unemployedFighter("Ana", LocalDate.of(1996, 1, 10), LocalDate.of(2026, 5, 1)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(0), result);
    }

    // CBD-C11: P -> 8 -> ... -> 12 -> 14 -> 16 -> 17 -> 18 -> 19 -> 8 -> 20 -> F (1 año justo)
    @Test
    void testGivenAnaUnemployedSince20251120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount10() {
        List<Fighter> fighters = List.of(
                unemployedFighter("Ana", LocalDate.of(1996, 1, 10), LocalDate.of(2025, 11, 20)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(10), result);
    }

    // CBD-C13: P -> 7 -> 8 -> ... -> 16 -> 17 -> 18 -> 8 -> 20 -> F (el 10 % no supera el 15 %)
    @Test
    void testGivenBeaAged17UnemployedSince20250601WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15() {
        List<Fighter> fighters = List.of(
                unemployedFighter("Bea", LocalDate.of(2009, 6, 1), LocalDate.of(2025, 6, 1)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(15), result);
    }

    // CBD-C14: dos vueltas del bucle
    @Test
    void testGivenAnaUnemployed3YearsThenCarlosUnemployed1YearWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount30() {
        List<Fighter> fighters = List.of(
                unemployedFighter("Ana", LocalDate.of(1996, 1, 10), LocalDate.of(2023, 11, 20)),
                unemployedFighter("Carlos", LocalDate.of(1990, 2, 2), LocalDate.of(2025, 11, 20)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(30), result);
    }

    // Adicional (dato de C12): 2 años justos de paro
    @Test
    void testGivenAnaUnemployedSince20241120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount20() {
        List<Fighter> fighters = List.of(
                unemployedFighter("Ana", LocalDate.of(1996, 1, 10), LocalDate.of(2024, 11, 20)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(20), result);
    }

    // Adicional: adulto y menor sin paro
    @Test
    void testGivenAnaAged30AndBeaAged17WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15() {
        List<Fighter> fighters = List.of(fighter("Ana", LocalDate.of(1996, 1, 10)), fighter("Bea", LocalDate.of(2009, 6, 1)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(15), result);
    }

    // Adicional (análisis PIT): el más joven cumple 18 años justo el día del entrenamiento
    // Mata el mutante que cambia age < 18 por age <= 18
    @Test
    void testGivenEvaTurning18On20261120WithoutUnemploymentWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0() {
        List<Fighter> fighters = List.of(fighter("Eva", LocalDate.of(2008, 11, 20)));
        Discount result = service.calculateBestDiscount(fighters, TRAINING_DATE);
        assertEquals(new Discount(0), result);
    }
}
