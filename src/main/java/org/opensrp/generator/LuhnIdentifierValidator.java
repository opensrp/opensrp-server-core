package org.opensrp.generator;

import org.opensrp.domain.IdentifierSource;
import org.opensrp.exception.UnallowedIdentifierException;

public class LuhnIdentifierValidator {

	public int getCheckDigit(String undecoratedIdentifier) {
		//		 remove leading or trailing whitespace, convert to uppercase
		String trimmedUppercaseUndecoratedIdentifier = undecoratedIdentifier.trim().toUpperCase();

		// this will privatebe a running total
		int sum = 0;

		// loop through digits from right to left
		for (int i = 0; i < trimmedUppercaseUndecoratedIdentifier.length(); i++) {

			// set ch to "current" character to be processed
			char ch = trimmedUppercaseUndecoratedIdentifier.charAt(trimmedUppercaseUndecoratedIdentifier.length() - i - 1);

			// our "digit" is calculated using ASCII value - 48
			int digit = (int) ch - 48;

			// weight will be the current digit's contribution to
			// the running total
			int weight;
			if (i % 2 == 0) {

				// for alternating digits starting with the rightmost, we
				// use our formula this is the same as multiplying x 2 and
				// adding digits together for values 0 to 9. Using the
				// following formula allows us to gracefully calculate a
				// weight for non-numeric "digits" as well (from their
				// ASCII value - 48).
				weight = (2 * digit) - (digit / 5) * 9;

			} else {

				// even-positioned digits just contribute their ascii
				// value minus 48
				weight = digit;

			}

			// keep a running total of weights
			sum += weight;

		}

		// avoid sum less than 10 (if characters below "0" allowed,
		// this could happen)
		sum = Math.abs(sum) + 10;

		// check digit is amount needed to reach next number
		// divisible by ten
		return (10 - (sum % 10)) % 10;
	}

	public String getValidIdentifier(String undecoratedIdentifier, IdentifierSource identifierSource)
			throws UnallowedIdentifierException {

		checkAllowedIdentifier(undecoratedIdentifier, identifierSource);

		int checkDigit = getCheckDigit(undecoratedIdentifier);

		return undecoratedIdentifier + "-" + checkDigit;
	}

	protected void checkAllowedIdentifier(String undecoratedIdentifier, IdentifierSource identifierSource)
			throws UnallowedIdentifierException {
		if (undecoratedIdentifier == null) {
			throw new UnallowedIdentifierException("Identifier can not be null.");
		}
		if (undecoratedIdentifier.length() == 0) {
			throw new UnallowedIdentifierException("Identifier must contain at least one character.");
		}
		if (undecoratedIdentifier.contains(" ")) {
			throw new UnallowedIdentifierException("Identifier may not contain white space.");
		}
		for (int i = 0; i < undecoratedIdentifier.length(); i++) {
			if (getAllowedCharacters(identifierSource).indexOf(undecoratedIdentifier.charAt(i)) == -1) {
				throw new UnallowedIdentifierException("\"" + undecoratedIdentifier.charAt(i)
						+ "\" is an invalid character.");
			}
		}
	}

	public String getAllowedCharacters(IdentifierSource identifierSource) {
		String prefix = identifierSource.getPrefix()!=null ? identifierSource.getPrefix() : "";
		String suffix = identifierSource.getSuffix()!=null ? identifierSource.getSuffix() : "";
		String baseCharacterSet = identifierSource.getSuffix()!=null ? identifierSource.getSuffix() : "";
		return prefix + baseCharacterSet + suffix;
	}
}
