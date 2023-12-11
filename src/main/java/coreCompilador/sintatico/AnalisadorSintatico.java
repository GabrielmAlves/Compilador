package coreCompilador.sintatico;


import coreCompilador.lexical.LexicalAnalyzer;
import coreCompilador.lexical.Token;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class AnalisadorSintatico {

    private final String path;
    private static final String PATH_CODIGO = System.getProperty("user.dir") + "\\src\\main\\java\\coreCompilador\\arquivos\\obj\\cod.obj"; // caminho que gera o arquivo obj
    private final File fileCod = new File(PATH_CODIGO);
    private LexicalAnalyzer lexical;
    private Token token;
    private Deque<TabelaSimbolos> tabelaSimbolos = new ArrayDeque<>(); // pilha da tabela de simbolos
    private Deque<PosFixa> pilhaPos = new ArrayDeque<>(); // pilha para fazer a conversão da expressão pra pós fixa (pilha em que verificamos as precedencia dos operadores)
    private List<PosFixa> saida = new ArrayList<>(); // expressão final após fazer a conversão para pós fixa
    private List<PosFixa> copiaSaida = new ArrayList<>(); // uma lista auxiliar para fazer a validação to tipo da expressão (inteiro ou booleano)
    private int rotulo; // proximo rotulo disponivel
    private int m = 1; // proximo index livre na memoria

    public AnalisadorSintatico(String PATH) {
        this.path = PATH;
    }

    public void analisa() throws Exception {

        FileWriter fileWriter = new FileWriter(fileCod);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(""); // para sobrescrever o arquivo a cada novo arquivo compilado

        File file = new File(path);
        lexical = new LexicalAnalyzer(file);
        rotulo = 1; // seta o primeiro rotulo disponivel
        token = lexical.analyze(); // retorna o token a ser analisado

        if(token.getSimbolo().equals("sprograma")){
            gera(-1,"START","","");
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")) {
                // semantico
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.PROGRAMA,false,""); // cria nova estrutura para adicionar na tabela de simbolos
                tabelaSimbolos.push(simbolo); // adiciona na tabela de simbolos

                token = lexical.analyze();
                if(token.getSimbolo().equals("spontovirgula")){
                    analisaBloco();
                    if(token.getSimbolo().equals("sponto")) {
                        token = lexical.analyze();
                        if(token == null) { // chegou no fim de arquivo, faz o dalloc e hlt
                            int n = contaVariaveis(); // retorna quantas variaveis serão desalocadas
                            gera(-1,"DALLOC",String.valueOf(m-n),String.valueOf(n));
                            m = m - n;
                            gera(-1,"HLT","","");
                        } else {
                            throw new Exception("Arquivo terminou de forma incorreta");
                        }

                    } else {
                        throw new Exception("Experava . mas obtive " + token.getLexema());
                    }
                } else {
                    throw new Exception("Experava ; mas obtive " + token.getLexema());
                }

            } else {
                throw new Exception("Programa sem nome");
            }
        } else {
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
                        throw new Exception("Esperava ; mas obtive " + token.getLexema());
                    }
                }
            } else {
                throw new Exception("Variavel sem nome");
            }
        }

    }

    private void analisaVariaveis() throws Exception {
        int contador = 0; // contador para fazer a alocação das variaveis
        do{
            if (token.getSimbolo().equals("sidentificador")) {
                // semantico
                if(!pesquisaDuplicidade(token.getLexema())) { // verifica se essa variavel ja foi declarada no escopo
                    TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.VARIAVEL,false,String.valueOf(m+contador));
                    tabelaSimbolos.push(simbolo);
                    contador++;

                    token = lexical.analyze();
                    if (token.getSimbolo().equals("svirgula") || token.getSimbolo().equals("sdoispontos")) {
                        if (token.getSimbolo().equals("svirgula")) {
                            token = lexical.analyze();
                            if (token.getSimbolo().equals("sdoispontos")) {
                                throw new Exception("Esperava uma variavel mas obtive " + token.getLexema());
                            }
                        }
                    } else {
                        throw new Exception("Esperava , ou : mas obtive " + token.getLexema());
                    }
                } else {
                    throw new Exception("Variavel duplicada");
                }
            } else {
                throw new Exception("Variavel sem nome");
            }
        } while (!token.getSimbolo().equals("sdoispontos"));
        token = lexical.analyze();
        gera(-1,"ALLOC",String.valueOf(m),String.valueOf(contador));
        m = m + contador;
        analisaTipo();
    }

    // função para verificar se um determinada variavel ja foi declarada dentro do escopo
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
            throw new Exception("Tipo " + token.getLexema() + " não permitido");
        }
        colocaTipoTabela(token.getLexema());
        token = lexical.analyze();
    }

    // função para adicionar o tipo da variavel de acordo com seu lexema
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
                    throw new Exception("Esperava ; mas obtive " + token.getLexema());
                }
            }
            token = lexical.analyze();
        } else {
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
        assert simbolo != null;
        token = lexical.analyze();
        if (token.getSimbolo().equals("satribuicao")){
            token = lexical.analyze();
            analisaExpressao();
            desempilhaFimPos();

            if(!copiaSaida.isEmpty()) { // verifica o tipo de atribuicao
                if (simbolo.getTipo() == Tipo.FUNCAO_INTEIRA) {
                    if(copiaSaida.get(0).getTipo() != Tipo.VARIAVEL_INTEIRA) {
                        throw new Exception("Esperava uma expressão que retornasse inteiro");
                    }
                } else if (simbolo.getTipo() == Tipo.FUNCAO_BOOLEANA) {
                    if(copiaSaida.get(0).getTipo() != Tipo.VARIAVEL_BOOLEANA) {
                        throw new Exception("Esperava uma expressão que retornasse booleano");
                    }
                } else if (copiaSaida.get(0).getTipo() == Tipo.CONSTANTE) {
                    if(simbolo.getTipo() != Tipo.VARIAVEL_INTEIRA) {
                        throw new Exception("Esperava uma expressão que retornasse " + simbolo.getTipo());
                    }
                } else if(simbolo.getTipo() != copiaSaida.get(0).getTipo()) {
                    throw new Exception("Esperava uma expressão que retornasse " + simbolo.getTipo());
                }
            }

            geraExpressao();
            if(simbolo.getEndMemoria().charAt(0) == 'L') { // se for retorno de função, guarda o valor em 0
                gera(-1, "STR", "0","");
            } else {
                gera(-1, "STR", simbolo.getEndMemoria(),"");
            }
        } else {
            chamadaProcedimento(simbolo);
        }
    }

    // desempilha a pilha no final da conversão para pós fixa, caso haja algum operador la
    private void desempilhaFimPos() throws Exception {
        for (PosFixa p : pilhaPos) {
            saida.add(p);
            pilhaPos.pop();
        }
        copiaSaida = saida;
        analisaTipoExpressao();
    }

    private void chamadaProcedimento(TabelaSimbolos simbolo) {
        gera(-1, "CALL", simbolo.getEndMemoria(),"");
    }

    private void analisaLeia() throws Exception {
        token = lexical.analyze();
        if(token.getSimbolo().equals("sabreparenteses")){
            token = lexical.analyze();
            if(token.getSimbolo().equals("sidentificador")){
                // semantico
                if (pesquisaDeclVarTabela(token.getLexema())) { // verifica se a variavel foi declarada
                    TabelaSimbolos simbolo = pesquisaTabela(token.getLexema());
                    gera(-1,"RD","","");
                    assert simbolo != null;
                    gera(-1,"STR",simbolo.getEndMemoria(),"");

                    token = lexical.analyze();
                    if(token.getSimbolo().equals("sfechaparenteses")){
                        token = lexical.analyze();
                    } else {
                        throw new Exception("Esperava ) mas obtive " + token.getLexema());
                    }
                } else {
                    throw new Exception("Variavel " + token.getLexema() + "  declarada");
                }
            } else {
                throw new Exception("Variavel " + token.getLexema() + "  não declarada");
            }
        } else {
            throw new Exception("Esperava ( mas obtive " + token.getLexema());
        }
    }

    // função que procura se uma variavel foi declarada
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
                if (pesquisaDeclVarTabela(token.getLexema())) { // verifica se a variavel foi declarada
                    TabelaSimbolos simbolo = pesquisaTabela(token.getLexema());
                    assert simbolo != null;
                    if (simbolo.getEndMemoria().charAt(0) == 'L') { // verifica se é o retorno de uma função q sera escrito
                        gera(-1,"CALL",simbolo.getEndMemoria(),"");
                        gera(-1,"LDV","0","");
                    } else {
                        gera(-1,"LDV",simbolo.getEndMemoria(),"");
                    }
                    gera(-1,"PRN","","");

                    token = lexical.analyze();
                    if(token.getSimbolo().equals("sfechaparenteses")){
                        token = lexical.analyze();
                    } else {
                        throw new Exception("Esperava ) mas obtive " + token.getLexema());
                    }
                } else {
                    throw new Exception("Variavel " + token.getLexema() +  " não declarada");
                }
            } else {
                throw new Exception("Variavel " + token.getLexema() +  " não declarada");
            }
        } else {
            throw new Exception("Esperava ( mas obtive " + token.getLexema());
        }
    }

    private void analisaEnquanto() throws Exception {
        int auxrot1, auxrot2;
        auxrot1 = rotulo; // guarda o rotulo para fazer o loop
        gera(rotulo,"NULL","","");
        rotulo = rotulo + 1;

        token = lexical.analyze();
        analisaExpressao();
        desempilhaFimPos();

        if(!copiaSaida.isEmpty()) {
            if(!copiaSaida.get(0).getLexema().equals("B")) {
                throw new Exception("Esperava uma expressão que retornasse booleano");
            }
        }

        geraExpressao();

        if (token.getSimbolo().equals("sfaca")) {
            auxrot2 = rotulo; // guarda o rotulo para sair do loop
            gera(-1, "JMPF", "L" + rotulo,"");
            rotulo = rotulo + 1;

            token = lexical.analyze();
            analisaComandoSimples();

            gera(-1,"JMP", "L" + auxrot1,""); // continuar o loop
            gera(auxrot2,"NULL","",""); // sair do loop
        } else {
            throw new Exception("Loop não iniciado");
        }
    }

    private void analisaSe() throws Exception {
        token = lexical.analyze();
        analisaExpressao();
        desempilhaFimPos();

        if(!copiaSaida.isEmpty()) {
            if(!copiaSaida.get(0).getLexema().equals("B")) {
                throw new Exception("Esperava uma expressão que retornasse booleano");
            }
        }

        geraExpressao();

        int auxrot = rotulo; // guarda rotulo do senao
        int auxrot2;
        gera(-1, "JMPF", "L" + rotulo,""); // se for falso, executa o senao
        rotulo++;

        if (token.getSimbolo().equals("sentao")) {
            token = lexical.analyze();
            analisaComandoSimples();

            auxrot2 = rotulo; // guarda rotulo para não executar o senao
            gera(-1, "JMP", "L" + rotulo,""); // pula pra não executar o senao
            rotulo++;

            gera(auxrot,"NULL","","");
            if (token.getSimbolo().equals("ssenao")){
                token = lexical.analyze();
                analisaComandoSimples();

            }
            gera(auxrot2,"NULL","","");
        } else {
            throw new Exception("Esperava \"entao\" mas obtive " + token.getLexema());
        }
    }

    private void analisaExpressao() throws Exception {
        analisaExpressaoSimples();
        if (token.getSimbolo().equals("smaior") || token.getSimbolo().equals("smaiorig") ||
                token.getSimbolo().equals("sig") || token.getSimbolo().equals("smenor") ||
                token.getSimbolo().equals("smenorig") || token.getSimbolo().equals("sdif")) {

            PosFixa pos = new PosFixa(token.getLexema(),Tipo.RELACIONAL,4); // prepara a estrutura para comparar com a pilha de operadores
            verificaPrecedencia(pos);

            token = lexical.analyze();
            analisaExpressaoSimples();
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
                    PosFixa pos = new PosFixa(simbolo.getLexema(), simbolo.getTipo());
                    saida.add(pos);
                    token = lexical.analyze();
                } else {
                    PosFixa pos = new PosFixa(simbolo.getLexema(), simbolo.getTipo());
                    saida.add(pos);
                    token = lexical.analyze();
                }
            } else {
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
                throw new Exception("Esperava ) mas obtive " + token.getLexema());
            }
        } else if (token.getLexema().equals("verdadeiro") || token.getLexema().equals("falso")) {
            token = lexical.analyze();
        } else {
            throw new Exception("Fator " + token.getLexema() + " não identificado");
        }
    }

    // função para desempilhar até o fecha parenteses
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

    // função que verifica a precedencia do operador q esta entrando na pilha, e desempilha os q forem maiores ou iguais
    public void verificaPrecedencia(PosFixa operador) {
        if(pilhaPos.peek() == null ) { // verifica se ta vazia
            pilhaPos.push(operador);
            return;
        }

        if(operador.getPrecedencia() > pilhaPos.peek().getPrecedencia()) { // se o operador tiver precedencia maior, empilha
            pilhaPos.push(operador);
            return;
        }

        for (PosFixa p : pilhaPos) { // enquanto for menor ou igual, desempilha e adiciona na saida
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

    // função que retorna um simbolo da tabela de simbolos
    private TabelaSimbolos pesquisaTabela(String lexema) {
        for(TabelaSimbolos simbolo : tabelaSimbolos) {
            if (simbolo.getLexema().equals(lexema)) {
                return simbolo;
            }
        }
        return null;
    }

    private void analisaSubrotinas() throws Exception {

        // geracao de código
        int auxrot = 0, flag;
        flag = 0;
        if(token.getSimbolo().equals("sprocedimento") || token.getSimbolo().equals("sfuncao")){
            auxrot = rotulo; // guarda o inicio dos comandos, para pular declaração de subrotinas
            gera(-1,"JMP", "L" + rotulo,""); // pula para os comandos
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
            if(!pesquisaDeclFuncProcTabela(token.getLexema())) { // verifica se o nome do procedimento ja esta em uso
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.PROCEDIMENTO, true, "L" + rotulo);
                tabelaSimbolos.push(simbolo);
                gera(rotulo,"NULL","",""); // inicio do procedimento
                rotulo = rotulo + 1;

                token = lexical.analyze();
                if (token.getSimbolo().equals("spontovirgula")) {
                    analisaBloco();
                    int n = contaVariaveis();
                    gera(-1,"DALLOC",String.valueOf(m-n),String.valueOf(n));
                    m = m - n;
                    gera(-1,"RETURN","","");
                } else {
                    throw new Exception("Esperava ; mas obtive " + token.getLexema());
                }
            } else {
                throw new Exception("Variavel " + token.getLexema() + " nao declarada");
            }
        } else {
            throw new Exception("Variavel " + token.getLexema() + " nao declarada");
        }

        desempilha();
    }

    // conta as variaveis ate a marca, que são as que estão dentro do escopo de um procedimento/função/programa
    private int contaVariaveis() {
        int contador = 0;
        for (TabelaSimbolos simbolo : tabelaSimbolos) {
            if(simbolo.getEscopo()) {
                break;
            }
            if(simbolo.getTipo() == Tipo.VARIAVEL_INTEIRA || simbolo.getTipo() == Tipo.VARIAVEL_BOOLEANA) {
                contador++;
            }
        }
        return contador;
    }

    // desempilha a tabela de simbolos ate achar a marca
    private void desempilha() {
        for (TabelaSimbolos simbolo : tabelaSimbolos) {
            if(simbolo.getEscopo()) {
                simbolo.setEscopo(false);
                break;
            }
            tabelaSimbolos.pop();
        }
    }

    // função q verifica se uma função ou procedimento foi declarado na tabela de simbolos
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
                TabelaSimbolos simbolo = new TabelaSimbolos(token.getLexema(), Tipo.FUNCAO, true, "L" + rotulo);

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
                            int n = contaVariaveis();
                            gera(-1,"DALLOC",String.valueOf(m-n),String.valueOf(n));
                            m = m - n;
                            gera(-1,"RETURN","","");
                        }
                    } else {
                        throw new Exception("Tipo " + token.getLexema() + " nao permitido");
                    }
                } else {
                    throw new Exception("Esperava : mas obtive " + token.getLexema());
                }
            } else {
                throw new Exception("Variavel " + token.getLexema() + " nao declarada");
            }
        } else {
            throw new Exception("Variavel " + token.getLexema() + " nao declarada");
        }

        desempilha();
    }

    // função que altera o lista "copiaSaida" até sobrar somente um elemento, de forma que seja I (inteiro) ou B (booleano)
    private void analisaTipoExpressao() throws Exception {
        PosFixa n1,n2;

        if(copiaSaida.size() == 1) {
            if(copiaSaida.get(0).getTipo() == Tipo.FUNCAO_INTEIRA || copiaSaida.get(0).getTipo() == Tipo.VARIAVEL_INTEIRA) {
                copiaSaida = new ArrayList<>();
                copiaSaida.add(new PosFixa("I",Tipo.VARIAVEL_INTEIRA));
            } else if(copiaSaida.get(0).getTipo() == Tipo.FUNCAO_BOOLEANA || copiaSaida.get(0).getTipo() == Tipo.VARIAVEL_BOOLEANA) {
                copiaSaida = new ArrayList<>();
                copiaSaida.add(new PosFixa("B",Tipo.VARIAVEL_BOOLEANA));
            }
        }

        while (copiaSaida.size()>1) {
            for (int i=0; i<copiaSaida.size(); i++) {
                if (copiaSaida.get(i).getTipo() == Tipo.ARITMETICO || copiaSaida.get(i).getTipo() == Tipo.RELACIONAL) {
                    n1 = copiaSaida.get(i-2);
                    n2 = copiaSaida.get(i-1);
                    if (n1.getTipo() != Tipo.FUNCAO_INTEIRA && n1.getTipo() != Tipo.VARIAVEL_INTEIRA) {
                        if (n2.getTipo() != Tipo.FUNCAO_INTEIRA && n2.getTipo() != Tipo.VARIAVEL_INTEIRA) {
                            throw new Exception("Não foi possivel validar a expressão pois " + copiaSaida.get(i).getLexema()
                                    + " espera que " + n1.getLexema() + " e " + n2.getLexema() + " sejam variaveis inteira");
                        }
                    }
                    if (copiaSaida.get(i).getTipo() == Tipo.ARITMETICO) {
                        alteraSaida(i,Tipo.ARITMETICO);
                    } else {
                        alteraSaida(i,Tipo.RELACIONAL);
                    }
                    break;
                } else if (copiaSaida.get(i).getTipo() == Tipo.LOGICO) {
                    n1 = copiaSaida.get(i-2);
                    n2 = copiaSaida.get(i-1);
                    if (n1.getTipo() != Tipo.FUNCAO_BOOLEANA && n1.getTipo() != Tipo.VARIAVEL_BOOLEANA) {
                        if (n2.getTipo() != Tipo.FUNCAO_BOOLEANA && n2.getTipo() != Tipo.VARIAVEL_BOOLEANA) {
                            throw new Exception("Não foi possivel validar a expressão pois " + copiaSaida.get(i).getLexema()
                                    + " espera que " + n1.getLexema() + " e " + n2.getLexema() + " sejam variaveis booleanas");
                        }
                    }
                    alteraSaida(i,Tipo.LOGICO);
                    break;
                } else if (copiaSaida.get(i).getTipo() == Tipo.UNARIO) {
                    n1 = copiaSaida.get(i-1);
                    if (n1.getTipo() != Tipo.VARIAVEL_INTEIRA) {
                        throw new Exception("Não foi possivel validar a expressão pois o operador unario " + copiaSaida.get(i).getLexema()
                                + " espera que " + n1.getLexema()  + " seja variavel inteira");
                    }
                    alteraSaida(i,Tipo.UNARIO);
                    break;
                }
            }
        }
    }

    // função que altera o lista "copiaSaida" para I (inteiro) ou B (booleano)
    private void alteraSaida(int y, Tipo tipo) {
        List<PosFixa> auxSaida = new ArrayList<>();
        for (int i=0 ; i<copiaSaida.size(); i++) {
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

            if(i<copiaSaida.size()) {
                auxSaida.add(copiaSaida.get(i));
            }
        }
        copiaSaida = auxSaida;
    }

    // função que escreve uma nova linha no arquivo obj
    private void gera(int r, String instrucao, String var1, String var2) {
        try {
            FileWriter fileWriter = new FileWriter(fileCod, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String rot = "";
            if (r != -1) {
                rot = "L" + r;
            }

            if (!Objects.equals(var2, "")) {
                bufferedWriter.write(rot + "\t" + instrucao + " " + var1 + "," + var2 + "\n");
            } else {
                bufferedWriter.write(rot + "\t" + instrucao + " " + var1 + "\n");
            }

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // função que pega a expressao pós fixa (saida) e gera o codigo dela
    private void geraExpressao() throws Exception {

        for(PosFixa s : saida) {
            if (s.getTipo() == Tipo.VARIAVEL_BOOLEANA || s.getTipo() == Tipo.VARIAVEL_INTEIRA) {
                TabelaSimbolos simbolo = pesquisaTabela(s.getLexema());
                assert simbolo != null;
                gera(-1,"LDV",simbolo.getEndMemoria(),"");
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
                    case "<=" -> gera(-1, "CMEQ", "", "");
                    case ">=" -> gera(-1, "CMAQ", "", "");
                }
            } else if (s.getTipo() == Tipo.LOGICO) {
                switch (s.getLexema()) {
                    case "e" -> gera(-1, "AND", "", "");
                    case "ou" -> gera(-1, "OR", "", "");
                    case "nao" -> gera(-1, "NEG", "", "");
                }
            } else if (s.getTipo() == Tipo.FUNCAO_BOOLEANA || s.getTipo() == Tipo.FUNCAO_INTEIRA) {
                TabelaSimbolos simbolo = pesquisaTabela(s.getLexema());
                assert simbolo != null;
                gera(-1, "CALL", simbolo.getEndMemoria(),"");
                gera(-1, "LDV", "0","");;
            }
        }

        saida = new ArrayList<>();
    }

}

