package io.github.paivathiago.javacalc.calc;

import io.github.paivathiago.javacalc.exceptions.FormulaException;

class Calculator {
	private final String str;
	private int pos;
	private int ch;
	/**
	 * This class is responsible by the  String parse expression. <a href="https://stackoverflow.com/a/26227947/4271396">Based on this solution proposed by Boann on StackOverFlow</a>.
	 * 
	 * @param str - This expression will be translated by a math formula. It will create an object instance, and its result will be available on parse method;
	 * 
	 *  
	 */
	protected Calculator(String str) {
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
	 * @return The calc result. In case of failure, an Exception will be thrown, as on validations on {@link Formula} class.
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
		double x;
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
        return switch (func) {
            case "sqrt" -> Math.sqrt(x);
            case "sin" -> Math.sin(Math.toRadians(x));
            case "cos" -> Math.cos(Math.toRadians(x));
            case "tan" -> Math.tan(Math.toRadians(x));
            default -> throw  new FormulaException("Unknown function: " + func);
        };
	}
	
	private boolean isNumber(int ch) {
		return ((ch >= '0' && ch <= '9') || ch == '.');
	}
}
