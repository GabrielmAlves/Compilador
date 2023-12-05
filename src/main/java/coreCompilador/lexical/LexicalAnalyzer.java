package coreCompilador.lexical;

import java.io.File;
import java.util.*;

public class LexicalAnalyzer {
	private static final Set<Character> operadoresAritimeticos = new HashSet<>(Arrays.asList('+', '-', '*'));
	private static final Set<Character> operadoresRelacionais = new HashSet<>(Arrays.asList('!', '<', '>', '='));
	private static final Set<Character> pontuacoes = new HashSet<>(Arrays.asList(';', ',', '(', ')', '.'));

	private Scanner scanner;
	private String line;
	private int i;	// ponteiro da posição de cada linha/string
	private Character character;
	private Token tokinho;

	public LexicalAnalyzer(File file) {
		try{
			scanner = new Scanner(file);
			i=-1;
			pegaLinha();
			character = pegaCaracter();
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	public Token analyze() throws Exception {
		if (character == null) {
			return null;
		}

		while (character.equals('{') || character.equals(' ') || character.equals('\t')) {
			if (character.equals('{')) {
				trataComentario(i);
			}
			character = pegaCaracter();
			if(character == null) {
				break;
			}
		}
		pegaToken();
		return tokinho;
	}

	private void trataComentario(int i) throws Exception {
		character = line.charAt(i);
		while (!character.equals('}')) {
			character = pegaCaracter();
		}
	}

	private void pegaToken() throws Exception {

		if(character == null) {
			tokinho = null;
			return;
		}

		if(Character.isDigit(character)) {
			// trata digito
			trataDigito();

		} else if(Character.isLetter(character)) {
			// trata letra
			trataLetra();

		} else if (character.equals(':')) {
			// trata atribuicao
			trataAtibuicao();

		} else if(characterIn(operadoresAritimeticos,character)) {
			// trata operador aritmetico
			trataAritmetico();

		} else if(characterIn(operadoresRelacionais,character)) {
			// trata operador relacional
			trataRelacional();

		} else if(characterIn(pontuacoes,character)) {
			// trata pontuacao
			trataPontuacao();

		} else {
			throw new Exception(String.format("Caracter %c inválido",character));
		}
	}

	private void trataDigito() throws Exception {
		String numero = String.valueOf(character);
		character = pegaCaracter();
		while(Character.isDigit(character)) {
			numero = numero + character;
			if((i+1) >= line.length()) {
				character = pegaCaracter();
				break;
			}
			character = pegaCaracter();
		}

		tokinho = new Token("snumero", numero);
	}

	private void trataLetra() throws Exception {
		String id = String.valueOf(character);
		if((i+1) < line.length()) {
			character = pegaCaracter();
			while (Character.isLetter(character) || Character.isDigit(character) || character.equals('_')) {
            			id = id + character;
            			if((i+1) >= line.length()) {
            				character = pegaCaracter();
            				break;
            			}
            			character = pegaCaracter();
			}
		} else {
			character = pegaCaracter();
		}

        switch (id) {
            case "programa" -> {
                tokinho = new Token("sprograma", id);
            }
            case "se" -> {
                tokinho= new Token("sse", id);
            }
            case "entao" -> {
                tokinho = new Token("sentao", id);
            }
            case "senao" -> {
				tokinho = new Token("ssenao", id);
            }
            case "enquanto" -> {
				tokinho = new Token("senquanto", id);
            }
            case "faca" -> {
				tokinho = new Token("sfaca", id);
            }
			case "inicio" -> {
				tokinho = new Token("sinicio", id);
			}
			case "fim" -> {
				tokinho = new Token("sfim", id);
			}
			case "escreva" -> {
				tokinho = new Token("sescreva", id);
			}
			case "leia" -> {
				tokinho = new Token("sleia", id);
			}
			case "var" -> {
				tokinho = new Token("svar", id);
			}
			case "inteiro" -> {
				tokinho = new Token("sinteiro", id);
			}
			case "booleano" -> {
				tokinho = new Token("sbooleano", id);
			}
			case "verdadeiro" -> {
				tokinho = new Token("sverdadeiro", id);
			}
			case "falso" -> {
				tokinho = new Token("sfalso", id);
			}
			case "procedimento" -> {
				tokinho = new Token("sprocedimento", id);
			}
			case "funcao" -> {
				tokinho = new Token("sfuncao", id);
			}
			case "div" -> {
				tokinho = new Token("sdiv", id);
			}
			case "e" -> {
				tokinho = new Token("se", id);
			}
			case "ou" -> {
				tokinho = new Token("sou", id);
			}
			case "nao" -> {
				tokinho = new Token("snao", id);
			}
			default -> {
				tokinho = new Token("sidentificador", id);
			}
        }
	}

	private void trataAtibuicao() throws Exception {
		character = pegaCaracter();

		if(character.equals('=')) {
			tokinho = new Token("satribuicao",":=");
			character = pegaCaracter();
		} else {
			tokinho = new Token("sdoispontos",":");
		}
	}

	private void trataAritmetico() throws Exception {
		tokinho.setLexema(String.valueOf(character));

		switch (character) {
			case '+' -> {
				tokinho.setSimbolo("smais");
			}
			case '-' -> {
				tokinho.setSimbolo("smenos");
			}
			case '*' -> {
				tokinho.setSimbolo("smult");
			}
		}
		character = pegaCaracter();
	}

	private void trataRelacional() throws Exception {

		if (character.equals('!')) {
			character = pegaCaracter();
			if(character.equals('=')) {
				tokinho.setLexema("!=");
				tokinho.setSimbolo("sdif");
				character = pegaCaracter();
			} else {
				throw new Exception(String.format("Caracter ! inválido"));
			}
		} else if (character.equals('<')) {
			character = pegaCaracter();
			if (character.equals('=')) {
				tokinho.setLexema("<=");
				tokinho.setSimbolo("smenorig");
				character = pegaCaracter();
			} else {
				tokinho.setLexema("<");
				tokinho.setSimbolo("smenor");
			}
		} else if (character.equals('>')) {
			character = pegaCaracter();
			if (character.equals('=')) {
				tokinho.setLexema(">=");
				tokinho.setSimbolo("smaiorig");
				character = pegaCaracter();
			} else {
				tokinho.setLexema(">");
				tokinho.setSimbolo("smaior");
			}
		} else if (character.equals('=')) {
			tokinho.setLexema("=");
			tokinho.setSimbolo("sig");
			character = pegaCaracter();
		}
	}

	private void trataPontuacao() throws Exception {

		tokinho.setLexema(String.valueOf(character));

		switch (character) {
			case '.' -> {
				tokinho.setSimbolo("sponto");
			}
			case ';' -> {
				tokinho.setSimbolo("spontovirgula");
			}
			case ',' -> {
				tokinho.setSimbolo("svirgula");
			}
			case '(' -> {
				tokinho.setSimbolo("sabreparenteses");
			}
			case ')' -> {
				tokinho.setSimbolo("sfechaparenteses");
			}
		}

		character = pegaCaracter();
	}

	private boolean characterIn(Set<Character> lista, Character character) {
		for(Character c : lista) {
			if(c == character) {
				return true;
			}
		}
		return false;
	}

	// função para pegar linhas com conteudo
	private boolean pegaLinha() {
		line = scanner.nextLine();
		while (line.isBlank()) {
			if (!scanner.hasNextLine()) {
				return false;
			}
			line = scanner.nextLine();
		}
		return true;
	}

	private Character pegaCaracter() throws Exception {
		Character character;
		if ((i + 1) >= line.length()) {
			i=0;
			if (!scanner.hasNextLine()) {
				return null;
			}
			if (!pegaLinha()) {
				return null;
			}
		} else {
			i++;
		}
		character = line.charAt(i);
		return character;
	}
}

