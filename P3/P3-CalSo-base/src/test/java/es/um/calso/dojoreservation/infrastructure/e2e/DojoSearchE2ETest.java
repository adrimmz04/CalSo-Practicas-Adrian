package es.um.calso.dojoreservation.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

/**
 * Caja negra de la pantalla de búsqueda GET /dojos (apartado 3.1 del README).
 * Reloj fijo del perfil e2e: 2026-10-20 (Europe/Madrid).
 * Reservas del catálogo: D-001 el 2026-11-20 y el 2027-11-20.
 */
// La configuración del navegador y las esperas se heredan de BrowserE2ESupport.
class DojoSearchE2ETest extends BrowserE2ESupport {

    private static final String FUTURE_DATE_REQUIRED = "Training date must be in the future";

    // CB1: T1 + F1 + R1 (límite: mañana, primer día válido)
    @Test
    void testGivenTeamEq001AndTomorrow20261021WhenSearchingDojosThenSelectsD001() {
        searchDojos("EQ-001", "2026-10-21");
        assertEquals("D-001", textOf("dojo-id"));
    }

    // CB2: F2 (límite: hoy)
    @Test
    void testGivenTeamEq001AndToday20261020WhenSearchingDojosThenShowsTrainingDateMustBeInTheFuture() {
        searchDojos("EQ-001", "2026-10-20");
        assertEquals(FUTURE_DATE_REQUIRED, textOf("error"));
    }

    // CB3: F3 (límite: ayer)
    @Test
    void testGivenTeamEq001AndYesterday20261019WhenSearchingDojosThenShowsTrainingDateMustBeInTheFuture() {
        searchDojos("EQ-001", "2026-10-19");
        assertEquals(FUTURE_DATE_REQUIRED, textOf("error"));
    }

    // CB4: R2 (límite de reserva: el mismo día de la reserva de D-001)
    @Test
    void testGivenTeamEq001AndD001Reserved20261120WhenSearchingDojosOn20261120ThenSelectsD002() {
        searchDojos("EQ-001", "2026-11-20");
        assertEquals("D-002", textOf("dojo-id"));
    }

    // CB5: R2, misma entrada que CB4, otra salida: la fila de D-001 indica que está ocupado
    @Test
    void testGivenTeamEq001AndD001Reserved20261120WhenSearchingDojosOn20261120ThenD001RowShowsOcupado() {
        searchDojos("EQ-001", "2026-11-20");
        String row = textAt(By.cssSelector("#dojo-table tr[data-dojo-id='D-001']"));
        assertTrue(row.contains("Ocupado"));
    }

    // CB6: R3 (límite de reserva: el día anterior a la reserva de D-001)
    @Test
    void testGivenTeamEq001AndD001Reserved20261120WhenSearchingDojosOn20261119ThenSelectsD001() {
        searchDojos("EQ-001", "2026-11-19");
        assertEquals("D-001", textOf("dojo-id"));
    }

    // CB7: R3 (límite de reserva: el día posterior a la reserva de D-001)
    @Test
    void testGivenTeamEq001AndD001Reserved20261120WhenSearchingDojosOn20261121ThenSelectsD001() {
        searchDojos("EQ-001", "2026-11-21");
        assertEquals("D-001", textOf("dojo-id"));
    }

    // CB8: R4 (el primer dojo de la lista no tiene el Bo que exige EQ-002)
    @Test
    void testGivenTeamEq002RequiringBoWhenSearchingDojosOn20261120ThenSelectsD002() {
        searchDojos("EQ-002", "2026-11-20");
        assertEquals("D-002", textOf("dojo-id"));
    }

    // CB9: T4 (identificador inexistente por URL)
    @Test
    void testGivenUnknownTeamEq999InUrlWhenSearchingDojosOn20261120ThenShowsTeamDoesNotExist() {
        open("/dojos?teamId=EQ-999&trainingDate=2026-11-20");
        assertEquals("Team does not exist", textOf("error"));
    }

    // CB10: T2 (identificador en blanco por URL)
    @Test
    void testGivenBlankTeamIdInUrlWhenSearchingDojosOn20261120ThenShowsTeamIdIsRequired() {
        open("/dojos?teamId=%20&trainingDate=2026-11-20");
        assertEquals("Team id is required", textOf("error"));
    }

    // CB11: T3 (identificador vacío por URL)
    @Test
    void testGivenEmptyTeamIdInUrlWhenSearchingDojosOn20261120ThenShowsTeamIdIsRequired() {
        open("/dojos?teamId=&trainingDate=2026-11-20");
        assertEquals("Team id is required", textOf("error"));
    }

    // CB12: F4 (fecha imposible por URL: Spring MVC la rechaza antes de ejecutar el controlador)
    @Test
    void testGivenImpossibleDate20261340InUrlWhenSearchingDojosThenSearchScreenIsNotShown() {
        open("/dojos?teamId=EQ-001&trainingDate=2026-13-40");
        assertTrue(browser.findElements(By.id("search")).isEmpty());
    }
}
