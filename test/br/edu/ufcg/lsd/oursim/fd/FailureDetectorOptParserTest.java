package br.edu.ufcg.lsd.oursim.fd;

import junit.framework.Assert;

import org.junit.Test;

public class FailureDetectorOptParserTest {

	@Test
	public void testParseInt() {
		Assert.assertEquals(0, FailureDetectorOptParser.parseInt(-1, "0"));
		Assert.assertEquals(0, FailureDetectorOptParser.parseInt(0, null));
	}

	@Test(expected = NumberFormatException.class)
	public void testParseIntError() {
		FailureDetectorOptParser.parseInt(0, "zero");
	}

	@Test
	public void testParseDouble() {
		Assert.assertEquals(0., FailureDetectorOptParser.parseDouble(-1, "0"));
		Assert.assertEquals(0., FailureDetectorOptParser.parseDouble(0., null));
	}

	@Test(expected = NumberFormatException.class)
	public void testParseDoubleError() {
		FailureDetectorOptParser.parseDouble(0, "zero");
	}

	@Test
	public void testParseLong() {
		Assert.assertEquals(0L, FailureDetectorOptParser.parseLong(-1, "0"));
		Assert.assertEquals(0L, FailureDetectorOptParser.parseLong(0, null));
	}

	@Test(expected = NumberFormatException.class)
	public void testParseLongError() {
		FailureDetectorOptParser.parseLong(0, "zero");
	}

}
