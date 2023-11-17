package sintatico;

public class PosFixa {

    private String lexema;
    private Tipo tipo;
    private int precedencia;

    public PosFixa(String lexema, Tipo tipo, int precedencia) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.precedencia = precedencia;
    }

    public PosFixa(String lexema, Tipo tipo) {
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public PosFixa(String lexema, int precedencia) {
        this.lexema = lexema;
        this.precedencia = precedencia;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public int getPrecedencia() {
        return precedencia;
    }

    public void setPrecedencia(int precedencia) {
        this.precedencia = precedencia;
    }
}
