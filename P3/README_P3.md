# Práctica 3 — Pruebas de caja blanca y de caja negra

**Caso de estudio:** Dojo Reservation · [Enunciado](docs/ENUNCIADO.md) · [Especificación funcional](docs/ESPECIFICACION.md) · [Guía de pruebas](docs/GUIA_PRUEBAS.md)

---

## 1. Identificación

| Integrante | Responsabilidades | Rama |
|---|---|---|
| Adrián Martínez Zamora | Práctica individual: caja blanca de los cuatro métodos de dominio, cobertura de las dos condiciones seleccionadas, caja negra de las dos pantallas (`/dojos` y `/coste`), análisis de calidad y vídeo | `main` |

Al tratarse de un grupo de un único integrante, todo el trabajo se ha realizado en la rama `main`.

**Uso de herramientas de IA:** se ha utilizado Claude (IA de Anthropic) como herramienta de apoyo: para resolver dudas durante el diseño de caja blanca (grafos, complejidad ciclomática, caminos, datos de prueba y cobertura de condiciones) y para generar, a partir de ese diseño, el código de las pruebas JUnit de los apartados 2.1 a 2.4 y de las correcciones D1 a D4. El autor ha revisado y ejecutado todo el código en Eclipse.

Módulo Maven: [`P3-CalSo-base/`](P3-CalSo-base/) · Workflow: [`.github/workflows/p3-quality.yml`](../.github/workflows/p3-quality.yml)

---

## 2. Caja blanca

### 2.1. `DojoSearchService.containsAllRequiredWeapons`

#### Grafo de flujo

Numeración de los nodos sobre el código:

```java
public boolean containsAllRequiredWeapons(Set<Weapon> available, Set<Weapon> required) {
    if (available == null) {                       // 1
        throw new NullPointerException(...);       //   (1 → F)
    }
    if (required == null) {                        // 2
        throw new NullPointerException(...);       //   (2 → F)
    }
    for (Weapon weapon : required) {               // 3  ¿quedan armas en required?
        if (available.isEmpty()) {                 // 4
            return false;                          // 6
        }
    }
    return true;                                   // 5
}                                                  // F  fin
```

Las salidas por excepción de los nodos 1 y 2 van directamente al nodo final F.

| Desde | Condición | Hacia |
|---|---|---|
| 1 | `available == null` verdadero | F (excepción) |
| 1 | `available == null` falso | 2 |
| 2 | `required == null` verdadero | F (excepción) |
| 2 | `required == null` falso | 3 |
| 3 | quedan armas en `required` | 4 |
| 3 | no quedan armas | 5 |
| 4 | `available.isEmpty()` verdadero | 6 |
| 4 | `available.isEmpty()` falso | 3 |
| 5 | `return true` | F |
| 6 | `return false` | F |

![Grafo de flujo de containsAllRequiredWeapons](evidencias/grafos/containsAllRequiredWeapons.png)

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | 10 aristas, 7 nodos (1, 2, 3, 4, 5, 6, F): 10 − 7 + 2 | **5** |
| V(G) = nodos predicado + 1 | Nodos de decisión 1, 2, 3 y 4: 4 + 1 | **5** |
| V(G) = número de regiones | 4 regiones cerradas (entre las dos salidas por excepción; entre la excepción del nodo 2 y el camino 2→3→5→F; el bucle 3→4→3; entre 3→5→F y 3→4→6→F) + 1 exterior | **5** |

#### Caminos independientes

| Camino | Recorrido | Aristas nuevas |
|---|---|---|
| C1 | 1 → F | 1→F |
| C2 | 1 → 2 → F | 1→2, 2→F |
| C3 | 1 → 2 → 3 → 5 → F | 2→3, 3→5, 5→F |
| C4 | 1 → 2 → 3 → 4 → 6 → F | 3→4, 4→6, 6→F |
| C5 | 1 → 2 → 3 → 4 → 3 → 5 → F | 4→3 |

**Recorrido no factible:** 1 → 2 → 3 → 4 → 3 → 4 → 6 → F. Para volver del nodo 4 al 3, `available` no puede estar vacío; como el bucle no modifica `available`, en las siguientes vueltas tampoco estará vacío y el nodo 4 nunca podrá llevar al 6 después de una vuelta completa.

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| `containsAllRequiredWeapons` | C1 | `available = null`, `required = {Katana}` | `NullPointerException` con mensaje `Available weapons are required` | `testGivenNullAvailableAndRequiredKatanaWhenCheckingWeaponsThenThrowsAvailableWeaponsAreRequired` | Pasa |
| `containsAllRequiredWeapons` | C2 | `available = {Katana}`, `required = null` | `NullPointerException` con mensaje `Required weapons are required` | `testGivenAvailableKatanaAndNullRequiredWhenCheckingWeaponsThenThrowsRequiredWeaponsAreRequired` | Pasa |
| `containsAllRequiredWeapons` | C3 | `available = {Katana}`, `required = {}` | `true` (la especificación lo indica también para `required` vacío) | `testGivenAvailableKatanaAndEmptyRequiredWhenCheckingWeaponsThenReturnsTrue` | Pasa |
| `containsAllRequiredWeapons` | C4 | `available = {}`, `required = {Katana}` | `false` | `testGivenEmptyAvailableAndRequiredKatanaWhenCheckingWeaponsThenReturnsFalse` | Pasa |
| `containsAllRequiredWeapons` | C5 (a) | `available = {Katana}`, `required = {Katana}` | `true` | `testGivenAvailableKatanaAndRequiredKatanaWhenCheckingWeaponsThenReturnsTrue` | Pasa |
| `containsAllRequiredWeapons` | C5 (b) | `available = {Bo}`, `required = {Katana}` | `false`: falta la Katana. Con el código actual recorre C5 y devuelve `true`, porque solo comprueba si `available` está vacío | `testGivenAvailableBoAndRequiredKatanaWhenCheckingWeaponsThenReturnsFalse` | Falla con el código original (D1). Pasa tras corregirlo |

