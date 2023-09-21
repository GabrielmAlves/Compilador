package lexical;

import java.io.File;
import java.util.*;

public class LexicalAnalyzer {

//	private static String PATH = "file.txt";
	private static final String PATH ="C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\arquivos\\teste_10.txt";
	private static final Set<Character> operadoresAritimeticos = new HashSet<>(Arrays.asList('+', '-', '*'));
	private static final Set<Character> operadoresRelacionais = new HashSet<>(Arrays.asList('!', '<', '>', '='));
	private static final Set<Character> pontuacoes = new HashSet<>(Arrays.asList(';', ',', '(', ')', '.'));

	private Scanner scanner;
	private String line;
	private int i;	// ponteiro da posição de cada linha/string
	private Character character;
	private List<Token> tokens = new ArrayList<>();

	public void analyzer() {

		try {
			File file = new File(PATH);
			scanner = new Scanner(file);

			if (!scanner.hasNextLine()) {
				throw new Exception("Arquivo vazio");
			}

			i=-1;
			pegaLinha();
			character = pegaCaracter();
			while (scanner.hasNextLine() || fimDeLinha()) { // enquanto não chegar no fim do arquivo

				if (character.equals('{') || character.equals(' ') || character.equals('\t')) {
					if (character.equals('{')) {
						trataComentario(i);
					}
					while (character.equals(' ') || character.equals('\t')) {
						character = pegaCaracter();
					}
				} else {
					// pega token e insere na lista
					pegaToken();
				}
			}

			System.out.println("\nLISTA DE TOKENS\n");
			for (Token t : this.tokens){
				System.out.println(t.getLexema() + " - " + t.getSimbolo());
			}
			System.out.println();

        } catch (Exception e) {
			System.out.println("\nLISTA DE TOKENS\n");
			for (Token t : this.tokens){
				System.out.println(t.getLexema() + " - " + t.getSimbolo());
			}
			System.out.println();
			System.out.println(e.getMessage());
        }
	}

	private void trataComentario(int i) throws Exception {
		character = line.charAt(i);
		while (!character.equals('}')) {
			character = pegaCaracter();
		}
		character = pegaCaracter();
	}

