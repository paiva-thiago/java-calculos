package br.com.thiagopaiva.java.calc;

import br.com.thiagopaiva.java.exceptions.FormulaException;

public class Calculadora {
	private String str;
	private int pos;
	private int ch;
	/**
	 * Classe responsável pelo parse da expressão em String. <a href="https://stackoverflow.com/a/26227947/4271396">Baseado na solução proposta por Boann no StackOverFlow</a>.
	 * 
	 * @param str - a expressão, em formato String, que será "traduzida" para uma fórmula matemática. Ele instanciará o objeto, e seu resultado, se disponível, estará no método parse();
	 * 
	 *  
	 */
	public Calculadora(String str) {
		this.str = str;
		this.pos = -1;
	}

	private void nextChar() {
		ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	}

	private boolean eat(int charToEat) {
		while (ch == ' ')
			nextChar();
		if (ch == charToEat) {
			nextChar();
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @return o resultado do c�lculo.Em caso de falha, ser� lan�ado um Exception, assim como nas valida��es na classe {@link Formula}.
	 */
	public double parse() {
		nextChar();
		double x = parseExpression();
		if (pos < str.length())
			throw new FormulaException("Unexpected: " + (char) ch);
		return x;
	}

	private double parseExpression() {
		double x = parseTerm();
		for (;;) {
			if (eat('+'))
				x += parseTerm(); // addition
			else if (eat('-'))
				x -= parseTerm(); // subtraction
			else
				return x;
		}
	}

	private double parseTerm() {
		double x = parseFactor();
		for (;;) {
			if (eat('*'))
				x *= parseFactor(); // multiplication
			else if (eat('/'))
				x /= parseFactor(); // division
			else
				return x;
		}
	}

	private double parseFactor(){
		if (eat('+')) {
			return parseFactor(); // unary plus
		}else if (eat('-')) {
			return -parseFactor(); // unary minus
		}		

		return operation();
	}
	private double operation() {
		double x = 0;
		int startPos = this.pos;
		if (eat('(')) { // parentheses
			x = parseExpression();
			eat(')');
		} else if (isNumber(ch)) { // numbers
			while (isNumber(ch))
				nextChar();
			x = Double.parseDouble(str.substring(startPos, this.pos));
		} else if (ch >= 'a' && ch <= 'z') { // functions
			while (ch >= 'a' && ch <= 'z') {
				nextChar();
			}
			x = switchFactor(startPos);
		} else {
			throw new FormulaException("Unexpected: " + (char) ch);
		}

		if (eat('^'))
			x = Math.pow(x, parseFactor()); // exponentiation
		return x;
	}
	
	private double switchFactor(int startPos) {
		String func = str.substring(startPos, this.pos);
		double x = parseFactor();
		if (func.equals("sqrt"))
			return Math.sqrt(x);
		else if (func.equals("sin"))
			return Math.sin(Math.toRadians(x));
		else if (func.equals("cos"))
			return Math.cos(Math.toRadians(x));
		else if (func.equals("tan"))
			return Math.tan(Math.toRadians(x));
		else
			throw new FormulaException("Unknown function: " + func);
	}
	
	private boolean isNumber(int ch) {
		return ((ch >= '0' && ch <= '9') || ch == '.');
	}
}
