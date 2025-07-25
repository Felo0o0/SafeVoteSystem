/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementacion de una cola sincronizada para el manejo seguro de numeros primos
 * en entornos concurrentes.
 * <p>
 * Esta clase proporciona una estructura de datos tipo cola (FIFO) que permite
 * a multiples hilos agregar y consumir numeros primos de forma segura y sincronizada.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.1
 * @since 2023-07-01
 */
public class PrimesQueue {
    
    /** Cola concurrente para almacenar los numeros primos */
    private final ConcurrentLinkedQueue<Integer> queue;
    
    /** Lock para sincronizar operaciones */
    private final ReentrantLock lock;
    
    /** Condicion para esperar cuando la cola esta vacia */
    private final Condition notEmpty;
    
    /** Capacidad maxima de la cola (0 significa sin limite) */
    private final int capacity;
    
    /** Contador de operaciones realizadas */
    private int operationsCount;
    
    /**
    * Crea una nueva cola de numeros primos sin limite de capacidad.
    */
    public PrimesQueue() {
    this(0); // Sin limite de capacidad
    }
    
    /**
    * Crea una nueva cola de numeros primos con la capacidad especificada.
    * 
    * @param capacity Capacidad maxima de la cola (0 significa sin limite)
    */
    public PrimesQueue(int capacity) {
    this.queue = new ConcurrentLinkedQueue<>();
    this.lock = new ReentrantLock(true); // true para bloqueo justo
    this.notEmpty = lock.newCondition();
    this.capacity = capacity;
    this.operationsCount = 0;
    }
    
    /**
    * Agrega un numero primo a la cola.
    * <p>
    * Si la cola tiene una capacidad maxima y esta llena, el metodo
    * bloqueara hasta que haya espacio disponible.
    * </p>
    * 
    * @param prime Numero primo a agregar
    * @throws InterruptedException Si el hilo es interrumpido mientras espera
    * @throws IllegalArgumentException Si el numero no es primo
    */
    public void offer(Integer prime) throws InterruptedException {
    if (!com.safevotesystem.model.PrimesList.isPrime(prime)) {
    throw new IllegalArgumentException("El numero " + prime + " no es primo");
    }
    
    lock.lock();
    try {
    // Si hay capacidad maxima, esperar hasta que haya espacio
    while (capacity > 0 && queue.size() >= capacity) {
    // Esperar hasta que haya espacio
    wait();
    }
    
    queue.offer(prime);
    operationsCount++;
    
    // Notificar a los consumidores que hay un nuevo elemento
    notEmpty.signal();
    
    System.out.println("Numero primo " + prime + " agregado a la cola. Tamaño actual: " + queue.size());
    } finally {
    lock.unlock();
    }
    }
    
    /**
    * Obtiene y elimina el primer numero primo de la cola.
    * <p>
    * Si la cola esta vacia, el metodo bloqueara hasta que haya
    * elementos disponibles.
    * </p>
    * 
    * @return El primer numero primo de la cola
    * @throws InterruptedException Si el hilo es interrumpido mientras espera
    */
    public Integer poll() throws InterruptedException {
    lock.lock();
    try {
    // Esperar hasta que haya elementos en la cola
    while (queue.isEmpty()) {
    notEmpty.await();
    }
    
    Integer prime = queue.poll();
    operationsCount++;
    
    // Notificar a los productores que hay espacio disponible
    if (capacity > 0) {
    notifyAll();
    }
    
    System.out.println("Numero primo " + prime + " obtenido de la cola. Tamaño actual: " + queue.size());
    return prime;
    } finally {
    lock.unlock();
    }
    }
    
    /**
    * Obtiene y elimina un lote de numeros primos de la cola.
    * <p>
    * Si la cola esta vacia, el metodo bloqueara hasta que haya
    * al menos un elemento disponible. Luego, obtendra hasta batchSize
    * elementos o los disponibles en la cola.
    * </p>
    * 
    * @param batchSize Tamaño maximo del lote a obtener
    * @return Lista con los numeros primos obtenidos
    * @throws InterruptedException Si el hilo es interrumpido mientras espera
    */
    public List<Integer> pollBatch(int batchSize) throws InterruptedException {
        lock.lock();
        try {
            List<Integer> batch = new ArrayList<>(batchSize);
            
            // Esperar hasta que haya al menos un elemento
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            
            // Obtener hasta batchSize elementos (o los disponibles)
            for (int i = 0; i < batchSize && !queue.isEmpty(); i++) {
                Integer prime = queue.poll();
                if (prime != null) {
                    batch.add(prime);
                    operationsCount++;
                }
            }
            
            // Notificar a los productores que hay espacio disponible
            if (capacity > 0) {
                notifyAll();
            }
            
            System.out.println("Lote de " + batch.size() + " numeros primos obtenido de la cola. Tamaño actual: " + queue.size());
            return batch;
        } finally {
            lock.unlock();
        }
    }
    
    /**
    * Agrega un lote de numeros primos a la cola.
    * <p>
    * Todos los numeros en el lote deben ser primos.
    * </p>
    * 
    * @param primes Lista de numeros primos a agregar
    * @throws InterruptedException Si el hilo es interrumpido mientras espera
    * @throws IllegalArgumentException Si algun numero no es primo
    */
    public void offerBatch(List<Integer> primes) throws InterruptedException {
        // Verificar que todos sean primos primero
        for (Integer prime : primes) {
            if (!com.safevotesystem.model.PrimesList.isPrime(prime)) {
                throw new IllegalArgumentException("El numero " + prime + " no es primo");
            }
        }
        
        lock.lock();
        try {
            // Si hay capacidad maxima, verificar espacio
            if (capacity > 0 && queue.size() + primes.size() > capacity) {
                System.out.println("Advertencia: No hay suficiente espacio para todo el lote. Se agregaran los que quepan.");
            }
            
            int added = 0;
            for (Integer prime : primes) {
                // Si hay capacidad maxima, verificar para cada elemento
                if (capacity > 0 && queue.size() >= capacity) {
                    break;
                }
                
                queue.offer(prime);
                operationsCount++;
                added++;
                
                // Notificar a los consumidores que hay un nuevo elemento
                notEmpty.signal();
            }
            
            System.out.println("Lote de " + added + " numeros primos agregado a la cola. Tamaño actual: " + queue.size());
        } finally {
            lock.unlock();
        }
    }
    
    /**
    * Obtiene el primer numero primo de la cola sin eliminarlo.
    * 
    * @return El primer numero primo de la cola, o null si esta vacia
    */
    public Integer peek() {
    return queue.peek();
    }
    
    /**
    * Verifica si la cola esta vacia.
    * 
    * @return true si la cola esta vacia, false en caso contrario
    */
    public boolean isEmpty() {
    return queue.isEmpty();
    }
    
    /**
    * Obtiene el tamaño actual de la cola.
    * 
    * @return Cantidad de elementos en la cola
    */
    public int size() {
    return queue.size();
    }
    
    /**
    * Obtiene la cantidad de operaciones realizadas en la cola.
    * 
    * @return Contador de operaciones (offer + poll)
    */
    public int getOperationsCount() {
    return operationsCount;
    }
    
    /**
    * Limpia la cola, eliminando todos los elementos.
    */
    public void clear() {
    lock.lock();
    try {
    queue.clear();
    System.out.println("Cola de numeros primos limpiada");
    } finally {
    lock.unlock();
    }
    }
}