package io.github.paivathiago.javacalc.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;



import io.github.paivathiago.javacalc.calc.Formula;
import io.github.paivathiago.javacalc.exceptions.FormulaException;

public class FormulaTest {

	
	private Formula formula;
	
	private final Logger log;
	public FormulaTest() {
		log = Logger.getLogger("Teste");
	}
	
	@Test
	public void simpleFormulaTest() {
		formula = new Formula("@0#+@1#-@2#","48;10;50");
		double expected=8d;
		double returned=formula.getCalc();
		Assert.assertEquals(expected,returned,0d);
	}
	
	@Test
	public void simpleFormulaCanRepeatFieldsTest() {
		formula = new Formula("(@0#+@1#-@2#)*@1#-@2#+@0#","48;10;50");
		double expected=78d;
		double returned=formula.getCalc();
		Assert.assertEquals(expected,returned,0d);
	}
	
	@Test
	public void formulaWithErrorShouldNotCalculateTest() {
		try {
			formula = new Formula("(@0+@1#","48;10;50");
			Assert.assertFalse(formula.valid());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Error as expected on test:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaWithErrorShouldNotCalculate_2Test() {
		try {
			formula = new Formula("@0#+@1#-@2#","48;10");
			Assert.assertFalse(formula.valid());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Error as expected on test:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaWithErrorShouldNotCalculate_3Test() {
		try {
			formula = new Formula("(@0#+@1#-@2#","48;10;29");
			Assert.assertFalse(formula.valid());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Error as expected on test:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	@Test
	public void formulaOnlyAcceptsParametersTest() {
		try {
			formula = new Formula("[(@0#+@1#-@2#)/3]^4","48;10;29");
			Assert.assertFalse(formula.valid());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Error as expected em Teste:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaShouldHaveSequentialParametersTest() {
		try {
			formula = new Formula("@0#+@2#-@3#","48;10;50");
			Assert.assertFalse(formula.valid());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Error as expected on test:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaShouldHaveFirstParameterAsZero() {
		try {
			formula = new Formula("@1#+@2#-@3#","48;10;50");
			Assert.assertFalse(formula.valid());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Error as expected on test:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void principalTest() {
		formula = new Formula("@0#*((6/100/12)/((1+6/100/12)^(240-@1#)-1))","4713852.24869;37");
		double expected  = 13449.75323;
		double returned = formula.getCalc();
		Assert.assertEquals(expected,returned,0d);
	}

}
