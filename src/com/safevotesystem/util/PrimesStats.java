/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para registrar y mostrar estadisticas del sistema.
 * <p>
 * Esta clase proporciona metodos para registrar y analizar el rendimiento
 * del sistema, incluyendo conteo de numeros primos, tiempos de procesamiento
 * y frecuencia de uso.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.1
 * @since 2023-07-15
 */
public class PrimesStats {
    
    /** Contador atomico de numeros primos generados */
    private static final AtomicInteger primesGenerated = new AtomicInteger(0);
    
    /** Contador atomico de numeros primos usados para encriptacion */
    private static final AtomicInteger primesUsedForEncryption = new AtomicInteger(0);
    
    /** Acumulador atomico del tiempo total de procesamiento en milisegundos */
    private static final AtomicLong totalProcessingTime = new AtomicLong(0);
    
    /** Mapa concurrente para registrar la frecuencia de uso de cada numero primo */
    private static final ConcurrentHashMap<Integer, Integer> primeFrequency = new ConcurrentHashMap<>();
    
    /**
    * Registra la generacion de un numero primo.
    * 
    * @param prime El numero primo generado
    */
    public static void recordPrimeGenerated(int prime) {
        primesGenerated.incrementAndGet();
        primeFrequency.compute(prime, (k, v) -> (v == null) ? 1 : v + 1);
    }
    
    /**
    * Registra el uso de un numero primo para encriptacion.
    * 
    * @param prime El numero primo utilizado
    */
    public static void recordPrimeUsed(int prime) {
        primesUsedForEncryption.incrementAndGet();
    }
    
    /**
    * Registra el tiempo de procesamiento de una operacion.
    * 
    * @param timeMs Tiempo de procesamiento en milisegundos
    */
    public static void recordProcessingTime(long timeMs) {
        totalProcessingTime.addAndGet(timeMs);
    }
    
    /**
    * Muestra las estadisticas del sistema en la consola.
    */
    public static void printStats() {
        System.out.println("\n==== ESTADISTICAS DEL SISTEMA ====");
        System.out.println("Numeros primos generados: " + primesGenerated.get());
        System.out.println("Numeros primos usados para encriptacion: " + primesUsedForEncryption.get());
        System.out.println("Tiempo total de procesamiento: " + totalProcessingTime.get() + " ms");
        
        System.out.println("\nTop 5 numeros primos mas frecuentes:");
        primeFrequency.entrySet().stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(e -> System.out.println("Primo: " + e.getKey() + " - Frecuencia: " + e.getValue()));
    }
    
    /**
    * Reinicia todas las estadisticas del sistema.
    */
    public static void resetStats() {
        primesGenerated.set(0);
        primesUsedForEncryption.set(0);
        totalProcessingTime.set(0);
        primeFrequency.clear();
    }
}