Fecha: este método no recibe fechas.

**Defecto previsto en el diseño:** el bucle recorre las armas requeridas pero no utiliza la variable `weapon`; comprueba en cada vuelta si `available` está vacío en lugar de comprobar si contiene el arma requerida. Cualquier dojo con al menos un arma se considera compatible. El caso C5 (b) debe revelarlo al ejecutarse.

### 2.2. `DojoSearchService.findFirstAvailableDojo`

#### Grafo de flujo

Numeración de los nodos sobre el código (las comprobaciones de `null` de los nodos 1 a 4 salen por excepción al nodo final F):

```java
if (dojos == null) throw ...;                                         // 1
if (requiredWeapons == null) throw ...;                               // 2
if (reservations == null) throw ...;                                  // 3
if (trainingDate == null) throw ...;                                  // 4
for (Dojo dojo : dojos) {                                             // 5  ¿quedan dojos?
    if (containsAllRequiredWeapons(dojo.weapons(), requiredWeapons)) { // 6
        boolean occupied = false;                                     // 7
        for (Reservation reservation : reservations) {                // 8  ¿quedan reservas?
            if (reservation.dojoId().equals(dojo.id())
                    && !reservation.trainingDate().equals(trainingDate)) { // 9
                occupied = true;                                      // 10
                break;                                                // 10
            }
        }
        if (!occupied) {                                              // 11
            return Optional.of(dojo);                                 // 12
        }
    }
}
return Optional.empty();                                              // 13
                                                                      // F  fin
```

La decisión compuesta del nodo 9 se trata como un único nodo para los caminos; sus condiciones se analizan en la cobertura de condiciones.

| Desde | Condición | Hacia |
|---|---|---|
| 1, 2, 3, 4 | parámetro `null` | F (excepción) |
| 1, 2, 3 | parámetro no nulo | 2, 3, 4 |
| 4 | `trainingDate` no nulo | 5 |
| 5 | quedan dojos / no quedan | 6 / 13 |
| 6 | el dojo tiene las armas / no las tiene | 7 / 5 |
| 7 | — | 8 |
| 8 | quedan reservas / no quedan | 9 / 11 |
| 9 | la reserva bloquea / no bloquea | 10 / 8 |
| 10 | `break` | 11 |
| 11 | `!occupied` verdadero / falso | 12 / 5 |
| 12, 13 | `return` | F |

![Grafo de flujo de findFirstAvailableDojo](evidencias/grafos/findFirstAvailableDojo.png)

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | 22 aristas, 14 nodos (1 a 13 y F): 22 − 14 + 2 | **10** |
| V(G) = nodos predicado + 1 | Nodos de decisión 1, 2, 3, 4, 5, 6, 8, 9 y 11: 9 + 1 | **10** |
| V(G) = número de regiones | 9 regiones cerradas + 1 exterior | **10** |

#### Caminos independientes

| Camino | Recorrido | Situación |
|---|---|---|
| C1 | 1 → F | `dojos` nulo |
| C2 | 1 → 2 → F | `requiredWeapons` nulo |
| C3 | 1 → 2 → 3 → F | `reservations` nulo |
| C4 | 1 → 2 → 3 → 4 → F | `trainingDate` nulo |
| C5 | 1 → 2 → 3 → 4 → 5 → 13 → F | lista de dojos vacía |
| C6 | 1 → 2 → 3 → 4 → 5 → 6 → 5 → 13 → F | un dojo sin las armas requeridas |
| C7 | 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 11 → 12 → F | dojo compatible sin reservas |
| C8 | 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 8 → 11 → 12 → F | dojo compatible con una reserva que no lo bloquea |
| C9 | 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10 → 11 → 5 → 13 → F | dojo compatible con una reserva que lo marca como ocupado |
| C10 | 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 11 → 5 → 13 → F | **no factible** |

**Camino no factible C10:** el nodo 7 asigna `occupied = false` y el único nodo que lo modifica es el 10. Si el bucle de reservas termina sin pasar por el nodo 10 (arista 8 → 11), `occupied` sigue valiendo `false`, `!occupied` es verdadero y el nodo 11 va siempre al 12. Nunca puede ir al 5.

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| `findFirstAvailableDojo` | C1 | `dojos = null`, armas {Katana}, sin reservas, 2026-11-20 | `NullPointerException` con mensaje `Dojos are required` | `testGivenNullDojosWhenFindingFirstAvailableDojoThenThrowsDojosAreRequired` | Pasa |
| `findFirstAvailableDojo` | C2 | `dojos = [D1]`, `requiredWeapons = null`, sin reservas, 2026-11-20 | `NullPointerException` con mensaje `Required weapons are required` | `testGivenNullRequiredWeaponsWhenFindingFirstAvailableDojoThenThrowsRequiredWeaponsAreRequired` | Pasa |
| `findFirstAvailableDojo` | C3 | `dojos = [D1]`, armas {Katana}, `reservations = null`, 2026-11-20 | `NullPointerException` con mensaje `Reservations are required` | `testGivenNullReservationsWhenFindingFirstAvailableDojoThenThrowsReservationsAreRequired` | Pasa |
| `findFirstAvailableDojo` | C4 | `dojos = [D1]`, armas {Katana}, sin reservas, `trainingDate = null` | `NullPointerException` con mensaje `Training date is required` | `testGivenNullTrainingDateWhenFindingFirstAvailableDojoThenThrowsTrainingDateIsRequired` | Pasa |
| `findFirstAvailableDojo` | C5 | `dojos = []`, armas {Katana}, sin reservas, 2026-11-20 | `Optional.empty()` | `testGivenEmptyDojoListWhenFindingFirstAvailableDojoOn20261120ThenReturnsEmpty` | Pasa |
| `findFirstAvailableDojo` | C6 | `dojos = [D1 sin armas]`, armas {Katana}, sin reservas, 2026-11-20 | `Optional.empty()`: el dojo no tiene la Katana | `testGivenOnlyDojoD1WithoutWeaponsAndRequiredKatanaWhenFindingFirstAvailableDojoOn20261120ThenReturnsEmpty` | Pasa |
| `findFirstAvailableDojo` | C7 | `dojos = [D1 {Katana}]`, armas {Katana}, sin reservas, 2026-11-20 | `Optional.of(D1)`: nadie lo ha reservado | `testGivenDojoD1WithKatanaAndNoReservationsWhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` | Pasa |
| `findFirstAvailableDojo` | C8 | `dojos = [D1 {Katana}]`, armas {Katana}, reserva de D2 el 2026-11-20, entrenamiento 2026-11-20 | `Optional.of(D1)`: la reserva es de otro dojo | `testGivenDojoD1AndReservationOfD2On20261120WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` | Pasa |
| `findFirstAvailableDojo` | C9 | `dojos = [D1 {Katana}]`, armas {Katana}, reserva de D1 el 2026-11-21, entrenamiento 2026-11-20 | `Optional.of(D1)`: la reserva es de otro día. Con el código actual devuelve `Optional.empty()` | `testGivenDojoD1AndReservationOfD1On20261121WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` | Falla con el código original (D2). Pasa tras corregirlo |

