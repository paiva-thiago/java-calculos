package io.github.paivathiago.javacalc.exceptions;

import java.io.Serial;

public class FormulaException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = -312825236068075835L;
	
	public FormulaException(String msg) {
		super(msg);
	}
	
	

}
