/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.security;

/**
 * Clase encargada de la codificacion de mensajes utilizando numeros primos.
 * <p>
 * Esta clase implementa un algoritmo simple de encriptacion basado en numeros primos
 * para proteger la informacion en el sistema de votacion.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.0
 * @since 2023-07-01
 */
public class PrimeEncoder {
    
    /**
     * Codifica un mensaje utilizando un numero primo como clave.
     * 
     * @param message El mensaje a codificar
     * @param primeCode El codigo primo a utilizar como clave
     * @return El mensaje codificado
     */
    public String encode(String message, int primeCode) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        
        StringBuilder encoded = new StringBuilder();
        char[] chars = message.toCharArray();
        
        for (char c : chars) {
            // Aplicar transformacion basada en el numero primo
            int transformed = (c + primeCode) % 65536; // Usar el rango completo de caracteres Unicode
            encoded.append((char) transformed);
        }
        
        return encoded.toString();
    }
}