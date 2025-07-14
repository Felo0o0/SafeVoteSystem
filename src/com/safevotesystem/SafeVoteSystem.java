/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package com.safevotesystem;

import com.safevotesystem.model.PrimesList;
import com.safevotesystem.model.PrimesThread;
import com.safevotesystem.util.FileManager;
import com.safevotesystem.util.PrimesQueue;
import com.safevotesystem.util.PrimesTopic;
import com.safevotesystem.util.PrimesStats;
import com.safevotesystem.security.PrimeEncoder;
import com.safevotesystem.security.PrimeDecoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.simple.parser.ParseException;

/**
 * Clase principal del sistema de votacion electronica seguro "SafeVoteSystem".
 * <p>
 * Esta clase implementa la funcionalidad principal del sistema, demostrando el uso
 * de hilos, colecciones sincronizadas, colas y topicos para gestionar numeros primos
 * y mensajes encriptados de manera segura y eficiente.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.1
 * @since 2023-07-01
 */
public class SafeVoteSystem {
    
    /** Lista sincronizada de numeros primos */
    private static final PrimesList primesList = new PrimesList();
    
    /** Cola para procesar numeros primos */
    private static final PrimesQueue primesQueue = new PrimesQueue(100);
    
    /** Topico para distribuir mensajes sobre numeros primos */
    private static final PrimesTopic primesTopic = new PrimesTopic("PrimesNotifications");
    
    /** Gestor de archivos */
    private static final FileManager fileManager = new FileManager();
    
    /** Ruta del archivo CSV para almacenar numeros primos */
    private static final String PRIMES_CSV_PATH = "primes.csv";
    
    /** Ruta del archivo para mensajes encriptados */
    private static final String ENCRYPTED_MESSAGES_PATH = "encrypted_messages.txt";
    
    /**
    * Punto de entrada principal del sistema.
    * 
    * @param args Argumentos de la linea de comandos (no utilizados)
    */
    public static void main(String[] args) {
    System.out.println("Iniciando SafeVoteSystem - Sistema de Votacion Electronica Seguro");
    System.out.println("====");
    
    // Registrar un suscriptor para el topico
    registerTopicSubscriber();
    
    // Intentar cargar numeros primos existentes desde archivo CSV
    loadPrimesFromFile();
    
    // Mostrar menu principal
    showMainMenu();
    
    System.out.println("Finalizando SafeVoteSystem. ¡Gracias por utilizar el sistema!");
    }
    
    /**
    * Registra un suscriptor para el topico de numeros primos.
    */
    private static void registerTopicSubscriber() {
    primesTopic.subscribe((message, primeNumber) -> {
    System.out.println(">>> NOTIFICACION: " + message + 
    (primeNumber != null ? " (Primo: " + primeNumber + ")" : ""));
    });
    }
    
    /**
    * Intenta cargar numeros primos desde un archivo CSV.
    */
    private static void loadPrimesFromFile() {
    File primesFile = new File(PRIMES_CSV_PATH);
    if (primesFile.exists()) {
    try {
    int loaded = fileManager.loadPrimesFromCSV(PRIMES_CSV_PATH, primesList);
    primesTopic.publish("Se cargaron " + loaded + " numeros primos desde archivo");
    } catch (IOException e) {
    System.err.println("Error al cargar numeros primos: " + e.getMessage());
    }
    } else {
    System.out.println("No se encontro archivo de numeros primos. Se creara uno nuevo.");
    }
    }
    
    /**
    * Muestra el menu principal y maneja la interaccion del usuario.
    */
    private static void showMainMenu() {
    Scanner scanner = new Scanner(System.in);
    boolean exit = false;
    
    while (!exit) {
    System.out.println("\n==== MENU PRINCIPAL ====");
    System.out.println("1. Generar numeros primos con hilos");
    System.out.println("2. Mostrar lista de numeros primos");
    System.out.println("3. Guardar numeros primos en archivo CSV");
    System.out.println("4. Encriptar un mensaje");
    System.out.println("5. Desencriptar un mensaje");
    System.out.println("6. Procesar lote de numeros primos");
    System.out.println("7. Exportar/Importar datos en JSON");
    System.out.println("8. Ver estadisticas del sistema");
    System.out.println("9. Salir");
    System.out.print("Seleccione una opcion: ");
    
    try {
    int option = Integer.parseInt(scanner.nextLine());
    
    switch (option) {
    case 1:
    generatePrimesWithThreads(scanner);
    break;
    case 2:
    displayPrimesList();
    break;
    case 3:
    savePrimesToFile();
    break;
    case 4:
    encryptMessage(scanner);
    break;
    case 5:
    decryptMessage(scanner);
    break;
    case 6:
    processBatchOfPrimes(scanner);
    break;
    case 7:
    handleJsonExportImport(scanner);
    break;
    case 8:
    PrimesStats.printStats();
    break;
    case 9:
    exit = true;
    break;
    default:
    System.out.println("Opcion invalida. Intente nuevamente.");
    }
    } catch (NumberFormatException e) {
    System.out.println("Por favor, ingrese un numero valido.");
    }
    }
    }
    