**Elección del dato de C6:** se usa un dojo sin ninguna arma. Un dojo con armas distintas de la Katana sería considerado compatible por el defecto previsto en `containsAllRequiredWeapons` y no recorrería C6; así se separa el recorrido del método estudiado de los errores del método auxiliar.

**Defecto previsto en el diseño:** la condición del nodo 9 compara la fecha con `!` delante, de modo que marca el dojo como ocupado cuando la reserva es de **otra** fecha. La especificación indica que solo bloquea una reserva del mismo dojo y la misma fecha exacta. C9 debe revelarlo al ejecutarse.

#### Cobertura de condiciones: decisión `&&` del bucle de reservas

`A = reservation.dojoId().equals(dojo.id())` · `B = !reservation.trainingDate().equals(trainingDate)`

Por el cortocircuito de `&&`, B solo se evalúa cuando A es verdadera.

| Caso | Reserva (entrenamiento el 2026-11-20) | A | B |
|---|---|---|---|
| C8 | D2, 2026-11-20 | falso | no se evalúa |
| C9 | D1, 2026-11-21 | verdadero | verdadero |
| CC1 | D1, 2026-11-20 (con dojos D1 y D2) | verdadero | falso |

| Condición atómica | Caso que evalúa verdadero | Caso que evalúa falso | Casos adicionales | Justificación si no es factible |
|---|---|---|---|---|
| A: `reservation.dojoId().equals(dojo.id())` | C9 | C8 | — | — |
| B: `!reservation.trainingDate().equals(trainingDate)` | C9 | CC1 | CC1: dojos `[D1 {Katana}, D2 {Katana}]`, reserva de D1 el 2026-11-20, entrenamiento el 2026-11-20. Esperado: `Optional.of(D2)`, porque D1 está ocupado y la búsqueda debe continuar. Con el código actual devuelve D1 | B nunca se evalúa con A falsa por el cortocircuito; no impide cubrir sus dos valores |

### 2.3. `ReservationDiscountService.findYoungestFighter`

#### Grafo de flujo

Numeración de los nodos sobre el código (las comprobaciones de `null` salen por excepción al nodo final F):

```java
if (fighters == null) throw ...;                                  // 1
if (referenceDate == null) throw ...;                             // 2
Fighter youngest = null;                                          // 3
for (Fighter fighter : fighters) {                                // 4  ¿quedan luchadores?
    if (fighter == null) throw ...;                               // 5
    if (youngest == null
            || fighter.birthDate().isBefore(youngest.birthDate())) { // 6
        youngest = fighter;                                       // 7
    }
}
return Optional.ofNullable(youngest);                             // 8
                                                                  // F  fin
```

El nodo 3 va separado del 4 porque cada vuelta del bucle vuelve al 4, no al 3. La decisión compuesta del nodo 6 se trata como un único nodo para los caminos.

| Desde | Condición | Hacia |
|---|---|---|
| 1 | `fighters == null` verdadero / falso | F (excepción) / 2 |
| 2 | `referenceDate == null` verdadero / falso | F (excepción) / 3 |
| 3 | — | 4 |
| 4 | quedan luchadores / no quedan | 5 / 8 |
| 5 | `fighter == null` verdadero / falso | F (excepción) / 6 |
| 6 | `A \|\| B` verdadero / falso | 7 / 4 |
| 7 | — | 4 |
| 8 | `return` | F |

![Grafo de flujo de findYoungestFighter](evidencias/grafos/findYoungestFighter.png)

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | 13 aristas, 9 nodos (1 a 8 y F): 13 − 9 + 2 | **6** |
| V(G) = nodos predicado + 1 | Nodos de decisión 1, 2, 4, 5 y 6: 5 + 1 | **6** |
| V(G) = número de regiones | 5 regiones cerradas + 1 exterior | **6** |

#### Caminos independientes

| Camino | Recorrido | Situación |
|---|---|---|
| C1 | 1 → F | lista nula |
| C2 | 1 → 2 → F | fecha de referencia nula |
| C3 | 1 → 2 → 3 → 4 → 8 → F | lista vacía |
| C4 | 1 → 2 → 3 → 4 → 5 → F | lista con un elemento nulo |
| C5 | 1 → 2 → 3 → 4 → 5 → 6 → 7 → 4 → 8 → F | un solo luchador |
| C6 | 1 → 2 → 3 → 4 → 5 → 6 → 7 → 4 → 5 → 6 → 4 → 8 → F | dos luchadores; el segundo no sustituye al guardado (arista nueva 6 → 4) |

