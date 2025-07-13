/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safevotesystem.model;

import java.io.Serializable;

/**
 * Representa un mensaje seguro en el sistema de votacion SafeVoteSystem.
 * <p>
 * Esta clase encapsula toda la informacion relacionada con un mensaje, incluyendo
 * su contenido, remitente, destinatario, y el numero primo usado para encriptacion.
 * </p>
 * 
 * @author SafeVoteSystem Team
 * @version 1.0
 * @since 2023-07-01
 */
public class Message implements Serializable {
    
    /** UID de serializacion */
    private static final long serialVersionUID = 1L;
    
    /** El contenido del mensaje (puede estar en texto plano o encriptado) */
    private String content;
    
    /** El remitente del mensaje */
    private String sender;
    
    /** El destinatario del mensaje */
    private String recipient;
    
    /** El codigo primo usado para encriptacion/desencriptacion */
    private int primeCode;
    
    /** Indicador de si el mensaje esta actualmente encriptado */
    private boolean encrypted;
    
    /**
    * Crea un nuevo mensaje con remitente y destinatario especificados.
    * <p>
    * El mensaje se crea en estado no encriptado por defecto.
    * </p>
    * 
    * @param content El contenido del mensaje
    * @param sender El remitente del mensaje
    * @param recipient El destinatario del mensaje
    * @param primeCode El codigo primo a usar para encriptacion/desencriptacion
    */
    public Message(String content, String sender, String recipient, int primeCode) {
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
        this.primeCode = primeCode;
        this.encrypted = false;
    }
    
    /**
    * Crea un mensaje simplificado con remitente y destinatario predeterminados.
    * <p>
    * Este constructor establece el remitente como "Sistema" y el destinatario como "Usuario".
    * </p>
    * 
    * @param content El contenido del mensaje
    * @param primeCode El codigo primo a usar para encriptacion/desencriptacion
    */
    public Message(String content, int primeCode) {
        this(content, "Sistema", "Usuario", primeCode);
    }
    
    /**
    * Encripta el contenido del mensaje usando el codigo primo.
    * <p>
    * Este metodo transforma el contenido original usando el numero primo
    * como clave de encriptacion. La implementacion utiliza el PrimeEncoder.
    * </p>
    * 
    * @see com.safevotesystem.security.PrimeEncoder
    */
    public void encrypt() {
        if (!encrypted) {
            this.content = new com.safevotesystem.security.PrimeEncoder().encode(content, primeCode);
            this.encrypted = true;
        }
    }
    
    /**
    * Desencripta el contenido del mensaje usando el codigo primo.
    * <p>
    * Este metodo restaura el contenido original usando el numero primo
    * como clave de desencriptacion. La implementacion utiliza el PrimeDecoder.
    * </p>
    * 
    * @see com.safevotesystem.security.PrimeDecoder
    */
    public void decrypt() {
        if (encrypted) {
            this.content = new com.safevotesystem.security.PrimeDecoder().decode(content, primeCode);
            this.encrypted = false;
        }
    }
    
    /**
    * Obtiene el contenido del mensaje.
    * 
    * @return El contenido (puede estar encriptado o en texto plano)
    */
    public String getContent() {
        return content;
    }
    
    /**
    * Obtiene el remitente del mensaje.
    * 
    * @return El identificador del remitente
    */
    public String getSender() {
        return sender;
    }
    
    /**
    * Obtiene el destinatario del mensaje.
    * 
    * @return El identificador del destinatario
    */
    public String getRecipient() {
        return recipient;
    }
    
    /**
    * Obtiene el codigo primo usado para encriptacion/desencriptacion.
    * 
    * @return El codigo de numero primo
    */
    public int getPrimeCode() {
        return primeCode;
    }
    
    /**
    * Verifica si el mensaje esta actualmente encriptado.
    * 
    * @return true si el mensaje esta encriptado, false en caso contrario
    */
    public boolean isEncrypted() {
        return encrypted;
    }
}