    /**
    * Genera numeros primos utilizando multiples hilos.
    * 
    * @param scanner Scanner para leer entrada del usuario
    */
    private static void generatePrimesWithThreads(Scanner scanner) {
    System.out.println("\n==== GENERACION DE NUMEROS PRIMOS ====");
    
    try {
    System.out.print("Ingrese la cantidad de hilos a utilizar (1-10): ");
    int numThreads = Integer.parseInt(scanner.nextLine());
    numThreads = Math.max(1, Math.min(10, numThreads));
    
    System.out.print("Ingrese el limite superior para los numeros (100-10000): ");
    int upperBound = Integer.parseInt(scanner.nextLine());
    upperBound = Math.max(100, Math.min(10000, upperBound));
    
    System.out.print("Ingrese cuantos primos debe encontrar cada hilo (1-100): ");
    int primesPerThread = Integer.parseInt(scanner.nextLine());
    primesPerThread = Math.max(1, Math.min(100, primesPerThread));
    
    // Crear un pool de hilos
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Thread> threads = new ArrayList<>();
    
    System.out.println("\nIniciando " + numThreads + " hilos para generar numeros primos...");
    primesTopic.publish("Iniciando generacion de primos con " + numThreads + " hilos");
    
    long startTime = System.currentTimeMillis();
    
    // Iniciar los hilos
    for (int i = 0; i < numThreads; i++) {
    PrimesThread primesThread = new PrimesThread(primesList, upperBound, primesPerThread);
    Thread thread = new Thread(primesThread, "PrimesThread-" + i);
    threads.add(thread);
    executor.execute(thread);
    }
    
    // Cerrar el executor y esperar a que todos los hilos terminen
    executor.shutdown();
    boolean completed = executor.awaitTermination(2, TimeUnit.MINUTES);
    
    long endTime = System.currentTimeMillis();
    long executionTime = endTime - startTime;
    
    // Registrar tiempo de procesamiento para estadisticas
    PrimesStats.recordProcessingTime(executionTime);
    
    if (completed) {
    System.out.println("\nTodos los hilos han finalizado su ejecucion en " + executionTime + " ms");
    System.out.println("Total de numeros primos encontrados: " + primesList.getPrimesCount());
    primesTopic.publish("Generacion de primos completada. Total: " + primesList.getPrimesCount());
    
    // Registrar los primos generados para estadisticas
    synchronized (primesList) {
        for (Integer prime : primesList) {
            PrimesStats.recordPrimeGenerated(prime);
        }
    }
    } else {
    System.out.println("\nLos hilos no terminaron en el tiempo esperado");
    executor.shutdownNow();
    }
    
    } catch (NumberFormatException e) {
    System.out.println("Error: Debe ingresar un valor numerico");
    } catch (InterruptedException e) {
    System.err.println("Error: La generacion de primos fue interrumpida");
    Thread.currentThread().interrupt();
    }
    }
    
    /**
    * Muestra la lista actual de numeros primos.
    */
    private static void displayPrimesList() {
    System.out.println("\n==== LISTA DE NUMEROS PRIMOS ====");
    
    synchronized (primesList) {
    if (primesList.isEmpty()) {
    System.out.println("La lista de numeros primos esta vacia");
    return;
    }
    
    System.out.println("Cantidad total de numeros primos: " + primesList.getPrimesCount());
    System.out.println("Tamaño de cache de verificacion: " + PrimesList.getCacheSize());
    
    // Crear una copia ordenada para mostrar
    List<Integer> sortedPrimes = new ArrayList<>(primesList);
    Collections.sort(sortedPrimes);
    
    System.out.println("\nNumeros primos en la lista:");
    int count = 0;
    for (Integer prime : sortedPrimes) {
    System.out.print(prime + " ");
    count++;
    if (count % 10 == 0) {
    System.out.println();
    }
    }
    System.out.println();
    }
    }
    