Los seis caminos son factibles. Recorridos imposibles que conviene señalar: en la primera vuelta el nodo 6 no puede ir al 4, porque `youngest` vale `null` y A es verdadera; y a partir de la segunda vuelta A es siempre falsa, porque `youngest` ya no vuelve a valer `null`.

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| `findYoungestFighter` | C1 | `fighters = null`, referencia 2026-11-20 | `NullPointerException` con mensaje `Fighters are required` | `testGivenNullFightersWhenFindingYoungestFighterThenThrowsFightersAreRequired` | Pasa |
| `findYoungestFighter` | C2 | `[Ana (2000-05-10)]`, `referenceDate = null` | `NullPointerException` con mensaje `Reference date is required` | `testGivenAnaBorn20000510AndNullReferenceDateWhenFindingYoungestFighterThenThrowsReferenceDateIsRequired` | Pasa |
| `findYoungestFighter` | C3 | `[]`, referencia 2026-11-20 | `Optional.empty()` | `testGivenEmptyFighterListWhenFindingYoungestFighterThenReturnsEmpty` | Pasa |
| `findYoungestFighter` | C4 | `[null]`, referencia 2026-11-20 | `NullPointerException` con mensaje `Null fighter` | `testGivenListWithNullFighterWhenFindingYoungestFighterThenThrowsNullFighter` | Pasa |
| `findYoungestFighter` | C5 | `[Ana (2000-05-10)]`, referencia 2026-11-20 | `Optional.of(Ana)` | `testGivenOnlyAnaBorn20000510WhenFindingYoungestFighterThenReturnsAna` | Pasa |
| `findYoungestFighter` | C6 | `[Ana (2000-05-10), Bea (2005-03-15)]`, referencia 2026-11-20 | `Optional.of(Bea)`: fecha de nacimiento más reciente. Con el código actual devuelve Ana | `testGivenAnaBorn20000510ThenBeaBorn20050315WhenFindingYoungestFighterThenReturnsBea` | Falla con el código original (D3). Pasa tras corregirlo |

**Defecto previsto en el diseño:** la comparación usa `isBefore`, que se queda con la fecha de nacimiento más **antigua**, es decir, con el luchador de más edad. La especificación pide el de fecha de nacimiento más reciente. C6 debe revelarlo al ejecutarse.

#### Cobertura de condiciones: decisión `||` sobre el luchador más joven

`A = youngest == null` · `B = fighter.birthDate().isBefore(youngest.birthDate())`

Por el cortocircuito de `||`, B solo se evalúa cuando A es falsa, es decir, desde la segunda vuelta del bucle; así se evita llamar a `youngest.birthDate()` con `youngest` nulo.

| Caso | Vuelta | A | B |
|---|---|---|---|
| C5 `[Ana]` | 1.ª | verdadero | no se evalúa |
| C6 `[Ana, Bea]` | 2.ª (Bea) | falso | falso: 2005-03-15 no es anterior a 2000-05-10 |
| CC2 `[Bea, Ana]` | 2.ª (Ana) | falso | verdadero: 2000-05-10 es anterior a 2005-03-15 |

| Condición atómica | Caso que evalúa verdadero | Caso que evalúa falso | Casos adicionales | Justificación si no es factible |
|---|---|---|---|---|
| A: `youngest == null` | C5 | C6 | — | — |
| B: `fighter.birthDate().isBefore(youngest.birthDate())` | CC2 | C6 | CC2: `[Bea (2005-03-15), Ana (2000-05-10)]`, referencia 2026-11-20. Esperado: `Optional.of(Bea)`. Con el código actual devuelve Ana | B no puede evaluarse con A verdadera: en la primera vuelta lo impide el cortocircuito y desde la segunda A es siempre falsa. No impide cubrir sus dos valores |

**Tras corregir D3:** la condición B pasa a ser `fighter.birthDate().isAfter(youngest.birthDate())`. Las tablas de cobertura de condiciones de 2.3 describen el código original; con el código corregido, C6 evalúa B verdadera y CC2 la evalúa falsa, así que B sigue cubierta en sus dos valores con las mismas pruebas.

### 2.4. `ReservationDiscountService.calculateBestDiscount`

#### Grafo de flujo

Numeración de los nodos sobre el código. Las comprobaciones de `null` y los dos `orElseThrow()` salen por excepción al nodo final F. Cada `orElseThrow()` se modela como una decisión (¿el `Optional` está vacío?), porque la especificación indica que forman parte del código que debe analizarse aunque no todas sus ramas excepcionales sean ejecutables. La llamada a `findYoungestFighter` del nodo 3 se trata como una instrucción; sus caminos internos se analizan en el apartado 2.3.

```java
if (fighters == null) throw ...;                                   // 1
if (trainingDate == null) throw ...;                               // 2
int bestPercentage = 0;                                            // 3
Optional<Fighter> youngest = findYoungestFighter(fighters, trainingDate);  // 3
if (youngest.isPresent()) {                                        // 4
    int age = youngest.orElseThrow().ageAt(trainingDate);          // 5
    if (age < 18) {                                                // 6
        bestPercentage = 15;                                       // 7
    }
}
for (Fighter fighter : fighters) {                                 // 8  ¿quedan luchadores?
    if (fighter == null) throw ...;                                // 9
    Optional<UnemploymentPeriod> period = fighter.unemployment();
    if (period.isPresent()) {                                      // 10
        long completeYears = period.orElseThrow().completeYearsAt(trainingDate);  // 11
        int unemploymentPercentage = 0;
        if (completeYears >= 3) {                                  // 12
            unemploymentPercentage = 30;                           // 13
        } else if (completeYears > 2) {                            // 14
            unemploymentPercentage = 20;                           // 15
        } else if (completeYears >= 1) {                           // 16
            unemploymentPercentage = 10;                           // 17
        }
        if (unemploymentPercentage > bestPercentage) {             // 18
            bestPercentage = unemploymentPercentage;               // 19
        }
    }
}
return new Discount(bestPercentage);                               // 20
                                                                   // F  fin
```

