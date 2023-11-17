package sintatico;

import lexical.LexicalAnalyzer;
import lexical.Token;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AnalisadorSintatico {

    private static final String PATH ="C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\arquivos\\aa.txt";
    private LexicalAnalyzer lexical;
    private Token token;
    private Deque<TabelaSimbolos> tabelaSimbolos = new ArrayDeque<>();
    private Deque<PosFixa> pilhaPos = new ArrayDeque<>();
    private List<PosFixa> saida = new ArrayList<>();
    private List<PosFixa> auxSaida = new ArrayList<>();

    public void analisa(){

        File file = new File(PATH);
        lexical = new LexicalAnalyzer(file);

        token = lexical.analyze();

        if(token.getSimbolo().equals("sprograma")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")) {
                // semantico
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.PROGRAMA,false,"");
                tabelaSimbolos.push(simbolo);

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

                    } else {
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
                // semantico
                if(!pesquisaDuplicidade(token.getLexema())) {
                    TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.VARIAVEL,false,"");
                    tabelaSimbolos.push(simbolo);

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
                    System.out.println("Erro duplicidade tabela de simbolos");
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

    private boolean pesquisaDuplicidade(String lexema) {
        for(TabelaSimbolos simbolo : tabelaSimbolos) {
            if(simbolo.getEscopo()) {
                return false;
            }
            if (simbolo.getLexema().equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    private void analisaTipo() {
        if (!token.getSimbolo().equals("sinteiro") && !token.getSimbolo().equals("sbooleano")) {
            //TODO erro
            System.out.println("Erro 11");
            return;
        }
        colocaTipoTabela(token.getLexema());
        token = lexical.analyze();
    }

    private void colocaTipoTabela(String lexema) {
        for(TabelaSimbolos simbolo : tabelaSimbolos) {
            if(simbolo.getTipo().equals(Tipo.VARIAVEL)) {
                if(lexema.equals("inteiro")) {
                    simbolo.setTipo(Tipo.VARIAVEL_INTEIRA);
                } else {
                    simbolo.setTipo(Tipo.VARIAVEL_BOOLEANA);
                }
            }
        }

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
            desempilhaFimPos();
        } else {
            chamadaProcedimento();
        }
    }

    private void desempilhaFimPos() {
        desempilhaPos();
        for (PosFixa p : saida) {
            System.out.print(p.getLexema() + ' ');
        }
        analisaTipoExpressao();
//        saida = new ArrayList<>();
    }

    private void chamadaProcedimento() {
        // TODO não sei
    }

    private void analisaLeia() {
        token = lexical.analyze();
        if(token.getSimbolo().equals("sabreparenteses")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
                // semantico
                if (pesquisaDeclVarTabela(token.getLexema())) {
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
                    System.out.println("Erro não achou o identificador na tabela");
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

    private boolean pesquisaDeclVarTabela(String lexema) {
        for(TabelaSimbolos simbolos : tabelaSimbolos) {
            if (simbolos.getLexema().equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    private void analisaEscreva() {
        token = lexical.analyze();
        if (token.getSimbolo().equals("sabreparenteses")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
                // semantico
                if (pesquisaDeclVarTabela(token.getLexema())) {
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
                    System.out.println("Erro não achou o identificador na tabela");
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
        desempilhaFimPos();

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
        desempilhaFimPos();

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

            PosFixa pos = new PosFixa(token.getLexema(),Tipo.RELACIONAL,4);
            verificaPrecedencia(pos);

            token = lexical.analyze();
            analisaExpressaoSimples();
        }
    }

    private void desempilhaPos() {
        for (PosFixa p : pilhaPos) {
            saida.add(p);
            pilhaPos.pop();
        }
    }

    private void analisaExpressaoSimples() {
        if (token.getSimbolo().equals("smais") || token.getSimbolo().equals("smenos")) {
            PosFixa pos = new PosFixa(token.getLexema(),Tipo.UNARIO,7);
            verificaPrecedencia(pos);
            token = lexical.analyze();
        }
        analisaTermo();
        while (token.getSimbolo().equals("smais") || token.getSimbolo().equals("smenos") || token.getSimbolo().equals("sou")) {
            if(token.getSimbolo().equals("sou")) {
                PosFixa pos = new PosFixa(token.getLexema(),Tipo.LOGICO,1);
                verificaPrecedencia(pos);
            } else {
                PosFixa pos = new PosFixa(token.getLexema(),Tipo.ARITMETICO,5);
                verificaPrecedencia(pos);
            }
            token = lexical.analyze();
            analisaTermo();
        }
    }

    private void analisaTermo() {
        analisaFator();
        while (token.getSimbolo().equals("smult") || token.getSimbolo().equals("sdiv") || token.getSimbolo().equals("se")) {
            if(token.getSimbolo().equals("se")) {
                PosFixa pos = new PosFixa(token.getLexema(),Tipo.LOGICO,2);
                verificaPrecedencia(pos);
            } else {
                PosFixa pos = new PosFixa(token.getLexema(),Tipo.ARITMETICO,6);
                verificaPrecedencia(pos);
            }
            token = lexical.analyze();
            analisaFator();
        }
    }

    private void analisaFator() {
        if(token.getSimbolo().equals("sidentificador")) {
            // semantico
            TabelaSimbolos simbolo = pesquisaTabela(token.getLexema());
            if(simbolo != null) {
                if(simbolo.getTipo() == Tipo.FUNCAO_BOOLEANA || simbolo.getTipo() == Tipo.FUNCAO_INTEIRA) {
                    chamadaFuncao();
                } else {
                    PosFixa pos = new PosFixa(simbolo.getLexema(), simbolo.getTipo());
                    saida.add(pos);
                    token = lexical.analyze();
                }
            } else {
                // TODO erro
                System.out.println("Erro não achou na tabela de simbolos");
                return;
            }
        } else if (token.getSimbolo().equals("snumero")) {
            PosFixa pos = new PosFixa(token.getLexema(), Tipo.VARIAVEL_INTEIRA);
            saida.add(pos);
            token = lexical.analyze();
        } else if (token.getSimbolo().equals("snao")) {
            PosFixa pos = new PosFixa(token.getLexema(),Tipo.LOGICO,3);
            verificaPrecedencia(pos);
            token = lexical.analyze();
            analisaFator();
        } else if (token.getSimbolo().equals("sabreparenteses")) {
            PosFixa pos = new PosFixa(token.getLexema(), 0);
            pilhaPos.push(pos);
            token = lexical.analyze();
            analisaExpressao();
            if (token.getSimbolo().equals("sfechaparenteses")) {
                desempilhaAteParenteses();
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

    private void desempilhaAteParenteses() {
        for(PosFixa s : pilhaPos) {
            if(s.getLexema().equals("(")) {
                pilhaPos.pop();
                break;
            }
            saida.add(s);
            pilhaPos.pop();
        }
    }

    public void verificaPrecedencia(PosFixa operador) {
        if(pilhaPos.peek() == null ) {
            pilhaPos.push(operador);
            return;
        }

        if(operador.getPrecedencia() > pilhaPos.peek().getPrecedencia()) {
            pilhaPos.push(operador);
            return;
        }

        for (PosFixa p : pilhaPos) {
            if(operador.getPrecedencia() <= p.getPrecedencia()) {
                saida.add(p);
                pilhaPos.pop();
            } else {
                pilhaPos.push(operador);
                return;
            }
        }

        pilhaPos.push(operador);
    }

    private TabelaSimbolos pesquisaTabela(String lexema) {
        for(TabelaSimbolos simbolo : tabelaSimbolos) {
            if (simbolo.getLexema().equals(lexema)) {
                return simbolo;
            }
        }
        return null;
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
        //semantico
        if (token.getSimbolo().equals("sidentificador")) {
            // semantico
            if(!pesquisaDeclFuncProcTabela(token.getLexema())) {
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.PROCEDIMENTO, true,"");
                tabelaSimbolos.push(simbolo);

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
                System.out.println("Erro não achou procedimento na tabela de simbolo");
                return;
            }
        } else {
            // TODO erro
            System.out.println("Erro 26");
            return;
        }

        desempilha();
    }

    private void desempilha() {
        for (TabelaSimbolos simbolo : tabelaSimbolos) {
            if(simbolo.getEscopo()) {
//                tabelaSimbolos.pop();
                break;
            }
            tabelaSimbolos.pop();
        }
    }

    private boolean pesquisaDeclFuncProcTabela(String lexema) {
        for(TabelaSimbolos simbolo : tabelaSimbolos) {
            if(simbolo.getLexema().equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    private void analisaDeclaracaoFuncao() {
        token = lexical.analyze();

        if (token.getSimbolo().equals("sidentificador")) {
            // semantico
            if(!pesquisaDeclFuncProcTabela(token.getLexema())) {
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.FUNCAO, true,"");

                token = lexical.analyze();
                if(token.getSimbolo().equals("sdoispontos")) {
                    token = lexical.analyze();
                    if (token.getSimbolo().equals("sinteiro") || token.getSimbolo().equals("sbooleano")) {
                        // semantico
                        if(token.getSimbolo().equals("sinteiro")) {
                            simbolo.setTipo(Tipo.FUNCAO_INTEIRA);
                        } else {
                            simbolo.setTipo(Tipo.FUNCAO_BOOLEANA);
                        }

                        tabelaSimbolos.push(simbolo);
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
                System.out.println("Erro função duplicada na tabela de simbolos");
                return;
            }
        } else {
            // TODO erro
            System.out.println("Erro 29");
            return;
        }

        desempilha();
    }

    private boolean analisaTipoExpressao() {
        PosFixa n1,n2;
        while (saida.toArray().length>1) {
            for (int i=0; i<saida.toArray().length; i++) {
                if (saida.get(i).getTipo() == Tipo.ARITMETICO || saida.get(i).getTipo() == Tipo.RELACIONAL) {
                    n1 = saida.get(i-2);
                    n2 = saida.get(i-1);
                    if(n1.getTipo() != Tipo.VARIAVEL_INTEIRA && n2.getTipo() != Tipo.VARIAVEL_INTEIRA) {
                        return false;
                    }
                    if (saida.get(i).getTipo() == Tipo.ARITMETICO) {
                        alteraSaida(i,Tipo.ARITMETICO);
                    } else {
                        alteraSaida(i,Tipo.RELACIONAL);
                    }
                    break;
                } else if (saida.get(i).getTipo() == Tipo.LOGICO) {
                    n1 = saida.get(i-2);
                    n2 = saida.get(i-1);
                    if (n1.getTipo() != Tipo.VARIAVEL_BOOLEANA && n2.getTipo() != Tipo.VARIAVEL_BOOLEANA) {
                        return false;
                    }
                    alteraSaida(i,Tipo.LOGICO);
                    break;
                }
            }
            System.out.println();
            for(PosFixa p : saida) {
                System.out.print(p.getLexema() + " ");
            }
        }

        return true;
    }

    private void alteraSaida(int y, Tipo tipo) {
        auxSaida = new ArrayList<>();
        for (int i=0 ; i<saida.toArray().length; i++) {
            if(i == y-2) {
                if (tipo == Tipo.ARITMETICO) {
                    PosFixa p = new PosFixa("I",Tipo.VARIAVEL_INTEIRA);
                    auxSaida.add(p);
                } else {
                    PosFixa p = new PosFixa("B",Tipo.VARIAVEL_BOOLEANA);
                    auxSaida.add(p);
                }
                i = y+1;
            }
            if(i<saida.toArray().length) {
                auxSaida.add(saida.get(i));
            }
        }
        saida = auxSaida;
    }
}

