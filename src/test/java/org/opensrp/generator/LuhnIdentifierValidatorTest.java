package org.opensrp.generator;

import org.junit.Test;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.exception.UnallowedIdentifierException;

public class LuhnIdentifierValidatorTest {

	@Test(expected = UnallowedIdentifierException.class)
	public void testGetCheckDigitWithEmptyId() {
		LuhnIdentifierValidator luhnIdentifierValidator = new LuhnIdentifierValidator();
		luhnIdentifierValidator.getValidIdentifier("",createIdentifierSource());
	}

	@Test(expected = UnallowedIdentifierException.class)
	public void testGetCheckDigitWithNull() {
		LuhnIdentifierValidator luhnIdentifierValidator = new LuhnIdentifierValidator();
		luhnIdentifierValidator.getValidIdentifier(null,createIdentifierSource());
	}

	@Test(expected = UnallowedIdentifierException.class)
	public void testGetCheckDigitWithWhiteSpaces() {
		LuhnIdentifierValidator luhnIdentifierValidator = new LuhnIdentifierValidator();
		luhnIdentifierValidator.getValidIdentifier("A B12",createIdentifierSource());
	}

	@Test(expected = UnallowedIdentifierException.class)
	public void testGetCheckDigitWithIncorrectFirstIdentifierBase() {
		LuhnIdentifierValidator luhnIdentifierValidator = new LuhnIdentifierValidator();
		luhnIdentifierValidator.getValidIdentifier("A B12",createIdentifierSource());
	}

	private IdentifierSource createIdentifierSource() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setId(1l);
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setFirstIdentifierBase("ab12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);
		return identifierSource;
	}
}
