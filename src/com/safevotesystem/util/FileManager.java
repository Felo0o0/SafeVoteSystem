/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.util;

import com.safevotesystem.model.PrimesList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Clase utilitaria para manejar operaciones de lectura y escritura de archivos.
 * <p>
 * Esta clase proporciona metodos para cargar numeros primos desde archivos CSV
 * y escribir mensajes encriptados junto con sus codigos primos en archivos de texto.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.0
 * @since 2023-07-01
 */
public class FileManager {
    
    /**
     * Carga numeros primos desde un archivo CSV y los agrega a una lista.
     * <p>
     * El archivo CSV debe contener un numero primo por linea.
     * </p>
     * 
     * @param filePath Ruta del archivo CSV que contiene los numeros primos
     * @param primesList Lista a la que se agregaran los numeros primos cargados
     * @return Cantidad de numeros primos cargados exitosamente
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public int loadPrimesFromCSV(String filePath, PrimesList primesList) throws IOException {
        int loadedCount = 0;
        
        synchronized (primesList) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        // Eliminar espacios en blanco y convertir a entero
                        int number = Integer.parseInt(line.trim());
                        
                        // Intentar agregar el numero a la lista si es primo
                        if (!primesList.contains(number)) {
                            primesList.add(number);
                            loadedCount++;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error al convertir a numero: " + line);
                    } catch (IllegalArgumentException e) {
                        // Este error ocurre si el numero no es primo
                        System.err.println("Numero no primo ignorado: " + line);
                    }
                }
            }
        }
        
        System.out.println("Numeros primos cargados desde CSV: " + loadedCount);
        return loadedCount;
    }
    
    /**
     * Escribe un mensaje encriptado y su codigo primo en un archivo de texto.
     * 
     * @param filePath Ruta del archivo donde se escribira el mensaje
     * @param encryptedMessage Mensaje encriptado a escribir
     * @param primeCode Codigo primo utilizado para la encriptacion
     * @param append Si es true, agrega al archivo existente; si es false, sobrescribe
     * @throws IOException Si ocurre un error al escribir en el archivo
     */
    public void writeEncryptedMessageToFile(String filePath, String encryptedMessage, 
            int primeCode, boolean append) throws IOException {
        
        try (FileWriter writer = new FileWriter(filePath, append)) {
            // Escribir un separador si se esta agregando al archivo
            if (append) {
                writer.write("\n----------------------------------------\n");
            }
            
            // Escribir el mensaje encriptado y el codigo primo
            writer.write("Mensaje Encriptado: " + encryptedMessage + "\n");
            writer.write("Codigo Primo: " + primeCode + "\n");
            writer.write("Fecha: " + java.time.LocalDateTime.now() + "\n");
        }
        
        System.out.println("Mensaje encriptado guardado en: " + filePath);
    }
    
    /**
     * Escribe una lista de numeros primos en un archivo CSV.
     * 
     * @param filePath Ruta del archivo CSV donde se escribiran los numeros primos
     * @param primesList Lista de numeros primos a escribir
     * @throws IOException Si ocurre un error al escribir en el archivo
     */
    public void writePrimesToCSV(String filePath, List<Integer> primesList) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Integer prime : primesList) {
                writer.write(prime + "\n");
            }
        }
        
        System.out.println("Numeros primos guardados en CSV: " + filePath);
    }
}