    /**
    * Guarda la lista de numeros primos en un archivo CSV.
    */
    private static void savePrimesToFile() {
    System.out.println("\n==== GUARDAR NUMEROS PRIMOS ====");
    
    synchronized (primesList) {
    if (primesList.isEmpty()) {
    System.out.println("La lista de numeros primos esta vacia. No hay nada que guardar.");
    return;
    }
    
    try {
    fileManager.writePrimesToCSV(PRIMES_CSV_PATH, primesList);
    primesTopic.publish("Numeros primos guardados en archivo CSV");
    } catch (IOException e) {
    System.err.println("Error al guardar numeros primos: " + e.getMessage());
    }
    }
    }
    
    /**
    * Encripta un mensaje utilizando un numero primo como clave.
    * 
    * @param scanner Scanner para leer entrada del usuario
    */
    private static void encryptMessage(Scanner scanner) {
    System.out.println("\n==== ENCRIPTAR MENSAJE ====");
    
    synchronized (primesList) {
    if (primesList.isEmpty()) {
    System.out.println("No hay numeros primos disponibles para encriptacion.");
    System.out.println("Primero debe generar numeros primos.");
    return;
    }
    
    try {
    System.out.print("Ingrese el mensaje a encriptar: ");
    String message = scanner.nextLine();
    
    // Seleccionar un numero primo aleatorio de la lista como clave
    int randomIndex = (int) (Math.random() * primesList.size());
    int primeCode = primesList.get(randomIndex);
    
    // Registrar uso del primo para estadisticas
    PrimesStats.recordPrimeUsed(primeCode);
    
    long startTime = System.currentTimeMillis();
    
    // Encriptar el mensaje
    PrimeEncoder encoder = new PrimeEncoder();
    String encryptedMessage = encoder.encode(message, primeCode);
    
    long endTime = System.currentTimeMillis();
    PrimesStats.recordProcessingTime(endTime - startTime);
    
    System.out.println("\nMensaje encriptado: " + encryptedMessage);
    System.out.println("Codigo primo utilizado: " + primeCode);
    
    // Guardar el mensaje encriptado en archivo
    fileManager.writeEncryptedMessageToFile(ENCRYPTED_MESSAGES_PATH, 
    encryptedMessage, primeCode, true);
    
    primesTopic.publish("Mensaje encriptado con exito", primeCode);
    } catch (IOException e) {
    System.err.println("Error al guardar mensaje encriptado: " + e.getMessage());
    }
    }
    }
    
    /**
    * Desencripta un mensaje utilizando un numero primo como clave.
    * 
    * @param scanner Scanner para leer entrada del usuario
    */
    private static void decryptMessage(Scanner scanner) {
    System.out.println("\n==== DESENCRIPTAR MENSAJE ====");
    
    try {
    System.out.print("Ingrese el mensaje encriptado: ");
    String encryptedMessage = scanner.nextLine();
    
    System.out.print("Ingrese el codigo primo utilizado: ");
    int primeCode = Integer.parseInt(scanner.nextLine());
    
    // Verificar que el codigo sea primo
    if (!PrimesList.isPrime(primeCode)) {
    System.out.println("Error: El codigo ingresado no es un numero primo.");
    return;
    }
    
    long startTime = System.currentTimeMillis();
    
    // Desencriptar el mensaje
    PrimeDecoder decoder = new PrimeDecoder();
    String decryptedMessage = decoder.decode(encryptedMessage, primeCode);
    
    long endTime = System.currentTimeMillis();
    PrimesStats.recordProcessingTime(endTime - startTime);
    
    System.out.println("\nMensaje desencriptado: " + decryptedMessage);
    primesTopic.publish("Mensaje desencriptado con exito");
    } catch (NumberFormatException e) {
    System.out.println("Error: Debe ingresar un valor numerico para el codigo primo");
    }
    }
    
