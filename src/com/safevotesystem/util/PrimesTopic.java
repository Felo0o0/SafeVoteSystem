/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementacion de un topico (topic) para distribuir mensajes relacionados con numeros primos
 * en un modelo de publicacion-suscripcion.
 * <p>
 * Esta clase permite que multiples componentes (suscriptores) reciban mensajes
 * publicados por otros componentes (publicadores) de manera sincronizada y segura
 * en entornos concurrentes.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.0
 * @since 2023-07-01
 */
public class PrimesTopic {
    
    /** Interfaz para recibir mensajes del topico */
    public interface MessageListener {
        /**
         * Metodo invocado cuando se recibe un mensaje en el topico.
         * 
         * @param message El mensaje recibido
         * @param primeNumber El numero primo asociado al mensaje (si aplica)
         */
        void onMessageReceived(String message, Integer primeNumber);
    }
    
    /** Lista sincronizada de suscriptores */
    private final List<MessageListener> subscribers;
    
    /** Lock para sincronizar operaciones de lectura/escritura */
    private final ReadWriteLock lock;
    
    /** Nombre identificador del topico */
    private final String topicName;
    
    /** Contador de mensajes publicados */
    private int messageCount;
    
    /**
     * Crea un nuevo topico con el nombre especificado.
     * 
     * @param topicName Nombre del topico
     */
    public PrimesTopic(String topicName) {
        this.subscribers = Collections.synchronizedList(new ArrayList<>());
        this.lock = new ReentrantReadWriteLock();
        this.topicName = topicName;
        this.messageCount = 0;
    }
    
    /**
     * Suscribe un oyente para recibir mensajes del topico.
     * 
     * @param listener El oyente a suscribir
     * @return true si se agrego correctamente, false si ya estaba suscrito
     */
    public boolean subscribe(MessageListener listener) {
        lock.writeLock().lock();
        try {
            if (!subscribers.contains(listener)) {
                subscribers.add(listener);
                System.out.println("Nuevo suscriptor agregado al topico: " + topicName);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Cancela la suscripcion de un oyente para no recibir mas mensajes.
     * 
     * @param listener El oyente a desuscribir
     * @return true si se elimino correctamente, false si no estaba suscrito
     */
    public boolean unsubscribe(MessageListener listener) {
        lock.writeLock().lock();
        try {
            boolean removed = subscribers.remove(listener);
            if (removed) {
                System.out.println("Suscriptor eliminado del topico: " + topicName);
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Publica un mensaje en el topico para todos los suscriptores.
     * 
     * @param message El mensaje a publicar
     * @param primeNumber El numero primo asociado al mensaje (puede ser null)
     */
    public void publish(String message, Integer primeNumber) {
        lock.readLock().lock();
        try {
            messageCount++;
            System.out.println("Publicando mensaje en topico " + topicName + ": " + message);
            
            // Notificar a todos los suscriptores
            for (MessageListener listener : subscribers) {
                try {
                    listener.onMessageReceived(message, primeNumber);
                } catch (Exception e) {
                    System.err.println("Error al notificar a suscriptor: " + e.getMessage());
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Publica un mensaje en el topico sin numero primo asociado.
     * 
     * @param message El mensaje a publicar
     */
    public void publish(String message) {
        publish(message, null);
    }
    
    /**
     * Obtiene la cantidad de suscriptores actuales.
     * 
     * @return Cantidad de suscriptores
     */
    public int getSubscribersCount() {
        lock.readLock().lock();
        try {
            return subscribers.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Obtiene la cantidad de mensajes publicados.
     * 
     * @return Contador de mensajes
     */
    public int getMessageCount() {
        return messageCount;
    }
    
    /**
     * Obtiene el nombre del topico.
     * 
     * @return Nombre del topico
     */
    public String getTopicName() {
        return topicName;
    }
}