| Desde | Condición | Hacia |
|---|---|---|
| 1 | `fighters == null` verdadero / falso | F (excepción) / 2 |
| 2 | `trainingDate == null` verdadero / falso | F (excepción) / 3 |
| 3 | — | 4 |
| 4 | `youngest` presente / vacío | 5 / 8 |
| 5 | `orElseThrow()` lanza / devuelve el luchador | F (excepción) / 6 |
| 6 | `age < 18` verdadero / falso | 7 / 8 |
| 7 | — | 8 |
| 8 | quedan luchadores / no quedan | 9 / 20 |
| 9 | `fighter == null` verdadero / falso | F (excepción) / 10 |
| 10 | `period` presente / vacío | 11 / 8 |
| 11 | `orElseThrow()` lanza / devuelve el período | F (excepción) / 12 |
| 12 | `completeYears >= 3` verdadero / falso | 13 / 14 |
| 13 | — | 18 |
| 14 | `completeYears > 2` verdadero / falso | 15 / 16 |
| 15 | — | 18 |
| 16 | `completeYears >= 1` verdadero / falso | 17 / 18 |
| 17 | — | 18 |
| 18 | `unemploymentPercentage > bestPercentage` verdadero / falso | 19 / 8 |
| 19 | — | 8 |
| 20 | `return` | F |

![Grafo de flujo de calculateBestDiscount](evidencias/grafos/calculateBestDiscount.png)

En el grafo, las aristas discontinuas son las que no se pueden recorrer.

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | 33 aristas, 21 nodos (1 a 20 y F): 33 − 21 + 2 | **14** |
| V(G) = nodos predicado + 1 | Nodos de decisión 1, 2, 4, 5, 6, 8, 9, 10, 11, 12, 14, 16 y 18: 13 + 1 | **14** |
| V(G) = número de regiones | 13 regiones cerradas + 1 exterior | **14** |

Si no se modelaran los dos `orElseThrow()` como decisiones, V(G) sería 12.

#### Caminos independientes

Para abreviar, **P** = 1 → 2 → 3 → 4 → 5 → 6, comienzo común de los caminos que llegan al bucle con al menos un luchador.

| Camino | Recorrido | Situación | Factible |
|---|---|---|---|
| C1 | 1 → F | lista nula | Sí |
| C2 | 1 → 2 → F | fecha de entrenamiento nula | Sí |
| C3 | 1 → 2 → 3 → 4 → 8 → 20 → F | lista vacía | Sí |
| C4 | 1 → 2 → 3 → 4 → 5 → F | `youngest.orElseThrow()` lanza | **No** |
| C5 | P → 8 → 9 → 10 → 8 → 20 → F | adulto sin paro | Sí |
| C6 | P → 7 → 8 → 9 → 10 → 8 → 20 → F | menor sin paro | Sí |
| C7 | P → 8 → 9 → F | luchador nulo dentro del bucle | **No** |
| C8 | P → 8 → 9 → 10 → 11 → F | `period.orElseThrow()` lanza | **No** |
| C9 | P → 8 → 9 → 10 → 11 → 12 → 13 → 18 → 19 → 8 → 20 → F | adulto con 3 años o más de paro | Sí |
| C10 | P → 8 → 9 → 10 → 11 → 12 → 14 → 16 → 18 → 8 → 20 → F | adulto con menos de 1 año de paro | Sí |
| C11 | P → 8 → 9 → 10 → 11 → 12 → 14 → 16 → 17 → 18 → 19 → 8 → 20 → F | adulto con 1 año de paro | Sí |
| C12 | P → 8 → 9 → 10 → 11 → 12 → 14 → 15 → 18 → 19 → 8 → 20 → F | rama del 20 % | **No** |
| C13 | P → 7 → 8 → 9 → 10 → 11 → 12 → 14 → 16 → 17 → 18 → 8 → 20 → F | menor con 1 año de paro: el 10 % no supera el 15 % | Sí |
| C14 | P → 8 → 9 → 10 → 11 → 12 → 13 → 18 → 19 → 8 → 9 → 10 → 11 → 12 → 14 → 16 → 17 → 18 → 8 → 20 → F | dos luchadores en paro: el segundo (10 %) no supera al primero (30 %) | Sí |

C13 y C14 no añaden aristas nuevas, pero son linealmente independientes de los anteriores: combinan las ramas de otra forma y completan la base de 14 caminos.

**Caminos no factibles:**

| Camino | Arista imposible | Justificación |
|---|---|---|
| C4 | 5 → F | Al nodo 5 solo se llega si `youngest.isPresent()` es verdadero. Un `Optional` con valor nunca lanza en `orElseThrow()`. |
| C7 | 9 → F | El nodo 3 llama antes a `findYoungestFighter`, que recorre toda la lista y lanza `NullPointerException("Null fighter")` si encuentra un elemento nulo. Una lista con un nulo nunca llega al nodo 9; la excepción se produce dentro del método auxiliar. |
| C8 | 11 → F | Al nodo 11 solo se llega si `period.isPresent()` es verdadero, con el mismo razonamiento que en C4. |
| C12 | 14 → 15 | `completeYears` es entero. Para llegar al nodo 14, el 12 ha descartado los valores mayores o iguales que 3, así que solo llegan 0, 1 o 2, y ninguno es mayor que 2. **Es un defecto:** la especificación asigna un 20 % de 2 a menos de 3 años. |

