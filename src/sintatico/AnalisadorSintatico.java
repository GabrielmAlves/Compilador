package sintatico;

import lexical.LexicalAnalyzer;
import lexical.Token;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AnalisadorSintatico {

    private static final String PATH ="C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\arquivos\\testes\\aa.txt";
    private static final String PATH_CODIGO = "C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\arquivos\\obj\\cod.obj";
    private final File fileCod = new File(PATH_CODIGO);
    private LexicalAnalyzer lexical;
    private Token token;
    private Deque<TabelaSimbolos> tabelaSimbolos = new ArrayDeque<>();
    private Deque<PosFixa> pilhaPos = new ArrayDeque<>();
    private List<PosFixa> saida = new ArrayList<>();
    private int rotulo;
    private List<Integer> memoria = new ArrayList<>();
    private int s;
    private int m = 1;

    public void analisa() throws Exception {

        File file = new File(PATH);
        lexical = new LexicalAnalyzer(file);
        rotulo = 1;
        token = lexical.analyze();

        if(token.getSimbolo().equals("sprograma")){
            gera(-1,"START","","");
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
                            throw new Exception("Arquivo terminou de forma incorreta");
                        }

                    } else {
                        // TODO erro
                        throw new Exception("Experava . mas obtive " + token.getLexema());
                    }
                } else {
                    // TODO erro
                    throw new Exception("Experava ; mas obtive " + token.getLexema());
                }

            } else {
                // TODO erro
                throw new Exception("Programa sem nome");
            }
        } else {
            // TODO erro
            throw new Exception("Programa não iniciado");
        }
    }

    private void analisaBloco() throws Exception {
        token = lexical.analyze();
        analisaEtVariaveis();
        analisaSubrotinas();
        analisaComandos();
    }

    private void analisaEtVariaveis() throws Exception {
        if (token.getSimbolo().equals("svar")) {
            token = lexical.analyze();
            if (token.getSimbolo().equals("sidentificador")) {
                while (token.getSimbolo().equals("sidentificador")) {
                    analisaVariaveis();
                    if (token.getSimbolo().equals("spontovirgula")) {
                        token = lexical.analyze();
                    } else {
                        //TODO erro
                        throw new Exception("Esperava ; mas obtive " + token.getLexema());
                    }
                }
            } else {
                //TODO erro
                throw new Exception("Variavel sem nome");
            }
        }

    }

    private void analisaVariaveis() throws Exception {
        int contador = 0;
        do{
            if (token.getSimbolo().equals("sidentificador")) {
                // semantico
                if(!pesquisaDuplicidade(token.getLexema())) {
                    TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.VARIAVEL,false,String.valueOf(m+contador));
                    tabelaSimbolos.push(simbolo);
                    contador++;

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
                        throw new Exception("Esperava , ou : mas obtive " + token.getLexema());
                    }
                } else {
                    //TODO erro
                    throw new Exception("Variavel duplicada");
                }
            } else {
                //TODO erro
                throw new Exception("Variavel sem nome");
            }
        } while (!token.getSimbolo().equals("sdoispontos"));
        token = lexical.analyze();
        gera(-1,"ALLOC",String.valueOf(m),String.valueOf(contador));
        m = m + contador;
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

    private void analisaTipo() throws Exception {
        if (!token.getSimbolo().equals("sinteiro") && !token.getSimbolo().equals("sbooleano")) {
            //TODO erro
            throw new Exception("Tipo " + token.getLexema() + " não permitido");
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

    private void analisaComandos() throws Exception {
        if (token.getSimbolo().equals("sinicio")) {
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
                    throw new Exception("Esperava ; mas obtive " + token.getLexema());
                }
            }
            token = lexical.analyze();
        } else {
            //TODO erro
            throw new Exception("Bloco de comandos não iniciado");
        }
    }

    private void analisaComandoSimples() throws Exception {
        switch (token.getSimbolo()) {
            case "sidentificador" -> analisaAtribChprocedimento();
            case "sse" -> analisaSe();
            case "senquanto" -> analisaEnquanto();
            case "sleia" -> analisaLeia();
            case "sescreva" -> analisaEscreva();
            default -> analisaComandos();
        }
    }

    private void analisaAtribChprocedimento() throws Exception {
        TabelaSimbolos simbolo = pesquisaTabela(token.getLexema());
        token = lexical.analyze();
        if (token.getSimbolo().equals("satribuicao")){
            token = lexical.analyze();
            analisaExpressao();
            desempilhaFimPos();
            geraExpressao();
            assert simbolo != null;
            gera(-1, "STR", simbolo.getEndMemoria(),"");
        } else {
            chamadaProcedimento();
        }
    }

    private void desempilhaFimPos() {
        desempilhaPos();
        for (PosFixa p : saida) {
            System.out.print(p.getLexema() + ' ');
        }
        // analisaTipoExpressao();
//        saida = new ArrayList<>();
    }

    private void chamadaProcedimento() {
        // TODO não sei
    }

    private void analisaLeia() throws Exception {
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
                        throw new Exception("Esperava ) mas obtive " + token.getLexema());
                    }
                } else {
                    //TODO erro
                    throw new Exception("Variavel " + token.getLexema() + "  declarada");
                }
            } else {
                //TODO erro
                throw new Exception("Variavel " + token.getLexema() + "  não declarada");
            }
        } else {
            //TODO erro
            throw new Exception("Esperava ( mas obtive " + token.getLexema());
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

    private void analisaEscreva() throws Exception {
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
                        throw new Exception("Esperava ) mas obtive " + token.getLexema());
                    }
                } else {
                    //TODO erro
                    throw new Exception("Variavel " + token.getLexema() +  " não declarada");
                }
            } else {
                //TODO erro
                throw new Exception("Variavel " + token.getLexema() +  " não declarada");
            }
        } else {
            //TODO erro
            throw new Exception("Esperava ( mas obtive " + token.getLexema());
        }
    }

    private void analisaEnquanto() throws Exception {
        //TODO geracao de código
        int auxrot1, auxrot2;
        auxrot1 = rotulo;
        gera(rotulo,"NULL","","");
        rotulo = rotulo + 1;

        token = lexical.analyze();
        analisaExpressao();
        desempilhaFimPos();
        geraExpressao();

        if (token.getSimbolo().equals("sfaca")) {
            auxrot2 = rotulo;
            gera(-1, "JMPF", String.valueOf(rotulo),"");
            rotulo = rotulo + 1;
            token = lexical.analyze();
            analisaComandoSimples();
            gera(-1,"JMP", String.valueOf(auxrot1),"");
            gera(auxrot2,"NULL","","");
        } else {
            //TODO erro
            throw new Exception("Loop não iniciado");
        }
    }

    private void analisaSe() throws Exception {
        token = lexical.analyze();
        analisaExpressao();
        desempilhaFimPos();
        geraExpressao();

        int auxrot = rotulo;
        int auxrot2 = 0;
        gera(-1, "JMPF", String.valueOf(rotulo),"");
        rotulo++;

        if (token.getSimbolo().equals("sentao")) {
            token = lexical.analyze();
            analisaComandoSimples();

            auxrot2 = rotulo;
            gera(-1, "JMP", String.valueOf(rotulo),"");
            rotulo++;

            if (token.getSimbolo().equals("ssenao")){
                gera(auxrot,"NULL","","");
                token = lexical.analyze();
                analisaComandoSimples();

                gera(auxrot2,"NULL","","");
            }
        } else {
            //TODO erro
            throw new Exception("Esperava \"entao\" mas obtive " + token.getLexema());
        }
    }

    private void analisaExpressao() throws Exception {
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

    private void analisaExpressaoSimples() throws Exception {
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

    private void analisaTermo() throws Exception {
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

    private void analisaFator() throws Exception {
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
                throw new Exception("Variavel " + token.getLexema() + " nao declarada");
            }
        } else if (token.getSimbolo().equals("snumero")) {
            PosFixa pos = new PosFixa(token.getLexema(), Tipo.CONSTANTE);
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
                throw new Exception("Esperava ) mas obtive " + token.getLexema());
            }
        } else if (token.getLexema().equals("verdadeiro") || token.getLexema().equals("falso")) {
            token = lexical.analyze();
        } else {
            // TODO erro
            throw new Exception("Fator não identificado");
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

    private void analisaSubrotinas() throws Exception {

        // geracao de código
        int auxrot = 0, flag;
        flag = 0;
        if(token.getSimbolo().equals("sprocedimento") || token.getSimbolo().equals("sfuncao")){
            auxrot = rotulo;
            gera(-1,"JMP", String.valueOf(rotulo),"");
            rotulo++;
            flag = 1;

        }
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
                throw new Exception("Esperava ; mas obtive " + token.getLexema());
            }
        }

        // geracao de código
        if (flag == 1) {
            gera(auxrot,"NULL","","");
        }
    }

    private void analisaDeclaracaoProcedimento() throws Exception {
        token = lexical.analyze();
        //semantico
        if (token.getSimbolo().equals("sidentificador")) {
            // semantico
            if(!pesquisaDeclFuncProcTabela(token.getLexema())) {
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.PROCEDIMENTO, true, "L" + rotulo);
                tabelaSimbolos.push(simbolo);
                gera(rotulo,"NULL","","");
                rotulo = rotulo + 1;

                // TODO geracao de código

                token = lexical.analyze();
                if (token.getSimbolo().equals("spontovirgula")) {
                    analisaBloco();
                } else {
                    // TODO erro
                    throw new Exception("Esperava ; mas obtive " + token.getLexema());
                }
            } else {
                // TODO erro
                throw new Exception("Variavel " + token.getLexema() + " nao declarada");
            }
        } else {
            // TODO erro
            throw new Exception("Variavel " + token.getLexema() + " nao declarada");
        }

        desempilha();
    }

    private void desempilha() {
        for (TabelaSimbolos simbolo : tabelaSimbolos) {
            if(simbolo.getEscopo()) {
                simbolo.setEscopo(false);
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

    private void analisaDeclaracaoFuncao() throws Exception {
        token = lexical.analyze();

        if (token.getSimbolo().equals("sidentificador")) {
            // semantico
            if(!pesquisaDeclFuncProcTabela(token.getLexema())) {
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.FUNCAO, true, String.valueOf(rotulo));

                gera(rotulo,"NULL","","");
                rotulo = rotulo + 1;

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
                        throw new Exception("Tipo " + token.getLexema() + " nao permitido");
                    }
                } else {
                    // TODO erro
                    throw new Exception("Esperava : mas obtive " + token.getLexema());
                }
            } else {
                // TODO erro
                throw new Exception("Variavel " + token.getLexema() + " nao declarada");
            }
        } else {
            // TODO erro
            throw new Exception("Variavel " + token.getLexema() + " nao declarada");
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
                } else if (saida.get(i).getTipo() == Tipo.UNARIO) {
                    n1 = saida.get(i-1);
                    if (n1.getTipo() != Tipo.VARIAVEL_INTEIRA) {
                        return false;
                    }
                    alteraSaida(i,Tipo.UNARIO);
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
        List<PosFixa> auxSaida = new ArrayList<>();
        for (int i=0 ; i<saida.toArray().length; i++) {
            if(tipo == Tipo.UNARIO) {
                if (i == y - 1) {
                    PosFixa p = new PosFixa("I", Tipo.VARIAVEL_INTEIRA);
                    auxSaida.add(p);
                    i = y + 1;
                }
            } else if(i == y-2) {
                if (tipo == Tipo.ARITMETICO) {
                    PosFixa p = new PosFixa("I",Tipo.VARIAVEL_INTEIRA);
                    auxSaida.add(p);
                }else {
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

    private void gera(int r, String instrucao, String var1, String var2) {
        try {
            FileWriter fileWriter = new FileWriter(fileCod, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String rot = "";
            if (r != -1) {
                rot = "L" + r;
            }

            int num1;
            int num2;
            int result;

            if (!Objects.equals(var2, "")) {
                bufferedWriter.write(rot + "\t" + instrucao + " " + var1 + "," + var2 + "\n");
            } else {
                bufferedWriter.write(rot + "\t" + instrucao + " " + var1 + "\n");
            }

            bufferedWriter.close();
            /*
            switch (instrucao) {
                case "LDC":
                    s++;
                    memoria.set(s, Integer.valueOf(var1));
                    break;
                case "LDV":
                    s++;
                    memoria.set(s, memoria.get(Integer.parseInt(var1)));
                    break;
                case "ADD":
                    num1 = memoria.get(s - 1);
                    num2 = memoria.get(s);
                    result = num1 + num2;
                    memoria.set(s - 1, result);
                    s--;
                    break;
                case "SUB":
                    num1 = memoria.get(s - 1);
                    num2 = memoria.get(s);
                    result = num1 - num2;
                    memoria.set(s - 1, result);
                    s--;
                    break;
                case "MULT":
                    num1 = memoria.get(s - 1);
                    num2 = memoria.get(s);
                    result = num1 * num2;
                    memoria.set(s - 1, result);
                    s--;
                    break;
                case "DIVI":
                    num1 = memoria.get(s - 1);
                    num2 = memoria.get(s);
                    result = num1 / num2;
                    memoria.set(s - 1, result);
                    s--;
                    break;
                case "INV":
                    memoria.set(s, -memoria.get(s));
                    break;
                case "AND":
                    if (memoria.get(s - 1) == 1 && memoria.get(s) == 1) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "OR":
                    if (memoria.get(s - 1) == 1 || memoria.get(s) == 1) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "NEG":
                    memoria.set(s, 1 - memoria.get(s));
                    break;
                case "CME":
                    if (memoria.get(s - 1) < memoria.get(s - 1)) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "CMA":
                    if (memoria.get(s - 1) > memoria.get(s - 1)) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "CEQ":
                    if (memoria.get(s - 1) == memoria.get(s - 1)) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "CDIF":
                    if (memoria.get(s - 1) != memoria.get(s - 1)) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "CMEQ":
                    if (memoria.get(s - 1) <= memoria.get(s - 1)) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "CMAQ":
                    if (memoria.get(s - 1) >= memoria.get(s - 1)) {
                        memoria.set(s - 1, 1);
                    } else {
                        memoria.set(s - 1, 0);
                    }
                    s--;
                    break;
                case "STR":
                    memoria.set(Integer.parseInt(var1), memoria.get(s));
                    s--;
                    break;
                case "JMP":
                    // i:=p
                    break;
                case "JMPF":
                    if (memoria.get(s) == 0) {
                        // i:=p
                    } else {
                        // i++
                    }
                    s--;
                    break;
                case "RD":
                    s++;
                    memoria.set(s, Integer.parseInt(var1));
                    break;
                case "PRN":
                    System.out.println(memoria.get(s));
                    break;
                case "START":
                    s = -1;
                    break;
                case "HLT":
                    // para exec da maquina virtual
                    break;
                case "ALLOC":
                    for (int k = 0; k < Integer.parseInt(var2); k++) {
                        s++;
                        memoria.set(s, memoria.get(Integer.parseInt(var1) + k));
                    }
                    break;
                case "DALLOC":
                    for (int k = Integer.parseInt(var2) - 1; k >= 0; k--) {
                        memoria.set(Integer.parseInt(var1) + k, memoria.get(s));
                        s--;
                    }
                    break;
                case "CALL":
                    s++;
                    // M[s]:=i+1
                    // i:=p
                    break;
                case "RETURN":
                    // i:=M[s]
                    s--;
                    break;
                default:
                    System.out.println("Instrução não encontrada");
            } */
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void geraExpressao() {
        System.out.println(saida);

        for(PosFixa s : saida) {
            if (s.getTipo() == Tipo.VARIAVEL_BOOLEANA || s.getTipo() == Tipo.VARIAVEL_INTEIRA) {
                TabelaSimbolos simbolo = pesquisaTabela(s.getLexema());
                assert simbolo != null;
                gera(-1, "LDV",simbolo.getEndMemoria(),"");
            } else if (s.getTipo() == Tipo.CONSTANTE) {
                gera(-1, "LDC",s.getLexema(),"");
            } else if (s.getTipo() == Tipo.ARITMETICO) {
                switch (s.getLexema()) {
                    case "+" -> gera(-1, "ADD", "", "");
                    case "-" -> gera(-1, "SUB", "", "");
                    case "*" -> gera(-1, "MULT", "", "");
                    case "div" -> gera(-1, "DIVI", "", "");
                }
            } else if (s.getTipo() == Tipo.RELACIONAL) {
                switch (s.getLexema()) {
                    case ">" -> gera(-1, "CMA", "", "");
                    case "<" -> gera(-1, "CME", "", "");
                    case "=" -> gera(-1, "CEQ", "", "");
                    case "!=" -> gera(-1, "CDIF", "", "");
                    case ">=" -> gera(-1, "CMEQ", "", "");
                    case "<=" -> gera(-1, "CMAQ", "", "");
                }
            } else if (s.getTipo() == Tipo.LOGICO) {
                switch (s.getLexema()) {
                    case "e" -> gera(-1, "AND", "", "");
                    case "ou" -> gera(-1, "OR", "", "");
                    case "nao" -> gera(-1, "NEG", "", "");
                }
            }
        }
    }

}

