import lexical.LexicalAnalyzer;
import sintatico.AnalisadorSintatico;

public class Main {
    public static void main(String[] args) {
        AnalisadorSintatico analisador = new AnalisadorSintatico();

        analisador.analisa();
    }
}