Fecha de entrenamiento de todos los casos: **2026-11-20**. Ana nació el 1996-01-10 (30 años), Bea el 2009-06-01 (17 años) y Carlos el 1990-02-02. Las fechas de inicio del paro son posteriores a los 16 años de cada luchador.

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| `calculateBestDiscount` | C1 | `fighters = null`, 2026-11-20 | `NullPointerException` con mensaje `Fighters are required` | `testGivenNullFightersWhenCalculatingBestDiscountThenThrowsFightersAreRequired` | Pasa |
| `calculateBestDiscount` | C2 | `[Ana]`, `trainingDate = null` | `NullPointerException` con mensaje `Training date is required` | `testGivenAnaBorn19960110AndNullTrainingDateWhenCalculatingBestDiscountThenThrowsTrainingDateIsRequired` | Pasa |
| `calculateBestDiscount` | C3 | `[]`, 2026-11-20 | `Discount(0)` | `testGivenEmptyFighterListWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0` | Pasa |
| `calculateBestDiscount` | C5 | `[Ana]` sin paro, 2026-11-20 | `Discount(0)` | `testGivenAnaAged30WithoutUnemploymentWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0` | Pasa |
| `calculateBestDiscount` | C6 | `[Bea]` sin paro, 2026-11-20 | `Discount(15)` | `testGivenBeaAged17WithoutUnemploymentWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15` | Pasa |
| `calculateBestDiscount` | C9 | `[Ana]` en paro desde 2023-11-20 (3 años justos), 2026-11-20 | `Discount(30)` | `testGivenAnaUnemployedSince20231120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount30` | Pasa |
| `calculateBestDiscount` | C10 | `[Ana]` en paro desde 2026-05-01 (0 años), 2026-11-20 | `Discount(0)` | `testGivenAnaUnemployedSince20260501WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0` | Pasa |
| `calculateBestDiscount` | C11 | `[Ana]` en paro desde 2025-11-20 (1 año justo), 2026-11-20 | `Discount(10)` | `testGivenAnaUnemployedSince20251120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount10` | Pasa |
| `calculateBestDiscount` | C13 | `[Bea]` en paro desde 2025-06-01 (1 año), 2026-11-20 | `Discount(15)`: no se suman, gana el mayor | `testGivenBeaAged17UnemployedSince20250601WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15` | Pasa |
| `calculateBestDiscount` | C14 | `[Ana` en paro desde 2023-11-20, `Carlos` en paro desde 2025-11-20`]`, 2026-11-20 | `Discount(30)`: el mayor de 30 y 10 | `testGivenAnaUnemployed3YearsThenCarlosUnemployed1YearWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount30` | Pasa |
| `calculateBestDiscount` | Adicional: 2 años de paro (dato de C12) | `[Ana]` en paro desde 2024-11-20 (2 años justos), 2026-11-20 | `Discount(20)`. Con el código actual da `Discount(10)` | `testGivenAnaUnemployedSince20241120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount20` | Falla con el código original (D4). Pasa tras corregirlo |
| `calculateBestDiscount` | Adicional: adulto y menor | `[Ana, Bea]` sin paro, 2026-11-20 | `Discount(15)`: la más joven tiene 17 años. Con el código actual da `Discount(0)`, porque `findYoungestFighter` devuelve a Ana | `testGivenAnaAged30AndBeaAged17WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15` | Falla con el código original (D3). Pasa tras corregirlo |

**Defectos previstos en el diseño:**

- La condición `completeYears > 2` hace inalcanzable el 20 %: un luchador con 2 años completos de paro recibe un 10 %. Lo revela la prueba adicional de 2 años.
- El descuento juvenil depende de `findYoungestFighter`, que devuelve al luchador de más edad (apartado 2.3). Un equipo con un adulto y un menor pierde el 15 %. No es un defecto de este método: se corrige en `findYoungestFighter`, y al hacerlo esta prueba debe pasar también a verde (regresión).

### 2.5. Trazabilidad

#### Camino → prueba

Pruebas en [`DojoSearchServiceTest`](P3-CalSo-base/src/test/java/es/um/calso/dojoreservation/domain/service/DojoSearchServiceTest.java) (apartados 2.1 y 2.2) y [`ReservationDiscountServiceTest`](P3-CalSo-base/src/test/java/es/um/calso/dojoreservation/domain/service/ReservationDiscountServiceTest.java) (apartados 2.3 y 2.4).

