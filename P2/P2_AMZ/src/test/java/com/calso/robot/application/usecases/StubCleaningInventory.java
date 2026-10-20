package com.calso.robot.application.usecases;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import com.calso.robot.domain.model.CleaningProduct;
import com.calso.robot.domain.repositories.ICleaningInventory;

/**
 * Suplantador (stub) del inventario de productos de limpieza.
 *
 * <p>Sustituye al inventario real, que todavía no está desarrollado, para poder
 * probar {@link ExecuteCleaningProgramUseCase} a través de la interfaz
 * {@link ICleaningInventory}. Permite fijar las existencias iniciales de cada
 * prueba y consultar después las existencias finales.</p>
 *
 * <p>La implementación se mantiene exclusivamente bajo {@code src/test/java}.</p>
 */
public final class StubCleaningInventory implements ICleaningInventory {

    private final Map<CleaningProduct, Integer> stock = new EnumMap<>(CleaningProduct.class);

    /**
     * Crea un inventario con una copia propia de las existencias indicadas.
     *
     * @param initialStock existencias iniciales; los productos ausentes valen 0
     * @throws NullPointerException     si {@code initialStock} es nulo
     * @throws IllegalArgumentException si contiene productos o cantidades nulos,
     *                                  o cantidades negativas
     */
    public StubCleaningInventory(Map<CleaningProduct, Integer> initialStock) {
        Objects.requireNonNull(initialStock, "initialStock");
        for (Map.Entry<CleaningProduct, Integer> entry : initialStock.entrySet()) {
            CleaningProduct product = entry.getKey();
            Integer quantity = entry.getValue();
            if (product == null || quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Invalid initial stock: " + product + "=" + quantity);
            }
            stock.put(product, quantity);
        }
    }

    @Override
    public int available(CleaningProduct product) {
        return stock.getOrDefault(product, 0);
    }

    @Override
    public void consume(CleaningProduct product, int quantity) {
        int available = available(product);
        if (quantity < 0 || quantity > available) {
            throw new IllegalArgumentException(
                "Cannot consume " + quantity + " units of " + product + " (available: " + available + ")");
        }
        stock.put(product, available - quantity);
    }
}
