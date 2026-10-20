# Práctica 1 — Revisiones estáticas de código con SonarQube for Eclipse

## 1. Autor

| Miembro | Nombre y apellidos |
|---|---|
| Alumno | Adrián Martínez Zamora |

> Práctica realizada de forma individual.

**Nombre del proyecto Eclipse:** `P1_AMZ`

---

## 2. Análisis inicial

Antes de realizar ninguna modificación sobre el código proporcionado se ha ejecutado el análisis estático del proyecto completo utilizando **SonarQube for Eclipse** en modo standalone y con su configuración de reglas por defecto.

Antes del análisis se ha adaptado únicamente la configuración del proyecto Eclipse (sin tocar el código Java): se ha renombrado a `P1_AMZ`, se ha eliminado la naturaleza y el *builder* de Maven de `.project`, se ha sustituido el contenedor de Maven de `.classpath` por el JRE 17 y se ha fijado `bin/` como carpeta de salida, tal como exige el enunciado.

### Captura inicial

![Análisis inicial de SonarQube for Eclipse](imagenes/sonar_inicial.png)

---

## 3. Disconformidades detectadas

En el análisis inicial se han identificado las siguientes disconformidades:

| Nº | Regla Sonar | Archivo | Línea | Disconformidad |
|---:|---|---|---:|---|
| 1 | `java:SXXXX` | `Clase.java` | XX | Descripción mostrada por Sonar |

---

## 4. Soluciones adoptadas

### Disconformidad 1 — `java:SXXXX`

**Localización:** `src/main/java/.../Clase.java`, línea XX  
**Responsable:** Adrián Martínez Zamora  
**Commit:** `abcdef1`

**Problema detectado**

Descripción breve del problema indicado por SonarQube for Eclipse.

**Solución adoptada**

Descripción de la modificación realizada para resolver la disconformidad.

---

## 5. Resumen de las correcciones

| Nº | Regla Sonar | Responsable | Commit | Resultado |
|---:|---|---|---|---|
| 1 | `java:SXXXX` | Adrián Martínez Zamora | `abcdef1` | Resuelta |

---

## 6. Análisis final

Una vez realizadas todas las modificaciones se ha vuelto a ejecutar el análisis del proyecto completo con **SonarQube for Eclipse**.

### Captura final

![Análisis final de SonarQube for Eclipse](imagenes/sonar_final.png)

La captura final permite comprobar que se está analizando el mismo proyecto utilizado en la captura inicial y que ya no quedan disconformidades pendientes.

---

## 7. Proyecto final

La versión final del proyecto Eclipse se encuentra en:

```text
P1/proyecto/P1_AMZ/
```

El proyecto incluido en esta carpeta contiene las modificaciones correspondientes a las soluciones documentadas anteriormente y coincide con la versión sobre la que se ha realizado la captura final.

---

## 8. Comprobación de la entrega

- [ ] El nombre del proyecto sigue el formato establecido: `P1_AMZ`.
- [ ] Se identifica al autor de la práctica.
- [ ] Se incluye la captura del análisis inicial.
- [ ] Se han documentado todas las disconformidades inicialmente detectadas.
- [ ] Cada solución está asociada a un commit identificable en `main`.
- [ ] Se identifica el responsable de cada corrección.
- [ ] Se incluye la captura del análisis final.
- [ ] La captura final permite comprobar que no quedan disconformidades.
- [ ] Se ha incorporado el proyecto Eclipse final dentro de `P1/proyecto/`.
- [ ] El proyecto final corresponde al código analizado en la captura final.
