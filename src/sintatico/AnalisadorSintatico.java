package sintatico;

import lexical.LexicalAnalyzer;
import lexical.Token;

import java.io.File;

public class AnalisadorSintatico {

    private static final String PATH ="C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\arquivos\\teste_1.txt";
    private  File file = new File(PATH);
    private LexicalAnalyzer lexical = new LexicalAnalyzer(file);
    private Token token;
    public void analisa(){

//        for (int i = 0; i<10;i++) {
            token = lexical.analyze();
            System.out.println(token.getLexema() + " " + token.getSimbolo());

//        }

        if(token.getSimbolo().equals("sprograma")){
            if(token.getSimbolo().equals("sidentificador")){
                token = lexical.analyze();
                if(token.getSimbolo().equals("spontovirgula")){
                    analisaBloco();
                    if(token.getSimbolo().equals("sponto")){
                        if(token == null) {
                            // TODO sucesso
                        } else {
                            // TODO erro
                        }

                    }else {
                        // TODO erro
                    }
                }else {
                    // TODO erro
                }

            }else {
                // TODO erro
            }
        }else {
            // TODO erro
        }
    }

    private void analisaBloco(){
        token = lexical.analyze();
        analisaEtVariaveis();
        analisaSubrotinas();
        analisaComandos();
    }

    private void analisaEtVariaveis() {
        if (token.getSimbolo().equals("svar")) {
            token = lexical.analyze();
            if (token.getSimbolo().equals("sidentificador")) {
                while (token.getSimbolo().equals("sidentificador")) {
                    analisaVariaveis();
                    if (token.getSimbolo().equals("spontovirgula")) {
                        token = lexical.analyze();
                    } else {
                        //TODO erro
                    }
                }
            } else {
                //TODO erro
            }
        }

    }

    private void analisaVariaveis() {
        do{
            if (token.getSimbolo().equals("sidentificador")) {
                //TODO semantico

                token = lexical.analyze();
                if (token.getSimbolo().equals("svirgula") || token.getSimbolo().equals("sdoispontos")) {
                    if (token.getSimbolo().equals("svirgula")) {
                        token = lexical.analyze();
                        if (token.getSimbolo().equals("sdoispontos")) {
                            //TODO erro
                        }
                    }
                } else {
                    //TODO erro
                }
            } else {
                //TODO erro
            }
        } while (!token.getSimbolo().equals("sdoispontos"));
        token = lexical.analyze();
        analisaTipo();
    }

    private void analisaTipo() {
        if (!token.getSimbolo().equals("sinteiro") && !token.getSimbolo().equals("sbooleano")) {
            //TODO erro
        } else {
            //TODO semantico
        }
        token = lexical.analyze();
    }

    private void analisaComandos() {
        if (token.getSimbolo().equals("sinicio")){
            token = lexical.analyze();
            analisaComandoSimples();
            while (!token.getSimbolo().equals("sfim")){
                if(token.getSimbolo().equals("spontovirgula")){
                    token = lexical.analyze();
                    if(!token.getSimbolo().equals("sfim")){
                        analisaComandoSimples();
                    }
                } else{
                    //TODO erro
                }
            }
            token = lexical.analyze();
        } else{
            //TODO erro
        }
    }

    private void analisaComandoSimples() {
        switch (token.getSimbolo()) {
            case "sidentificador" -> analisaAtribChprocedimento();
            case "sse" -> analisaSe();
            case "senquanto" -> analisaEnquanto();
            case "sleia" -> analisaLeia();
            case "sescreva" -> analisaEscreva();
            default -> analisaComandos();
        }
    }

    private void analisaAtribChprocedimento() {
        token = lexical.analyze();
        if (token.getSimbolo().equals("satribuicao")){
            analisaAtribuicao();
        } else {
            chamaProcedimento();
        }
    }

    private void analisaLeia() {
        token = lexical.analyze();
        if(token.getSimbolo().equals("sabreparenteses")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
               // if(pesquisaDeclvarTabela(token.getLexema())){
                    token = lexical.analyze();
                    if(token.getSimbolo().equals("sfechaparenteses")){
                        token = lexical.analyze();
                    } else {
                        //TODO erro
                    }
               // } else{TODO erro}
            } else {
                //TODO erro
            }
        } else {
            //TODO erro
        }
    }

    private void analisaEscreva() {
        token = lexical.analyze();
        if (token.getSimbolo().equals("sabreparenteses")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
                // if(pesquisaDeclvarfuncTabela(token.getLexema())){
                token = lexical.analyze();
                if(token.getSimbolo().equals("sfechaparenteses")){
                    token = lexical.analyze();
                } else {
                    //TODO erro
                }
                // } else{TODO erro}
            } else {
                //TODO erro
            }
        } else {
            //TODO erro
        }
    }

    private void analisaEnquanto() {
        //TODO geracao de código
        token = lexical.analyze();
        analisaExpressao();

        if (token.getSimbolo().equals("sfaca")) {
            //TODO geracao de código
            token = lexical.analyze();
            analisaComandoSimples();
            //TODO geracao de código
        } else {
            //TODO erro
        }
    }

    private void analisaSe() {
        token = lexical.analyze();
        analisaExpressao();

        if (token.getSimbolo().equals("sentao")) {
            token = lexical.analyze();
            analisaComandoSimples();
            if (token.getSimbolo().equals("ssenao")){
                token = lexical.analyze();
                analisaComandoSimples();
            }
        } else {
            //TODO erro
        }
    }

    private void analisaSubrotinas() {

    }

}

