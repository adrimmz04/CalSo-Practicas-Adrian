package es.um.calso.dojoreservation.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Money;
import es.um.calso.dojoreservation.domain.model.Reservation;
import es.um.calso.dojoreservation.domain.model.Weapon;

/**
 * Caja blanca de DojoSearchService (apartados 2.1 y 2.2 del README).
 * CAW = containsAllRequiredWeapons, FAD = findFirstAvailableDojo.
 */
class DojoSearchServiceTest {

    private static final Weapon KATANA = new Weapon("Katana");
    private static final Weapon BO = new Weapon("Bo");
    private static final LocalDate TRAINING_DATE = LocalDate.of(2026, 11, 20);
    private static final LocalDate NEXT_DAY = LocalDate.of(2026, 11, 21);

    private final DojoSearchService service = new DojoSearchService();

    // Crea un dojo con el identificador y las armas indicadas (precio fijo, no influye)
    private static Dojo dojo(String id, Set<Weapon> weapons) {
        return new Dojo(id, "Dojo " + id, Money.euros("40"), weapons);
    }

    // ------------------------------------------------------------------
    // containsAllRequiredWeapons
    // ------------------------------------------------------------------

    // CAW-C1: 1 -> F
    @Test
    void testGivenNullAvailableAndRequiredKatanaWhenCheckingWeaponsThenThrowsAvailableWeaponsAreRequired() {
        Set<Weapon> required = Set.of(KATANA);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.containsAllRequiredWeapons(null, required));
        assertEquals("Available weapons are required", exception.getMessage());
    }

    // CAW-C2: 1 -> 2 -> F
    @Test
    void testGivenAvailableKatanaAndNullRequiredWhenCheckingWeaponsThenThrowsRequiredWeaponsAreRequired() {
        Set<Weapon> available = Set.of(KATANA);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.containsAllRequiredWeapons(available, null));
        assertEquals("Required weapons are required", exception.getMessage());
    }

    // CAW-C3: 1 -> 2 -> 3 -> 5 -> F
    @Test
    void testGivenAvailableKatanaAndEmptyRequiredWhenCheckingWeaponsThenReturnsTrue() {
        Set<Weapon> available = Set.of(KATANA);
        Set<Weapon> required = Set.of();
        boolean result = service.containsAllRequiredWeapons(available, required);
        assertTrue(result);
    }

    // CAW-C4: 1 -> 2 -> 3 -> 4 -> 6 -> F
    @Test
    void testGivenEmptyAvailableAndRequiredKatanaWhenCheckingWeaponsThenReturnsFalse() {
        Set<Weapon> available = Set.of();
        Set<Weapon> required = Set.of(KATANA);
        boolean result = service.containsAllRequiredWeapons(available, required);
        assertFalse(result);
    }

    // CAW-C5 (a): 1 -> 2 -> 3 -> 4 -> 3 -> 5 -> F
    @Test
    void testGivenAvailableKatanaAndRequiredKatanaWhenCheckingWeaponsThenReturnsTrue() {
        Set<Weapon> available = Set.of(KATANA);
        Set<Weapon> required = Set.of(KATANA);
        boolean result = service.containsAllRequiredWeapons(available, required);
        assertTrue(result);
    }

    // CAW-C5 (b): mismo camino en el código original
    @Test
    void testGivenAvailableBoAndRequiredKatanaWhenCheckingWeaponsThenReturnsFalse() {
        Set<Weapon> available = Set.of(BO);
        Set<Weapon> required = Set.of(KATANA);
        boolean result = service.containsAllRequiredWeapons(available, required);
        assertFalse(result);
    }

    // ------------------------------------------------------------------
    // findFirstAvailableDojo
    // ------------------------------------------------------------------

    // FAD-C1: 1 -> F
    @Test
    void testGivenNullDojosWhenFindingFirstAvailableDojoThenThrowsDojosAreRequired() {
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of();
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.findFirstAvailableDojo(null, required, reservations, TRAINING_DATE));
        assertEquals("Dojos are required", exception.getMessage());
    }

    // FAD-C2: 1 -> 2 -> F
    @Test
    void testGivenNullRequiredWeaponsWhenFindingFirstAvailableDojoThenThrowsRequiredWeaponsAreRequired() {
        List<Dojo> dojos = List.of(dojo("D1", Set.of(KATANA)));
        List<Reservation> reservations = List.of();
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.findFirstAvailableDojo(dojos, null, reservations, TRAINING_DATE));
        assertEquals("Required weapons are required", exception.getMessage());
    }

    // FAD-C3: 1 -> 2 -> 3 -> F
    @Test
    void testGivenNullReservationsWhenFindingFirstAvailableDojoThenThrowsReservationsAreRequired() {
        List<Dojo> dojos = List.of(dojo("D1", Set.of(KATANA)));
        Set<Weapon> required = Set.of(KATANA);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.findFirstAvailableDojo(dojos, required, null, TRAINING_DATE));
        assertEquals("Reservations are required", exception.getMessage());
    }

    // FAD-C4: 1 -> 2 -> 3 -> 4 -> F
    @Test
    void testGivenNullTrainingDateWhenFindingFirstAvailableDojoThenThrowsTrainingDateIsRequired() {
        List<Dojo> dojos = List.of(dojo("D1", Set.of(KATANA)));
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of();
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.findFirstAvailableDojo(dojos, required, reservations, null));
        assertEquals("Training date is required", exception.getMessage());
    }

    // FAD-C5: 1 -> 2 -> 3 -> 4 -> 5 -> 13 -> F
    @Test
    void testGivenEmptyDojoListWhenFindingFirstAvailableDojoOn20261120ThenReturnsEmpty() {
        List<Dojo> dojos = List.of();
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of();
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, required, reservations, TRAINING_DATE);
        assertEquals(Optional.empty(), result);
    }

    // FAD-C6: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 5 -> 13 -> F
    @Test
    void testGivenOnlyDojoD1WithoutWeaponsAndRequiredKatanaWhenFindingFirstAvailableDojoOn20261120ThenReturnsEmpty() {
        List<Dojo> dojos = List.of(dojo("D1", Set.of()));
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of();
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, required, reservations, TRAINING_DATE);
        assertEquals(Optional.empty(), result);
    }

    // FAD-C7: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 11 -> 12 -> F
    @Test
    void testGivenDojoD1WithKatanaAndNoReservationsWhenFindingFirstAvailableDojoOn20261120ThenReturnsD1() {
        Dojo d1 = dojo("D1", Set.of(KATANA));
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of();
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, required, reservations, TRAINING_DATE);
        assertEquals(Optional.of(d1), result);
    }

    // FAD-C8: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9 -> 8 -> 11 -> 12 -> F
    // Condición A (mismo dojo) = falso; B no se evalúa
    @Test
    void testGivenDojoD1AndReservationOfD2On20261120WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1() {
        Dojo d1 = dojo("D1", Set.of(KATANA));
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of(new Reservation("D2", TRAINING_DATE));
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, required, reservations, TRAINING_DATE);
        assertEquals(Optional.of(d1), result);
    }

    // FAD-C9: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9 -> 10 -> 11 -> 5 -> 13 -> F (código original)
    // Condición A = verdadero, B (!fecha igual) = verdadero
    @Test
    void testGivenDojoD1AndReservationOfD1On20261121WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1() {
        Dojo d1 = dojo("D1", Set.of(KATANA));
        List<Dojo> dojos = List.of(d1);
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of(new Reservation("D1", NEXT_DAY));
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, required, reservations, TRAINING_DATE);
        assertEquals(Optional.of(d1), result);
    }

    // CC1 (cobertura de condiciones): A = verdadero, B (!fecha igual) = falso
    @Test
    void testGivenDojosD1AndD2AndReservationOfD1On20261120WhenFindingFirstAvailableDojoOn20261120ThenReturnsD2() {
        Dojo d1 = dojo("D1", Set.of(KATANA));
        Dojo d2 = dojo("D2", Set.of(KATANA));
        List<Dojo> dojos = List.of(d1, d2);
        Set<Weapon> required = Set.of(KATANA);
        List<Reservation> reservations = List.of(new Reservation("D1", TRAINING_DATE));
        Optional<Dojo> result = service.findFirstAvailableDojo(dojos, required, reservations, TRAINING_DATE);
        assertEquals(Optional.of(d2), result);
    }
}
