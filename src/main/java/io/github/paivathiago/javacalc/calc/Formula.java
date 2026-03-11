package io.github.paivathiago.javacalc.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import io.github.paivathiago.javacalc.exceptions.FormulaException;

/**
 * <p>Class designed to calculate mathematical formulas.</p>
 *
 * <p>Application inspired by <a href="https://stackoverflow.com/a/26227947/4271396">Boann's code on StackOverflow</a>.</p>
 *
 * @author Thiago Paiva
 */
public class Formula {
	private String sFormula;
	private boolean error;
	private final List<String> params;

	/**
	 * To build the formula, two arguments are required: the formula and its parameters.
	 *
	 * @param formula
	 * The formula is defined as a calculation performed in a spreadsheet editor, but with fields
	 * defined using the characters @ and #, with a number between them. Example:
	 * <p>
	 *   <code>
	 *     &#064;0# + @1#
	 *   </code>
	 * </p>
	 * This will define a sum between two values. They will be associated through a template,
	 * in which the fields are mapped to the provided parameters.
	 *
	 * @param params
	 * The parameters that will be associated with the formula fields, in the order they were defined.
	 * They must be separated by a semicolon `;``. Based on the formula example above, an example would be:
	 * <p>
	 * <code>
	 *   40;2
	 * </code>
	 * </p>
	 * When merging the values, the formula will calculate 40+2, with the result obtained
	 * through the <code>getResult()</code> method.
	 */
	public Formula(String formula, String params) {
		this.error =true;
		String[] aPrms  = params.split(";");
		this.params = new ArrayList<>();
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
		int 	lastClosing;
		int 	elementNumbers=0;
		int  	c=0;
		int     max=0;
		while(c<formula.length()) {
			char crc = formula.charAt(c);
			if(crc=='@') {
				lastClosing=formula.indexOf('#',c)+1;
				String item = formula.substring(c,lastClosing);
				int parsingTest;
				try {
					parsingTest = Integer.parseInt(item.substring(1,item.length()-1));
					if((parsingTest<0)||(parsingTest>max)) {
						throw new FormulaException("Error on field format!");
					}else if (!this.params.contains(item)) {
						max++;
						this.params.add(item);
						elementNumbers++;
					}
				} catch(FormulaException e) {
					return false;
				}
				c=lastClosing;
			}else{
				c++;
			}
		}
		return (elementNumbers==qtParams);
	}

	/**
	 * @return Returns true if the formula is valid.
	 */
	public boolean valid() {
		return !this.error;
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
}