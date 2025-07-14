/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Una ArrayList especializada que solo almacena numeros primos.
 * <p>
 * Esta clase extiende ArrayList con funcionalidad para validar que
 * solo numeros primos sean agregados a la coleccion. Tambien proporciona
 * metodos de utilidad especificos para operaciones con numeros primos.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.1
 * @since 2023-07-01
 */
public class PrimesList extends ArrayList<Integer> {
    
    /** Cache para almacenar resultados de verificacion de numeros primos */
    private static final Map<Integer, Boolean> primeCache = new ConcurrentHashMap<>();
    
    /**
    * Crea una nueva lista vacia de numeros primos.
    */
    public PrimesList() {
    super();
    }
    
    /**
    * Agrega un numero a la lista solo si es primo.
    * <p>
    * Este metodo sobrescribe el metodo estandar de add para asegurar
    * que solo numeros primos puedan ser agregados a la coleccion.
    * </p>
    * 
    * @param number El numero a agregar
    * @return true si el numero fue agregado (es primo)
    * @throws IllegalArgumentException si el numero no es primo
    */
    @Override
    public boolean add(Integer number) {
    if (isPrime(number)) {
    return super.add(number);
    }
    throw new IllegalArgumentException("El numero " + number + " no es primo");
    }
    
    /**
    * Elimina un numero de la lista.
    * <p>
    * Este metodo sobrescribe el metodo estandar de remove para mantener
    * la integridad de la lista de primos.
    * </p>
    * 
    * @param o El objeto a eliminar
    * @return true si el objeto fue eliminado
    */
    @Override
    public boolean remove(Object o) {
    if (o instanceof Integer) {
    // No necesitamos verificar si es primo aquí, ya que solo 
    // los números primos deberían estar en la lista
    return super.remove(o);
    }
    return false;
    }
    
    /**
    * Verifica si un numero es primo.
    * <p>
    * Un numero primo es un numero natural mayor que 1 que no puede ser
    * formado multiplicando dos numeros naturales menores.
    * Utiliza un cache para mejorar el rendimiento en verificaciones repetidas.
    * </p>
    * 
    * @param number El numero a verificar
    * @return true si el numero es primo, false en caso contrario
    */
    public static boolean isPrime(int number) {
    // Verificar cache primero
    Boolean cached = primeCache.get(number);
    if (cached != null) {
        return cached;
    }
    
    // Algoritmo de verificacion de primos
    boolean result;
    
    if (number <= 1) {
        result = false;
    } else if (number <= 3) {
        result = true;
    } else if (number % 2 == 0 || number % 3 == 0) {
        result = false;
    } else {
        result = true;
        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                result = false;
                break;
            }
        }
    }
    
    // Guardar resultado en cache
    primeCache.put(number, result);
    return result;
    }
    
    /**
    * Obtiene la cantidad de numeros primos en la lista.
    * 
    * @return La cantidad de numeros primos en la lista
    */
    public int getPrimesCount() {
    return this.size();
    }
    
    /**
    * Limpia la cache de verificacion de numeros primos.
    * Util para liberar memoria en sistemas con restricciones.
    */
    public static void clearCache() {
        primeCache.clear();
    }
    
    /**
    * Obtiene el tamaño actual de la cache de numeros primos.
    * 
    * @return Cantidad de entradas en la cache
    */
    public static int getCacheSize() {
        return primeCache.size();
    }
}