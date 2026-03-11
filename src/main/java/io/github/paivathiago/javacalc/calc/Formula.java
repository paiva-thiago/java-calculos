package io.github.paivathiago.javacalc.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import io.github.paivathiago.javacalc.exceptions.FormulaException;
/**
 * <p>Classe destinada a calcular fórmulas matemáticas.</p>
 * 
 * <p>Aplicação inspirada no <a href="https://stackoverflow.com/a/26227947/4271396"> código do Boann no StackOverFlow</a>.</p>
 * 
 * @author Thiago Paiva
 * 
 * 
 *
 */
public class Formula {
	private String sFormula;
	private boolean error;
	private final List<String> params;
	/**
	 * Para construir a fórmula é necessário dois argumentos: a fórmula e os parâmetros da fórmula.
	 *
	 * @param formula
	 * A fórmula é definida como um cálculo realizado em um editor de planilhas, mas com os campos
	 * definidos através dos caracteres @ e #, com um número entre eles. Exemplo:
	 * <p>
	 *  <code>
	 *  	&#064;0#  + @1#
	 *  </code>
	 * </p>
	 *  Definirá uma soma entre dois valores. Estes valo
	 *
	 * Eles serão associados através de um template, em que os camoos
	 *
	 * @param params
	 * São os parâmetros que serão associados com os campos da fórmula, na ordem em que os mesmos foram definidos.
	 * Eles devem ser separados por um ponto-e-vírgula (;). Baseando-se no exemplo da fórmula, temos como exemplo:
	 * <p>
	 * <code>
	 * 		40;2
	 * </code>
	 * </p>
	 * Ao mesclar os valores, a fórmula calculará 40+2, tendo seu resultado obtido através do método <code>getResultado()</code>.
	 */
	public Formula(String formula, String params) {
		this.error =true;
		String[] aPrms  = params.split(";");
		this.params = new ArrayList<String>();
		formulaValidation(formula,aPrms);
		
	}
	private int countChar(char s,String str) {
		int count = 0;		  
		for (int i = 0; i < str.length(); i++) {
		    if (str.charAt(i) == s) {
		        count++;
		    }
		}
		return count;
	}
	private void formulaValidation(String formula, String[] aParams) {
		this.sFormula=formula;
		if (countChar('(',formula)!=countChar(')',formula)) {
			this.error =true;
			return;
		}
		if(validParams(formula,aParams.length)) {
			this.error =false;
			int x=0;
			for(String param:params) {				
				this.sFormula=this.sFormula.replace(param, aParams[x]);
				x++;
			}
			try {
				new Calculator(this.sFormula).parse();
				this.getCalc();
				this.error =false;
			}catch(FormulaException e) {
				this.error =true;
			}
		}
	}
	private boolean validParams(String formula, int qtParams) {
		int 	lastClosing   		    = 	0;
		int 	qtde					= 	0;
		int  	c						=	0;
		int     max 					=   0;
		while(c<formula.length()) {
			char crc = formula.charAt(c);
			if(crc=='@') {
				lastClosing=formula.indexOf('#',c)+1;
				String item = formula.substring(c,lastClosing);
				Integer parsingTest 	 = null;
				try {
					parsingTest = Integer.parseInt(item.substring(1,item.length()-1));
					if((parsingTest<0)||(parsingTest>max)) {
						throw new FormulaException("Erro no formato do campo!");
					}else if (!this.params.contains(item)) {
						max++;
						this.params.add(item);
						qtde++;
					}					
				} catch(FormulaException e) {
					return false;
				}
			  c=lastClosing;
			}else{
			  c++;
			}
		}
		return (qtde==qtParams);
	}
	/**
	 * 
	 * @return Returns true if the formula is valid
	 */
	public boolean valid() {
		return !this.error;
	}
	/**
	 * 
	 * @return O resultado do cálculo definido na fórmula. É lançada uma Exception em caso de falhas.
	 * @deprecated use getCalculo.
	 */
	@Deprecated
	public double getResult(){
		if(this.valid()) {
			double result = this.eval(this.sFormula);
			return BigDecimal.valueOf(result).setScale(5, RoundingMode.HALF_DOWN).doubleValue();
		}else {
			this.error =true;
			throw new FormulaException("ERROR ON FORMULA: "+this.sFormula);
		}
	}

	public double getCalc(){
		if(this.valid()) {
			double result = new Calculator(this.sFormula).parse();
			return BigDecimal.valueOf(result).setScale(5, RoundingMode.HALF_DOWN).doubleValue();
		}else {
			this.error =true;
			throw new FormulaException("ERROR ON FORMULA: "+this.sFormula);
		}
	}
	/**
	 * Fórmula que fará o parse da expressão em String. <a href="https://stackoverflow.com/a/26227947/4271396">Método baseado na solução proposta por Boann no StackOverFlow</a>.
	 * 
	 * @param str - a expressão, em formato String, que será "traduzida" para uma fórmula matemática.
	 * @return o resultado do cálculo. Em caso de falha, será retornado 0 e lançado um {@link FormulaException}.
	 * @deprecated use o método getCalculo;
	 *  
	 */
	@Deprecated
	private double eval(final String str){
		return new Object() {
	        int pos = -1; 
	        int ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new FormulaException("Unexpected: " + (char)ch);
	            return x;
	        }

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new FormulaException("Unknown function: " + func);
	            } else {
	                throw new FormulaException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}

}