    /**
    * Procesa un lote de numeros para verificar cuales son primos.
    * 
    * @param scanner Scanner para leer entrada del usuario
    */
    private static void processBatchOfPrimes(Scanner scanner) {
        System.out.println("\n==== PROCESAMIENTO POR LOTES ====");
        
        try {
            System.out.print("Ingrese tamaño del lote a procesar (5-50): ");
            int batchSize = Integer.parseInt(scanner.nextLine());
            batchSize = Math.max(5, Math.min(50, batchSize));
            
            // Generar lote de números para verificar
            List<Integer> numbersToCheck = new ArrayList<>();
            int start = 1000 + (int)(Math.random() * 9000);
            for (int i = 0; i < batchSize; i++) {
                numbersToCheck.add(start + i);
            }
            
            System.out.println("Procesando lote de " + batchSize + " numeros...");
            long startTime = System.currentTimeMillis();
            
            // Procesar en lote
            int primesFound = 0;
            List<Integer> foundPrimes = new ArrayList<>();
            
            for (Integer num : numbersToCheck) {
                if (PrimesList.isPrime(num)) {
                    primesFound++;
                    foundPrimes.add(num);
                    // Registrar para estadisticas
                    PrimesStats.recordPrimeGenerated(num);
                }
            }
            
            // Agregar primos encontrados a la lista principal
            synchronized (primesList) {
                for (Integer prime : foundPrimes) {
                    if (!primesList.contains(prime)) {
                        primesList.add(prime);
                    }
                }
            }
            
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;
            PrimesStats.recordProcessingTime(processingTime);
            
            System.out.println("Procesamiento completado en " + processingTime + " ms");
            System.out.println("Numeros primos encontrados en el lote: " + primesFound);
            
            // Mostrar los primos encontrados
            if (!foundPrimes.isEmpty()) {
                System.out.println("Primos encontrados: ");
                int count = 0;
                for (Integer prime : foundPrimes) {
                    System.out.print(prime + " ");
                    count++;
                    if (count % 5 == 0) {
                        System.out.println();
                    }
                }
                System.out.println();
            }
            
            primesTopic.publish("Procesamiento por lotes completado. Primos encontrados: " + primesFound);
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un valor numerico");
        }
    }
    
    /**
    * Maneja la exportacion e importacion de datos en formato JSON.
    * 
    * @param scanner Scanner para leer entrada del usuario
    */
    private static void handleJsonExportImport(Scanner scanner) {
        System.out.println("\n==== EXPORTAR/IMPORTAR JSON ====");
        System.out.println("1. Exportar numeros primos a JSON");
        System.out.println("2. Importar numeros primos desde JSON");
        System.out.print("Seleccione una opcion: ");
        
        try {
            int option = Integer.parseInt(scanner.nextLine());
            
            switch (option) {
                case 1:
                    System.out.print("Ingrese nombre del archivo JSON: ");
                    String exportFile = scanner.nextLine();
                    if (!exportFile.endsWith(".json")) {
                        exportFile += ".json";
                    }
                    
                    synchronized (primesList) {
                        if (primesList.isEmpty()) {
                            System.out.println("La lista de numeros primos esta vacia. No hay nada que exportar.");
                            return;
                        }
                        
                        try {
                            fileManager.exportPrimesToJSON(exportFile, primesList);
                            primesTopic.publish("Numeros primos exportados a " + exportFile);
                        } catch (IOException e) {
                            System.err.println("Error al exportar a JSON: " + e.getMessage());
                        }
                    }
                    break;
                    
                case 2:
                    System.out.print("Ingrese nombre del archivo JSON a importar: ");
                    String importFile = scanner.nextLine();
                    if (!importFile.endsWith(".json")) {
                        importFile += ".json";
                    }
                    
                    File file = new File(importFile);
                    if (!file.exists()) {
                        System.out.println("El archivo " + importFile + " no existe.");
                        return;
                    }
                    
                    try {
                        PrimesList importedList = fileManager.importPrimesFromJSON(importFile);
                        
                        synchronized (primesList) {
                            // Agregar solo los que no existen ya
                            int added = 0;
                            for (Integer prime : importedList) {
                                if (!primesList.contains(prime)) {
                                    primesList.add(prime);
                                    added++;
                                    // Registrar para estadisticas
                                    PrimesStats.recordPrimeGenerated(prime);
                                }
                            }
                            
                            System.out.println("Se agregaron " + added + " nuevos numeros primos a la lista.");
                        }
                        
                        primesTopic.publish("Importacion desde JSON completada");
                    } catch (IOException | ParseException e) {
                        System.err.println("Error al importar desde JSON: " + e.getMessage());
                    }
                    break;
                    
                default:
                    System.out.println("Opcion invalida");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un numero valido.");
        }
    }
}