| Método | Camino | Prueba JUnit |
|---|---|---|
| `containsAllRequiredWeapons` | C1 | `testGivenNullAvailableAndRequiredKatanaWhenCheckingWeaponsThenThrowsAvailableWeaponsAreRequired` |
| `containsAllRequiredWeapons` | C2 | `testGivenAvailableKatanaAndNullRequiredWhenCheckingWeaponsThenThrowsRequiredWeaponsAreRequired` |
| `containsAllRequiredWeapons` | C3 | `testGivenAvailableKatanaAndEmptyRequiredWhenCheckingWeaponsThenReturnsTrue` |
| `containsAllRequiredWeapons` | C4 | `testGivenEmptyAvailableAndRequiredKatanaWhenCheckingWeaponsThenReturnsFalse` |
| `containsAllRequiredWeapons` | C5 (a) | `testGivenAvailableKatanaAndRequiredKatanaWhenCheckingWeaponsThenReturnsTrue` |
| `containsAllRequiredWeapons` | C5 (b) | `testGivenAvailableBoAndRequiredKatanaWhenCheckingWeaponsThenReturnsFalse` |
| `findFirstAvailableDojo` | C1 | `testGivenNullDojosWhenFindingFirstAvailableDojoThenThrowsDojosAreRequired` |
| `findFirstAvailableDojo` | C2 | `testGivenNullRequiredWeaponsWhenFindingFirstAvailableDojoThenThrowsRequiredWeaponsAreRequired` |
| `findFirstAvailableDojo` | C3 | `testGivenNullReservationsWhenFindingFirstAvailableDojoThenThrowsReservationsAreRequired` |
| `findFirstAvailableDojo` | C4 | `testGivenNullTrainingDateWhenFindingFirstAvailableDojoThenThrowsTrainingDateIsRequired` |
| `findFirstAvailableDojo` | C5 | `testGivenEmptyDojoListWhenFindingFirstAvailableDojoOn20261120ThenReturnsEmpty` |
| `findFirstAvailableDojo` | C6 | `testGivenOnlyDojoD1WithoutWeaponsAndRequiredKatanaWhenFindingFirstAvailableDojoOn20261120ThenReturnsEmpty` |
| `findFirstAvailableDojo` | C7 | `testGivenDojoD1WithKatanaAndNoReservationsWhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` |
| `findFirstAvailableDojo` | C8 | `testGivenDojoD1AndReservationOfD2On20261120WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` |
| `findFirstAvailableDojo` | C9 | `testGivenDojoD1AndReservationOfD1On20261121WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` |
| `findFirstAvailableDojo` | CC1 (cobertura de condiciones) | `testGivenDojosD1AndD2AndReservationOfD1On20261120WhenFindingFirstAvailableDojoOn20261120ThenReturnsD2` |
| `findYoungestFighter` | C1 | `testGivenNullFightersWhenFindingYoungestFighterThenThrowsFightersAreRequired` |
| `findYoungestFighter` | C2 | `testGivenAnaBorn20000510AndNullReferenceDateWhenFindingYoungestFighterThenThrowsReferenceDateIsRequired` |
| `findYoungestFighter` | C3 | `testGivenEmptyFighterListWhenFindingYoungestFighterThenReturnsEmpty` |
| `findYoungestFighter` | C4 | `testGivenListWithNullFighterWhenFindingYoungestFighterThenThrowsNullFighter` |
| `findYoungestFighter` | C5 | `testGivenOnlyAnaBorn20000510WhenFindingYoungestFighterThenReturnsAna` |
| `findYoungestFighter` | C6 | `testGivenAnaBorn20000510ThenBeaBorn20050315WhenFindingYoungestFighterThenReturnsBea` |
| `findYoungestFighter` | CC2 (cobertura de condiciones) | `testGivenBeaBorn20050315ThenAnaBorn20000510WhenFindingYoungestFighterThenReturnsBea` |
| `calculateBestDiscount` | C1 | `testGivenNullFightersWhenCalculatingBestDiscountThenThrowsFightersAreRequired` |
| `calculateBestDiscount` | C2 | `testGivenAnaBorn19960110AndNullTrainingDateWhenCalculatingBestDiscountThenThrowsTrainingDateIsRequired` |
| `calculateBestDiscount` | C3 | `testGivenEmptyFighterListWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0` |
| `calculateBestDiscount` | C5 | `testGivenAnaAged30WithoutUnemploymentWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0` |
| `calculateBestDiscount` | C6 | `testGivenBeaAged17WithoutUnemploymentWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15` |
| `calculateBestDiscount` | C9 | `testGivenAnaUnemployedSince20231120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount30` |
| `calculateBestDiscount` | C10 | `testGivenAnaUnemployedSince20260501WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount0` |
| `calculateBestDiscount` | C11 | `testGivenAnaUnemployedSince20251120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount10` |
| `calculateBestDiscount` | C13 | `testGivenBeaAged17UnemployedSince20250601WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15` |
| `calculateBestDiscount` | C14 | `testGivenAnaUnemployed3YearsThenCarlosUnemployed1YearWhenCalculatingBestDiscountOn20261120ThenReturnsDiscount30` |
| `calculateBestDiscount` | Adicional: 2 años de paro (dato de C12) | `testGivenAnaUnemployedSince20241120WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount20` |
| `calculateBestDiscount` | Adicional: adulto y menor | `testGivenAnaAged30AndBeaAged17WhenCalculatingBestDiscountOn20261120ThenReturnsDiscount15` |

#### Condición atómica / valor → prueba

| Decisión | Condición atómica | Valor | Prueba JUnit |
|---|---|---|---|
| `findFirstAvailableDojo`, bucle de reservas (`&&`) | A: `reservation.dojoId().equals(dojo.id())` | verdadero | `testGivenDojoD1AndReservationOfD1On20261121WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` (C9) |
| | A | falso | `testGivenDojoD1AndReservationOfD2On20261120WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` (C8) |
| | B: `!reservation.trainingDate().equals(trainingDate)` | verdadero | `testGivenDojoD1AndReservationOfD1On20261121WhenFindingFirstAvailableDojoOn20261120ThenReturnsD1` (C9) |
| | B | falso | `testGivenDojosD1AndD2AndReservationOfD1On20261120WhenFindingFirstAvailableDojoOn20261120ThenReturnsD2` (CC1) |
| `findYoungestFighter`, luchador más joven (`\|\|`) | A: `youngest == null` | verdadero | `testGivenOnlyAnaBorn20000510WhenFindingYoungestFighterThenReturnsAna` (C5) |
| | A | falso | `testGivenAnaBorn20000510ThenBeaBorn20050315WhenFindingYoungestFighterThenReturnsBea` (C6, segunda vuelta) |
| | B: `fighter.birthDate().isBefore(youngest.birthDate())` | verdadero | `testGivenBeaBorn20050315ThenAnaBorn20000510WhenFindingYoungestFighterThenReturnsBea` (CC2) |
| | B | falso | `testGivenAnaBorn20000510ThenBeaBorn20050315WhenFindingYoungestFighterThenReturnsBea` (C6) |

---

## 3. Caja negra

### 3.1. Búsqueda de dojos (`/dojos`)

#### Clases de equivalencia

| Código de clase | Variable | Condición de pertenencia | Válida / inválida | Comportamiento exigido |
|---|---|---|---|---|
| | | | | |

#### Valores límite

| Variable | Valor del límite | Valor elegido | Expectativas |
|---|---|---|---|
| | | | |

#### Casos de prueba

| ID del caso | Clases cubiertas | Equipo/dojo/fecha o URL concreta | Estado inicial | Resultado único esperado | Nombre prueba |
|---|---|---|---|---|---|
| | | | | | |

### 3.2. Coste y bonificaciones (`/coste`)

#### Clases de equivalencia

