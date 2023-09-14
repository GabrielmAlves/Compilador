package lexical;

public class Token {
	private String lexema;
	private String simbolo;

	public Token(String simbolo, String lexema) {
		this.lexema = lexema;
		this.simbolo = simbolo;
	}

	public Token() {}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}
}
