package com.calso.robot.domain.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CleaningProductTest {

    @Test
    void testGivenCleaningProductEnumWhenListingValuesThenReturnsWaterSoapWaxAndGlassCleanerInOrder() {
        CleaningProduct[] expected = {
            CleaningProduct.WATER, CleaningProduct.SOAP, CleaningProduct.WAX, CleaningProduct.GLASS_CLEANER
        };

        assertArrayEquals(expected, CleaningProduct.values());
    }

    @Test
    void testGivenWaterWhenConvertingToStringThenReturnsAgua() {
        assertEquals("agua", CleaningProduct.WATER.toString());
    }

    @Test
    void testGivenSoapWhenConvertingToStringThenReturnsJabonWithAccent() {
        assertEquals("jabón", CleaningProduct.SOAP.toString());
    }

    @Test
    void testGivenWaxWhenConvertingToStringThenReturnsCera() {
        assertEquals("cera", CleaningProduct.WAX.toString());
    }

    @Test
    void testGivenGlassCleanerWhenConvertingToStringThenReturnsLimpiacristales() {
        assertEquals("limpiacristales", CleaningProduct.GLASS_CLEANER.toString());
    }
}