| Código de clase | Variable | Condición de pertenencia | Válida / inválida | Comportamiento exigido |
|---|---|---|---|---|
| | | | | |

#### Valores límite

| Variable | Valor del límite | Valor elegido | Expectativas |
|---|---|---|---|
| | | | |

#### Casos de prueba

| ID del caso | Clases cubiertas | Equipo/dojo/fecha o URL concreta | Estado inicial | Resultado único esperado | Nombre prueba |
|---|---|---|---|---|---|
| | | | | | |

---

## 4. Defectos y correcciones

| ID | Síntoma | Prueba que lo detectó | Causa | Corrección | Regresión posterior | Commit |
|---|---|---|---|---|---|---|
| D1 | `containsAllRequiredWeapons({Bo}, {Katana})` devuelve `true` aunque falta la Katana | `testGivenAvailableBoAndRequiredKatanaWhenCheckingWeaponsThenReturnsFalse` (C5 b) | El bucle comprueba `available.isEmpty()` en lugar de si contiene cada arma requerida | `if (available.isEmpty())` → `if (!available.contains(weapon))` | Pasan 33 de 39 pruebas del paquete `domain.service`; fallan 6, todas de defectos aún sin corregir (D2, D3, D4). Ninguna prueba que pasaba ha dejado de pasar | «P3 - D1 - containsAllRequiredWeapons comprueba cada arma requerida» |
| D2 | Un dojo con una reserva otro día se considera ocupado, y uno reservado ese mismo día se considera libre | `testGivenDojoD1AndReservationOfD1On20261121When…ThenReturnsD1` (C9) y `testGivenDojosD1AndD2AndReservationOfD1On20261120When…ThenReturnsD2` (CC1) | La condición de ocupación tiene la comparación de fechas negada (`!`) | `&& !reservation.trainingDate().equals(trainingDate)` → `&& reservation.trainingDate().equals(trainingDate)` | Pasan 35 de 39 pruebas del paquete `domain.service`; fallan 4, todas de defectos aún sin corregir (D3, D4). Ninguna prueba que pasaba ha dejado de pasar | «P3 - D2 - findFirstAvailableDojo descarta el dojo reservado ese mismo día» |
| D3 | `findYoungestFighter` devuelve al luchador de más edad; el equipo con un adulto y un menor pierde el 15 % | `testGivenAnaBorn20000510ThenBeaBorn20050315When…ThenReturnsBea` (C6), `testGivenBeaBorn20050315ThenAnaBorn20000510When…ThenReturnsBea` (CC2) y `testGivenAnaAged30AndBeaAged17When…ThenReturnsDiscount15` (adicional de 2.4) | Se sustituye al candidato cuando nació **antes** (`isBefore`), es decir, cuando es mayor | `isBefore(youngest.birthDate())` → `isAfter(youngest.birthDate())` | Pasan 38 de 39 pruebas del paquete `domain.service`; fallan 1, todas de defectos aún sin corregir (D4). Ninguna prueba que pasaba ha dejado de pasar | «P3 - D3 - findYoungestFighter devuelve el luchador más joven» |
| D4 | Un luchador con 2 años completos de paro recibe un 10 % en vez de un 20 % | `testGivenAnaUnemployedSince20241120When…ThenReturnsDiscount20` (adicional de 2.4) | `completeYears > 2` nunca es cierto tras descartar los valores ≥ 3 (camino C12 no factible) | `else if (completeYears > 2)` → `else if (completeYears >= 2)` | Pasan 39 de 39 pruebas del paquete `domain.service`; no falla ninguna. Ninguna prueba que pasaba ha dejado de pasar | «P3 - D4 - calculateBestDiscount aplica el 20 % con 2 años de paro» |

---

## 5. Herramientas de calidad

| Herramienta | Resultado | Interpretación | Evidencia |
|---|---|---|---|
| JUnit 5 | | | |
| JaCoCo (líneas y ramas) | | | |
| PIT (servicios de dominio) | | | |
| GitHub Actions | | | |

---

## 6. Ejecución

| Comando | Para qué | Resultado |
|---|---|---|
| `mvn clean verify` | Pruebas unitarias y JaCoCo | |
| `mvn -Pe2e clean verify` | Pruebas Selenium | |
| `mvn -Pmutation test pitest:mutationCoverage` | PIT | |

**Entorno:**

| Componente | Versión |
|---|---|
| Selenium Java | 4.51.0 (la declarada en `pom.xml`) |
| Google Chrome | 156 |
| Eclipse IDE | 2026-09 (4.41.0) |
| Java | 25.0.4, el JRE que incluye Eclipse (`org.eclipse.justj.openjdk.hotspot.jre.full`). El proyecto compila para Java 17 (`maven.compiler.release=17`) |
| Maven | El que incluye Eclipse (m2e) |

Java y Maven no están instalados aparte en el sistema (`java -version` no se reconoce en la terminal). Por eso los comandos `mvn` se ejecutan desde Eclipse con *Run As > Maven build…*, indicando los mismos *Goals* y *Profiles*. La GitHub Action usa JDK 21.

**Incidencias de compatibilidad:**

| Fecha | Síntoma | Causa | Decisión |
|---|---|---|---|
| 2026-11-02 | Advertencia al ejecutar Selenium: `Unable to find an exact match for CDP version 156, returning the closest version; found: 155` | Chrome 156 es más reciente que la última versión del protocolo CDP (Chrome DevTools Protocol) que incluye Selenium 4.51.0, que llega a la 155. Selenium usa la versión más próxima. | No se modifica nada. Es una advertencia, no un fallo: las pruebas no usan funciones de DevTools y la guía `docs/ENTORNO_Y_VERSIONES.md` indica revisar la compatibilidad antes de añadir artefactos `selenium-devtools`. No se cambia la versión de Selenium del `pom.xml`. |

---

## 7. Vídeo de síntesis

**Enlace (oculto/no listado):** *pendiente.*

| Parte | Resultado concreto que se explica |
|---|---|
| | |
