/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.security;

/**
 * Clase encargada de la decodificacion de mensajes encriptados con numeros primos.
 * <p>
 * Esta clase implementa el algoritmo inverso al de PrimeEncoder para recuperar
 * los mensajes originales a partir de su version encriptada.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.0
 * @since 2023-07-01
 */
public class PrimeDecoder {
    
    /**
     * Decodifica un mensaje utilizando un numero primo como clave.
     * 
     * @param encodedMessage El mensaje codificado
     * @param primeCode El codigo primo utilizado en la codificacion
     * @return El mensaje original decodificado
     */
    public String decode(String encodedMessage, int primeCode) {
        if (encodedMessage == null || encodedMessage.isEmpty()) {
            return "";
        }
        
        StringBuilder decoded = new StringBuilder();
        char[] chars = encodedMessage.toCharArray();
        
        for (char c : chars) {
            // Aplicar transformacion inversa
            int transformed = c - primeCode;
            // Ajustar si el valor es negativo
            if (transformed < 0) {
                transformed += 65536; // Ajustar dentro del rango Unicode
            }
            decoded.append((char) transformed);
        }
        
        return decoded.toString();
    }
}