	private void pegaToken() throws Exception {
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
			// TODO erro
			throw new Exception(String.format("Caracter %c inválido",character));
		}
	}

	private void trataDigito() throws Exception {
		String numero = String.valueOf(character);
		character = pegaCaracter();
		while(Character.isDigit(character)) {
			numero = numero + character;
			character = pegaCaracter();
		}

		Token token = new Token("snumero", numero);
		this.tokens.add(token);
	}

	private void trataLetra() throws Exception {
		String id = String.valueOf(character);
		character = pegaCaracter();
		while (Character.isLetter(character) || Character.isDigit(character) || character.equals('_')) {
			id = id + character;
			character = pegaCaracter();
		}

		Token token;
        switch (id) {
            case "programa" -> {
                token = new Token("sprograma", id);
                this.tokens.add(token);
            }
            case "se" -> {
                token = new Token("sse", id);
                this.tokens.add(token);
            }
            case "entao" -> {
                token = new Token("sentao", id);
                this.tokens.add(token);
            }
            case "senao" -> {
                token = new Token("ssenao", id);
                this.tokens.add(token);
            }
            case "enquanto" -> {
                token = new Token("senquanto", id);
                this.tokens.add(token);
            }
            case "faca" -> {
                token = new Token("sfaca", id);
                this.tokens.add(token);
            }
			case "inicio" -> {
				token = new Token("sinicio", id);
				this.tokens.add(token);
			}
			case "fim" -> {
				token = new Token("sfim", id);
				this.tokens.add(token);
			}
			case "escreva" -> {
				token = new Token("sescreva", id);
				this.tokens.add(token);
			}
			case "leia" -> {
				token = new Token("sleia", id);
				this.tokens.add(token);
			}
			case "var" -> {
				token = new Token("svar", id);
				this.tokens.add(token);
			}
			case "inteiro" -> {
				token = new Token("sinteiro", id);
				this.tokens.add(token);
			}
			case "booleano" -> {
				token = new Token("sbooleano", id);
				this.tokens.add(token);
			}
			case "verdadeiro" -> {
				token = new Token("sverdadeiro", id);
				this.tokens.add(token);
			}
			case "falso" -> {
				token = new Token("sfalso", id);
				this.tokens.add(token);
			}
			case "procedimento" -> {
				token = new Token("sprocedimento", id);
				this.tokens.add(token);
			}
			case "funcao" -> {
				token = new Token("sfuncao", id);
				this.tokens.add(token);
			}
			case "div" -> {
				token = new Token("sdiv", id);
				this.tokens.add(token);
			}
			case "e" -> {
				token = new Token("se", id);
				this.tokens.add(token);
			}
			case "ou" -> {
				token = new Token("sou", id);
				this.tokens.add(token);
			}
			case "nao" -> {
				token = new Token("snao", id);
				this.tokens.add(token);
			}
			default -> {
				token = new Token("sidentificador", id);
				this.tokens.add(token);
			}
        }
	}

	private void trataAtibuicao() throws Exception {
		character = pegaCaracter();

		Token token;
		if(character.equals('=')) {
			token = new Token("satribuicao",":=");
			tokens.add(token);
			character = pegaCaracter();
		} else {
			token = new Token("sdoispontos",":");
			tokens.add(token);
		}
	}

	private void trataAritmetico() throws Exception {
		Token token = new Token();
		token.setLexema(String.valueOf(character));

		switch (character) {
			case '+' -> {
				token.setSimbolo("smais");
			}
			case '-' -> {
				token.setSimbolo("smenos");
			}
			case '*' -> {
				token.setSimbolo("smult");
			}
		}
		tokens.add(token);
		character = pegaCaracter();
	}

	private void trataRelacional() throws Exception {
		Token token = new Token();

		if (character.equals('!')) {
			character = pegaCaracter();
			if(character.equals('=')) {
				token.setLexema("!=");
				token.setSimbolo("sdif");
			} else {
				// TODO erro
				throw new Exception(String.format("Caracter ! inválido"));
			}
		} else if (character.equals('<')) {
			character = pegaCaracter();
			if (character.equals('=')) {
				token.setLexema("<=");
				token.setSimbolo("smenorig");
				character = pegaCaracter();
			} else {
				token.setLexema("<");
				token.setSimbolo("smenor");
			}
		} else if (character.equals('>')) {
			character = pegaCaracter();
			if (character.equals('=')) {
				token.setLexema(">=");
				token.setSimbolo("smaiorig");
				character = pegaCaracter();
			} else {
				token.setLexema(">");
				token.setSimbolo("smaior");
			}
		} else if (character.equals('=')) {
			token.setLexema("=");
			token.setSimbolo("sig");
			character = pegaCaracter();
		}
		tokens.add(token);
	}

	private void trataPontuacao() throws Exception {
		Token token = new Token();
		token.setLexema(String.valueOf(character));

		switch (character) {
			case '.' -> {
				token.setSimbolo("sponto");
			}
			case ';' -> {
				token.setSimbolo("sponto_virgula");
			}
			case ',' -> {
				token.setSimbolo("svirgula");
			}
			case '(' -> {
				token.setSimbolo("sabre_parenteses");
			}
			case ')' -> {
				token.setSimbolo("sfecha_parenteses");
			}
		}
		tokens.add(token);
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
	private void pegaLinha() throws Exception {
		line = scanner.nextLine();
		while (line.isBlank()) {
			if (!scanner.hasNextLine()) {
				throw new Exception("Fim de arquivo");
			}
			line = scanner.nextLine();
		}
	}

	private boolean fimDeLinha() {
		return  !(i >= line.length());
	}

	private Character pegaCaracter() throws Exception {
		Character character;
		if ((i + 1) >= line.length()) {
			i=0;
			if(!scanner.hasNextLine()) {
				throw new Exception("Fim de arquivo");
			}
			pegaLinha();
		} else {
			i++;
		}
		character = line.charAt(i);
		return character;
	}
}

