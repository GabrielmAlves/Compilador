package sintatico;

public class TabelaSimbolos {

    private String lexema;
    private Tipo tipo;
    private boolean escopo;
    private String endMemoria;

    public TabelaSimbolos(String lexema, Tipo tipo, boolean escopo, String endMemoria) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.escopo = escopo;
        this.endMemoria = endMemoria;
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

    public boolean getEscopo() {
        return escopo;
    }

    public void setEscopo(boolean escopo) {
        this.escopo = escopo;
    }

    public String getEndMemoria() {
        return endMemoria;
    }

    public void setEndMemoria(String endMemoria) {
        this.endMemoria = endMemoria;
    }

    protected enum Tipo {
        VARIAVEL,
        VARIAVEL_INTEIRA,
        VARIAVEL_BOOLEANA,
        FUNCAO,
        FUNCAO_BOOLEANA,
        FUNCAO_INTEIRA,
        PROCEDIMENTO,
        PROGRAMA

    }

}
