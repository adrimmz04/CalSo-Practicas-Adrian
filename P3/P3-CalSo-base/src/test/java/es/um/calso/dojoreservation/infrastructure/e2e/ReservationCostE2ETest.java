package es.um.calso.dojoreservation.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Caja negra de la pantalla de coste GET /coste (apartado 3.2 del README).
 * Reloj fijo del perfil e2e: 2026-10-20 (Europe/Madrid).
 * Datos de equipos y dojos: catálogo InMemoryCatalog.
 */
// La configuración del navegador y las esperas se heredan de BrowserE2ESupport.
class ReservationCostE2ETest extends BrowserE2ESupport {

    private static final String FUTURE_DATE_REQUIRED = "Training date must be in the future";

    // CO1: T1 + D1 + A1 (1 arma requerida)
    @Test
    void testGivenTeamEq002RequiringOneWeaponAndDojoD001WhenCalculatingCostOn20261021ThenWeaponSupplementIs500() {
        calculateCost("EQ-002", "D-001", "2026-10-21");
        assertEquals("5.00", textOf("weapons-price"));
    }

    // CO2: A2 (2 armas requeridas)
    @Test
    void testGivenTeamEq001RequiringTwoWeaponsAndDojoD001WhenCalculatingCostOn20261021ThenWeaponSupplementIs1000() {
        calculateCost("EQ-001", "D-001", "2026-10-21");
        assertEquals("10.00", textOf("weapons-price"));
    }

    // CO3: subtotal = base + suplemento
    @Test
    void testGivenTeamEq002AndDojoD002WhenCalculatingCostOn20261021ThenSubtotalIs5500() {
        calculateCost("EQ-002", "D-002", "2026-10-21");
        assertEquals("55.00", textOf("subtotal"));
    }

    // CO4: B0 (sin bonificación); límite fecha: mañana
    @Test
    void testGivenTeamEq002WithoutDiscountAndDojoD003WhenCalculatingCostOn20261021ThenFinalPriceIs4000() {
        calculateCost("EQ-002", "D-003", "2026-10-21");
        assertEquals("40.00", textOf("final-price"));
    }

    // CO5: B1 (17 años, antes del límite de 18)
    @Test
    void testGivenTeamEq003WithFighterAged17WhenCalculatingCostOn20271119ThenDiscountIs15() {
        calculateCost("EQ-003", "D-003", "2027-11-19");
        assertEquals("15", textOf("discount"));
    }

    // CO6: B0 (18 años justos, en el límite)
    @Test
    void testGivenTeamEq003WithFighterTurning18WhenCalculatingCostOn20271120ThenDiscountIs0() {
        calculateCost("EQ-003", "D-003", "2027-11-20");
        assertEquals("0", textOf("discount"));
    }

    // CO7: B0 (después del límite de 18)
    @Test
    void testGivenTeamEq003WithFighterAged18WhenCalculatingCostOn20271121ThenDiscountIs0() {
        calculateCost("EQ-003", "D-003", "2027-11-21");
        assertEquals("0", textOf("discount"));
    }

    // CO8: B2 (paro de 0 años, antes del límite de 1)
    @Test
    void testGivenTeamEq004UnemployedLessThanOneYearWhenCalculatingCostOn20261119ThenDiscountIs0() {
        calculateCost("EQ-004", "D-003", "2026-11-19");
        assertEquals("0", textOf("discount"));
    }

    // CO9: B3 (1 año justo de paro)
    @Test
    void testGivenTeamEq004UnemployedOneYearWhenCalculatingCostOn20261120ThenDiscountIs10() {
        calculateCost("EQ-004", "D-003", "2026-11-20");
        assertEquals("10", textOf("discount"));
    }

    // CO10: B3 (1 año, antes del límite de 2)
    @Test
    void testGivenTeamEq004UnemployedAlmostTwoYearsWhenCalculatingCostOn20271119ThenDiscountIs10() {
        calculateCost("EQ-004", "D-003", "2027-11-19");
        assertEquals("10", textOf("discount"));
    }

    // CO11: B4 (2 años justos de paro)
    @Test
    void testGivenTeamEq004UnemployedTwoYearsWhenCalculatingCostOn20271120ThenDiscountIs20() {
        calculateCost("EQ-004", "D-003", "2027-11-20");
        assertEquals("20", textOf("discount"));
    }

    // CO12: B4 (2 años, antes del límite de 3)
    @Test
    void testGivenTeamEq005UnemployedAlmostThreeYearsWhenCalculatingCostOn20271119ThenDiscountIs20() {
        calculateCost("EQ-005", "D-003", "2027-11-19");
        assertEquals("20", textOf("discount"));
    }

