/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.model;

import java.util.Random;

/**
 * Clase que implementa la interfaz Runnable para gestionar la verificacion y adicion 
 * de numeros primos a una lista de forma concurrente.
 * <p>
 * Esta clase representa un hilo ligero con su propio flujo de control
 * que genera numeros aleatorios y verifica si son primos para agregarlos
 * a una lista sincronizada de numeros primos.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.0
 * @since 2023-07-01
 */
public class PrimesThread implements Runnable {
    
    /** La lista de numeros primos a la que se agregaran los numeros */
    private final PrimesList primesList;
    
    /** Generador de numeros aleatorios */
    private final Random random;
    
    /** Limite superior para la generacion de numeros aleatorios */
    private final int upperBound;
    
    /** Cantidad de numeros primos que se intentaran generar */
    private final int primesToGenerate;
    
    /** Indicador de si el hilo debe continuar ejecutandose */
    private volatile boolean running;
    
    /**
     * Crea un nuevo hilo para generar y agregar numeros primos.
     * 
     * @param primesList La lista de numeros primos a la que se agregaran los numeros
     * @param upperBound Limite superior para la generacion de numeros aleatorios
     * @param primesToGenerate Cantidad de numeros primos que se intentaran generar
     */
    public PrimesThread(PrimesList primesList, int upperBound, int primesToGenerate) {
        this.primesList = primesList;
        this.random = new Random();
        this.upperBound = upperBound;
        this.primesToGenerate = primesToGenerate;
        this.running = true;
    }
    
    /**
     * Detiene la ejecucion del hilo de forma segura.
     */
    public void stop() {
        this.running = false;
    }
    
    /**
     * Ejecuta la tarea principal del hilo.
     * <p>
     * Genera numeros aleatorios y verifica si son primos. Si lo son,
     * los agrega a la lista de numeros primos de forma sincronizada.
     * </p>
     */
    @Override
    public void run() {
        int primesFound = 0;
        
        try {
            while (running && primesFound < primesToGenerate) {
                // Genera un numero aleatorio dentro del rango especificado
                int randomNumber = random.nextInt(upperBound) + 2; // +2 para evitar 0 y 1 que no son primos
                
                // Verifica si el numero es primo utilizando el metodo de PrimesList
                if (PrimesList.isPrime(randomNumber)) {
                    // Intenta agregar el numero a la lista de forma sincronizada
                    synchronized (primesList) {
                        // Verifica si el numero ya existe en la lista
                        if (!primesList.contains(randomNumber)) {
                            primesList.add(randomNumber);
                            primesFound++;
                            
                            // Notifica a otros hilos que puedan estar esperando
                            primesList.notify();
                            
                            System.out.println("Hilo " + Thread.currentThread().getName() + 
                                    " agrego el numero primo: " + randomNumber);
                        }
                    }
                }
                
                // Pausa breve para permitir que otros hilos se ejecuten
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.err.println("Hilo interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            // Este error no debería ocurrir ya que verificamos si es primo antes de agregar
            System.err.println("Error al agregar numero primo: " + e.getMessage());
        }
        
        System.out.println("Hilo " + Thread.currentThread().getName() + 
                " finalizado. Numeros primos agregados: " + primesFound);
    }
}