package com.auth.application.exception;

public class SenhaInvalidaException extends RuntimeException {

	public SenhaInvalidaException() {
		super("Senha Invalida");
	}
}
