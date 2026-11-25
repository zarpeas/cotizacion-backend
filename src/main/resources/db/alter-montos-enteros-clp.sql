-- Hibernate ddl-auto=update no cambia la precisión de columnas existentes.
-- El peso chileno no tiene centavos: costo y precio se guardan en pesos enteros.
-- Ejecutar una vez para ajustar la escala de las columnas actuales.
ALTER TABLE cotizacion_detalle
    MODIFY costo_fabricacion DECIMAL(12, 0),
    MODIFY precio_venta_sugerido DECIMAL(12, 0);
