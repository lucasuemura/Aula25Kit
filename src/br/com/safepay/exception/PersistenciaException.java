package br.com.safepay.exception;

/**
 * Exceção de domínio não-checada (RuntimeException) para encapsular SQLException
 * seguindo o padrão Exception Translation (isolamento de infraestrutura).
 */
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