    // CO13: B5 (3 años justos de paro)
    @Test
    void testGivenTeamEq005UnemployedThreeYearsWhenCalculatingCostOn20271120ThenDiscountIs30() {
        calculateCost("EQ-005", "D-003", "2027-11-20");
        assertEquals("30", textOf("discount"));
    }

    // CO14: B6 (juventud 15 % y paro 10 %: gana el mayor)
    @Test
    void testGivenTeamEq001WithYouth15AndUnemployment10WhenCalculatingCostOn20261021ThenDiscountIs15() {
        calculateCost("EQ-001", "D-002", "2026-10-21");
        assertEquals("15", textOf("discount"));
    }

    // CO15: B6 (paro 20 % y juventud 15 %: gana el mayor, no se suman)
    @Test
    void testGivenTeamEq001WithUnemployment20AndYouth15WhenCalculatingCostOn20261120ThenDiscountIs20() {
        calculateCost("EQ-001", "D-002", "2026-11-20");
        assertEquals("20", textOf("discount"));
    }

    // CO16: precio final con bonificación
    @Test
    void testGivenTeamEq001AndDojoD002WithDiscount20WhenCalculatingCostOn20261120ThenFinalPriceIs4800() {
        calculateCost("EQ-001", "D-002", "2026-11-20");
        assertEquals("48.00", textOf("final-price"));
    }

    // CO17: R1 (dojo reservado ese día: el presupuesto se calcula igual)
    @Test
    void testGivenTeamEq001AndDojoD001ReservedOn20261120WhenCalculatingCostOn20261120ThenFinalPriceIs4000() {
        calculateCost("EQ-001", "D-001", "2026-11-20");
        assertEquals("40.00", textOf("final-price"));
    }

    // CO18: F2 (límite: hoy)
    @Test
    void testGivenTeamEq002AndDojoD003AndToday20261020WhenCalculatingCostThenShowsTrainingDateMustBeInTheFuture() {
        calculateCost("EQ-002", "D-003", "2026-10-20");
        assertEquals(FUTURE_DATE_REQUIRED, textOf("error"));
    }

    // CO19: F3 (límite: ayer)
    @Test
    void testGivenTeamEq002AndDojoD003AndYesterday20261019WhenCalculatingCostThenShowsTrainingDateMustBeInTheFuture() {
        calculateCost("EQ-002", "D-003", "2026-10-19");
        assertEquals(FUTURE_DATE_REQUIRED, textOf("error"));
    }

    // CO20: T4 (equipo inexistente por URL)
    @Test
    void testGivenUnknownTeamEq999InUrlWhenCalculatingCostThenShowsTeamDoesNotExist() {
        open("/coste?teamId=EQ-999&dojoId=D-003&trainingDate=2026-11-20");
        assertEquals("Team does not exist", textOf("error"));
    }

    // CO21: D4 (dojo inexistente por URL)
    @Test
    void testGivenUnknownDojoD999InUrlWhenCalculatingCostThenShowsDojoDoesNotExist() {
        open("/coste?teamId=EQ-002&dojoId=D-999&trainingDate=2026-11-20");
        assertEquals("Dojo does not exist", textOf("error"));
    }

    // CO22: T2 (equipo en blanco por URL)
    @Test
    void testGivenBlankTeamIdInUrlWhenCalculatingCostThenShowsTeamIdIsRequired() {
        open("/coste?teamId=%20&dojoId=D-003&trainingDate=2026-11-20");
        assertEquals("Team id is required", textOf("error"));
    }

    // CO23: D2 (dojo en blanco por URL)
    @Test
    void testGivenBlankDojoIdInUrlWhenCalculatingCostThenShowsDojoIdIsRequired() {
        open("/coste?teamId=EQ-002&dojoId=%20&trainingDate=2026-11-20");
        assertEquals("Dojo id is required", textOf("error"));
    }

    // CO24: T3 (equipo vacío por URL)
    @Test
    void testGivenEmptyTeamIdInUrlWhenCalculatingCostThenShowsTeamIdIsRequired() {
        open("/coste?teamId=&dojoId=D-003&trainingDate=2026-11-20");
        assertEquals("Team id is required", textOf("error"));
    }

    // CO25: D3 (dojo vacío por URL)
    @Test
    void testGivenEmptyDojoIdInUrlWhenCalculatingCostThenShowsDojoIdIsRequired() {
        open("/coste?teamId=EQ-002&dojoId=&trainingDate=2026-11-20");
        assertEquals("Dojo id is required", textOf("error"));
    }
}
