package br.edu.ufcg.lsd.oursim.util;

public class LineParser {

	private final String line;
	private int idx = 0;
	
	/**
	 * @param line
	 */
	public LineParser(String line) {
		this.line = line;
	}
	
	public String next() {
		StringBuilder b = new StringBuilder();
		while (true) {
			b.append(line.charAt(idx));
			if (idx + 1 >= line.length() || Character.isWhitespace(line.charAt(idx+1))) {
				break;
			}
			idx++;
		}
		
		while (true) {
			if (idx + 1 >= line.length() || !Character.isWhitespace(line.charAt(idx+1))) {
				break;
			}
			idx++;
		}
		return b.toString().trim();
	}
	
	public String restOfLine() {
		if (idx >= line.length()) {
			return null;
		}
		return line.substring(idx).trim();
	}
	
}
