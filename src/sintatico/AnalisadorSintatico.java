package sintatico;

import lexical.LexicalAnalyzer;
import lexical.Token;

import java.io.File;

public class AnalisadorSintatico {

    private static final String PATH ="D:\\Pucc\\Compiladores\\Compilador\\src\\arquivos\\sintatico\\sint18.txt";
    private LexicalAnalyzer lexical;
    private Token token;
    public void analisa(){

        File file = new File(PATH);
        lexical = new LexicalAnalyzer(file);

        token = lexical.analyze();

        if(token.getSimbolo().equals("sprograma")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
                // TODO semantico
                token = lexical.analyze();
                if(token.getSimbolo().equals("spontovirgula")){
                    analisaBloco();
                    if(token.getSimbolo().equals("sponto")){
                        token = lexical.analyze();
                        if(token == null) {
                            // TODO sucesso
                            System.out.println("Sucesso!");
                        } else {
                            // TODO erro
                            System.out.println("Erro 1");
                            return;
                        }

                    }else {
                        // TODO erro
                        System.out.println("Erro 2");
                        return;
                    }
                }else {
                    // TODO erro
                    System.out.println("Erro 3");
                    return;
                }

            }else {
                // TODO erro
                System.out.println("Erro 4");
                return;
            }
        }else {
            // TODO erro
            System.out.println("Erro 5");
            return;
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
                        System.out.println("Erro 6");
                        return;
                    }
                }
            } else {
                //TODO erro
                System.out.println("Erro 7");
                return;
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
                            System.out.println("Erro 8");
                            return;
                        }
                    }
                } else {
                    //TODO erro
                    System.out.println("Erro 9");
                    return;
                }
            } else {
                //TODO erro
                System.out.println("Erro 10");
                return;
            }
        } while (!token.getSimbolo().equals("sdoispontos"));
        token = lexical.analyze();
        analisaTipo();
    }

    private void analisaTipo() {
        if (!token.getSimbolo().equals("sinteiro") && !token.getSimbolo().equals("sbooleano")) {
            //TODO erro
            System.out.println("Erro 11");
            return;

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
                if (token.getSimbolo().equals("spontovirgula")){
                    token = lexical.analyze();
                    if(!token.getSimbolo().equals("sfim")){
                        analisaComandoSimples();
                    }
                } else {
                    //TODO erro
                    System.out.println("Erro 12");
                    return;
                }
            }
            token = lexical.analyze();
        } else{
            //TODO erro
            System.out.println("Erro 13");
            return;
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
            token = lexical.analyze();
            analisaExpressao();
        } else {
            chamadaProcedimento();
        }
    }

    private void chamadaProcedimento() {
        // TODO não sei
    }

    private void analisaLeia() {
        token = lexical.analyze();
        if(token.getSimbolo().equals("sabreparenteses")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
                token = lexical.analyze();
                if(token.getSimbolo().equals("sfechaparenteses")){
                    token = lexical.analyze();
                } else {
                    //TODO erro
                    System.out.println("Erro 14");
                    return;
                }
            } else {
                //TODO erro
                System.out.println("Erro 15");
                return;
            }
        } else {
            //TODO erro
            System.out.println("Erro 16");
            return;
        }
    }

    private void analisaEscreva() {
        token = lexical.analyze();
        if (token.getSimbolo().equals("sabreparenteses")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
                token = lexical.analyze();
                if(token.getSimbolo().equals("sfechaparenteses")){
                    token = lexical.analyze();
                } else {
                    //TODO erro
                    System.out.println("Erro 17");
                    return;
                }
            } else {
                //TODO erro
                System.out.println("Erro 18");
                return;
            }
        } else {
            //TODO erro
            System.out.println("Erro 19");
            return;
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
            System.out.println("Erro 20");
            return;
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
            System.out.println("Erro 21");
            return;
        }
    }

    private void analisaExpressao() {
        analisaExpressaoSimples();
        if (token.getSimbolo().equals("smaior") || token.getSimbolo().equals("smaiorig") ||
                token.getSimbolo().equals("sig") || token.getSimbolo().equals("smenor") ||
                token.getSimbolo().equals("smenorig") || token.getSimbolo().equals("sdif")) {

            token = lexical.analyze();
            analisaExpressaoSimples();
        }
    }

    private void analisaExpressaoSimples() {
        if (token.getSimbolo().equals("smais") || token.getSimbolo().equals("smenos")) {
            token = lexical.analyze();
        }
        analisaTermo();
        while (token.getSimbolo().equals("smais") || token.getSimbolo().equals("smenos") || token.getSimbolo().equals("sou")) {
            token = lexical.analyze();
            analisaTermo();
        }
    }

    private void analisaTermo() {
        analisaFator();
        while (token.getSimbolo().equals("smult") || token.getSimbolo().equals("sdiv") || token.getSimbolo().equals("se")) {
            token = lexical.analyze();
            analisaFator();
        }
    }

    private void analisaFator() {
        if(token.getSimbolo().equals("sidentificador")) {
            // TODO semantico
            chamadaFuncao();
        } else if (token.getSimbolo().equals("snumero")) {
            token = lexical.analyze();
        } else if (token.getSimbolo().equals("snao")) {
            token = lexical.analyze();
            analisaFator();
        } else if (token.getSimbolo().equals("sabreparenteses")) {
            token = lexical.analyze();
            analisaExpressao();
            if (token.getSimbolo().equals("sfechaparenteses")) {
                token = lexical.analyze();
            } else {
                // TODO erro
                System.out.println("Erro 22");
                return;
            }
        } else if (token.getLexema().equals("verdadeiro") || token.getLexema().equals("falso")) {
            token = lexical.analyze();
        } else {
            // TODO erro
            System.out.println("Erro 23");
            return;
        }
    }

    private void chamadaFuncao() {
        token = lexical.analyze();
        // TODO não sei
    }

    private void analisaSubrotinas() {
        //TODO geracao de código

        while (token.getSimbolo().equals("sprocedimento") || token.getSimbolo().equals("sfuncao")) {
            if (token.getSimbolo().equals("sprocedimento")) {
                analisaDeclaracaoProcedimento();
            } else {
                analisaDeclaracaoFuncao();
            }

            if (token.getSimbolo().equals("spontovirgula")) {
                token = lexical.analyze();
            } else {
                // TODO erro
                System.out.println("Erro 24");
                return;
            }
        }

        //TODO geracao de código
    }

    private void analisaDeclaracaoProcedimento() {
        token = lexical.analyze();

        if (token.getSimbolo().equals("sidentificador")) {
            // TODO semantico
            // TODO geracao de código

            token = lexical.analyze();
            if (token.getSimbolo().equals("spontovirgula")) {
                analisaBloco();
            } else {
                // TODO erro
                System.out.println("Erro 25");
                return;
            }
        } else {
            // TODO erro
            System.out.println("Erro 26");
            return;
        }
    }

    private void analisaDeclaracaoFuncao() {
        token = lexical.analyze();

        if (token.getSimbolo().equals("sidentificador")) {
            // TODO semantico

            token = lexical.analyze();
            if(token.getSimbolo().equals("sdoispontos")) {
                token = lexical.analyze();
                if (token.getSimbolo().equals("sinteiro") || token.getSimbolo().equals("sbooleano")) {
                    // TODO semantico

                    token = lexical.analyze();
                    if (token.getSimbolo().equals("spontovirgula")) {
                        analisaBloco();
                    }
                } else {
                    // TODO erro
                    System.out.println("Erro 27");
                    return;
                }
            } else {
                // TODO erro
                System.out.println("Erro 28");
                return;
            }
        } else {
            // TODO erro
            System.out.println("Erro 29");
            return;
        }
        // TODO semantico
    }

}

