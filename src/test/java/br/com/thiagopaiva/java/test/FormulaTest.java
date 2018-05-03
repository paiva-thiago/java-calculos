package br.com.thiagopaiva.java.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;



import br.com.thiagopaiva.java.calc.Formula;
import br.com.thiagopaiva.java.exceptions.FormulaException;

public class FormulaTest {

	
	private Formula formula;
	
	private Logger log;
	public FormulaTest() {
		log = Logger.getLogger("Teste");
	}
	
	@Test
	public void formulaSimplesTest() {
		formula = new Formula("@0#+@1#-@2#","48;10;50");
		double esperado=8d;
		double calculado=formula.getCalculo();		
		Assert.assertEquals(esperado,calculado,0d);
	}
	
	@Test
	public void formulaSimplesClasseTest() {
		formula = new Formula("@0#+@1#-@2#","48;10;50");
		double esperado=8d;
		double calculado=formula.getCalculo();		
		Assert.assertEquals(esperado,calculado,0d);
	}
	
	
	@Test
	public void formulaSimplesPodeRepetirCamposTest() {
		formula = new Formula("(@0#+@1#-@2#)*@1#-@2#+@0#","48;10;50");
		double esperado=78d;
		double calculado=formula.getCalculo();
		Assert.assertEquals(esperado,calculado,0d);
	}
	
	@Test
	public void formulaComErroNaoDeveCalcularParteUmTest() {
		try {
			formula = new Formula("(@0+@1#","48;10;50");
			Assert.assertFalse(formula.valido());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Erro conforme esperado em Teste:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaComErroNaoDeveCalcularParteDoisTest() {
		try {
			formula = new Formula("@0#+@1#-@2#","48;10");
			Assert.assertFalse(formula.valido());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Erro conforme esperado em Teste:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaComErroNaoDeveCalcularParteTresTest() {
		try {
			formula = new Formula("(@0#+@1#-@2#","48;10;29");
			Assert.assertFalse(formula.valido());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Erro conforme esperado em Teste:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	@Test
	public void formulaSomenteAceitaParentesesTest() {
		try {
			formula = new Formula("[(@0#+@1#-@2#)/3]^4","48;10;29");
			Assert.assertFalse(formula.valido());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Erro conforme esperado em Teste:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaDeveTerParametrosEmSequenciaTest() {
		try {
			formula = new Formula("@0#+@2#-@3#","48;10;50");
			Assert.assertFalse(formula.valido());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Erro conforme esperado em Teste:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void formulaDeveTerPrimeiroParametroZeroTest() {
		try {
			formula = new Formula("@1#+@2#-@3#","48;10;50");
			Assert.assertFalse(formula.valido());
		}catch(FormulaException e) {
			log.log(Level.INFO, "Erro conforme esperado em Teste:\n\t "+e.getMessage());
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void principalTest() {
		formula = new Formula("@0#*((6/100/12)/((1+6/100/12)^(240-@1#)-1))","4713852.24869;37");
		double esperado  = 13449.75323;
		double calculado = formula.getCalculo(); 
		Assert.assertEquals(esperado,calculado,0d);
	}

}
