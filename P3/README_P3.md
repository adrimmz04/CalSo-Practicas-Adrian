# Práctica 3 — Pruebas de caja blanca y de caja negra

**Caso de estudio:** Dojo Reservation · [Enunciado](docs/ENUNCIADO.md) · [Especificación funcional](docs/ESPECIFICACION.md) · [Guía de pruebas](docs/GUIA_PRUEBAS.md)

---

## 1. Identificación

| Integrante | Responsabilidades | Rama |
|---|---|---|
| Adrián Martínez Zamora | Práctica individual: caja blanca de los cuatro métodos de dominio, cobertura de las dos condiciones seleccionadas, caja negra de las dos pantallas (`/dojos` y `/coste`), análisis de calidad y vídeo | `main` |

Al tratarse de un grupo de un único integrante, todo el trabajo se ha realizado en la rama `main`.

Módulo Maven: [`P3-CalSo-base/`](P3-CalSo-base/) · Workflow: [`.github/workflows/p3-quality.yml`](../.github/workflows/p3-quality.yml)

---

## 2. Caja blanca

### 2.1. `DojoSearchService.containsAllRequiredWeapons`

#### Grafo de flujo

*Pendiente.*

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | | |
| V(G) = nodos predicado + 1 | | |
| V(G) = número de regiones | | |

#### Caminos independientes

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| | | | | | |

### 2.2. `DojoSearchService.findFirstAvailableDojo`

#### Grafo de flujo

*Pendiente.*

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | | |
| V(G) = nodos predicado + 1 | | |
| V(G) = número de regiones | | |

#### Caminos independientes

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| | | | | | |

#### Cobertura de condiciones: decisión `&&` del bucle de reservas

| Condición atómica | Caso que evalúa verdadero | Caso que evalúa falso | Casos adicionales | Justificación si no es factible |
|---|---|---|---|---|
| | | | | |

### 2.3. `ReservationDiscountService.findYoungestFighter`

#### Grafo de flujo

*Pendiente.*

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | | |
| V(G) = nodos predicado + 1 | | |
| V(G) = número de regiones | | |

#### Caminos independientes

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| | | | | | |

#### Cobertura de condiciones: decisión `||` sobre el luchador más joven

| Condición atómica | Caso que evalúa verdadero | Caso que evalúa falso | Casos adicionales | Justificación si no es factible |
|---|---|---|---|---|
| | | | | |

### 2.4. `ReservationDiscountService.calculateBestDiscount`

#### Grafo de flujo

*Pendiente.*

#### Complejidad ciclomática

| Fórmula | Cálculo | Resultado |
|---|---|---|
| V(G) = aristas − nodos + 2 | | |
| V(G) = nodos predicado + 1 | | |
| V(G) = número de regiones | | |

#### Caminos independientes

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|
| | | | | | |

### 2.5. Trazabilidad

#### Camino → prueba

| Método | Camino | Prueba JUnit |
|---|---|---|
| | | |

#### Condición atómica / valor → prueba

| Decisión | Condición atómica | Valor | Prueba JUnit |
|---|---|---|---|
| | | | |

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
| | | | | | | |

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

**Entorno:** *pendiente (versiones de Java, Maven, Eclipse y Chrome).*

**Problemas de compatibilidad resueltos:** *pendiente.*

---

## 7. Vídeo de síntesis

**Enlace (oculto/no listado):** *pendiente.*

| Parte | Resultado concreto que se explica |
|---|---|
| | |
