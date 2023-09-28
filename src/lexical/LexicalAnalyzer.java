package lexical;

import java.io.File;
import java.util.*;

public class LexicalAnalyzer {

	//	private static String PATH = "file.txt";
	private static final Set<Character> operadoresAritimeticos = new HashSet<>(Arrays.asList('+', '-', '*'));
	private static final Set<Character> operadoresRelacionais = new HashSet<>(Arrays.asList('!', '<', '>', '='));
	private static final Set<Character> pontuacoes = new HashSet<>(Arrays.asList(';', ',', '(', ')', '.'));

	private Scanner scanner;
	private String line;
	private int i;	// ponteiro da posição de cada linha/string
	private Character character;
//	private List<Token> tokens = new ArrayList<>();
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

	public Token analyze() {

		try {

			if (!scanner.hasNextLine()) {
				throw new Exception("Arquivo vazio");
			}

			if (character.equals('{') || character.equals(' ') || character.equals('\t')) {
				if (character.equals('{')) {
					trataComentario(i);
				}
				while (character.equals(' ') || character.equals('\t')) {
					character = pegaCaracter();
				}
			}
				// pega token e insere na lista
				pegaToken();


//			System.out.println(tokinho.getLexema());
//			System.out.println(tokinho.getSimbolo());
			//System.out.println(character);
			return tokinho;


        } catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
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
			// TODO erro
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
//		this.tokens.add(token);
	}

	private void trataLetra() throws Exception {
		String id = String.valueOf(character);
		character = pegaCaracter();
		while (Character.isLetter(character) || Character.isDigit(character) || character.equals('_')) {
			id = id + character;
			if((i+1) >= line.length()) {
				character = pegaCaracter();
				break;
			}
			character = pegaCaracter();
		}

        switch (id) {
            case "programa" -> {
                tokinho = new Token("sprograma", id);
//                this.tokens.add(token);
            }
            case "se" -> {
                tokinho= new Token("sse", id);
//                this.tokens.add(token);
            }
            case "entao" -> {
                tokinho = new Token("sentao", id);
//                this.tokens.add(token);
            }
            case "senao" -> {
				tokinho = new Token("ssenao", id);
//                this.tokens.add(token);
            }
            case "enquanto" -> {
				tokinho = new Token("senquanto", id);
//                this.tokens.add(token);
            }
            case "faca" -> {
				tokinho = new Token("sfaca", id);
//                this.tokens.add(token);
            }
			case "inicio" -> {
				tokinho = new Token("sinicio", id);
//				this.tokens.add(token);
			}
			case "fim" -> {
				tokinho = new Token("sfim", id);
//				this.tokens.add(token);
			}
			case "escreva" -> {
				tokinho = new Token("sescreva", id);
//				this.tokens.add(token);
			}
			case "leia" -> {
				tokinho = new Token("sleia", id);
//				this.tokens.add(token);
			}
			case "var" -> {
				tokinho = new Token("svar", id);
//				this.tokens.add(token);
			}
			case "inteiro" -> {
				tokinho = new Token("sinteiro", id);
//				this.tokens.add(token);
			}
			case "booleano" -> {
				tokinho = new Token("sbooleano", id);
//				this.tokens.add(token);
			}
			case "verdadeiro" -> {
				tokinho = new Token("sverdadeiro", id);
//				this.tokens.add(token);
			}
			case "falso" -> {
				tokinho = new Token("sfalso", id);
//				this.tokens.add(token);
			}
			case "procedimento" -> {
				tokinho = new Token("sprocedimento", id);
//				this.tokens.add(token);
			}
			case "funcao" -> {
				tokinho = new Token("sfuncao", id);
//				this.tokens.add(token);
			}
			case "div" -> {
				tokinho = new Token("sdiv", id);
//				this.tokens.add(token);
			}
			case "e" -> {
				tokinho = new Token("se", id);
//				this.tokens.add(token);
			}
			case "ou" -> {
				tokinho = new Token("sou", id);
//				this.tokens.add(token);
			}
			case "nao" -> {
				tokinho = new Token("snao", id);
//				this.tokens.add(token);
			}
			default -> {
				tokinho = new Token("sidentificador", id);
//				this.tokens.add(token);
			}
        }
	}

	private void trataAtibuicao() throws Exception {
		character = pegaCaracter();

		if(character.equals('=')) {
			tokinho = new Token("satribuicao",":=");
//			tokens.add(token);
			character = pegaCaracter();
		} else {
			tokinho = new Token("sdoispontos",":");
//			tokens.add(token);
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
//		tokens.add(token);
		character = pegaCaracter();
	}

	private void trataRelacional() throws Exception {

		if (character.equals('!')) {
			character = pegaCaracter();
			if(character.equals('=')) {
				tokinho.setLexema("!=");
				tokinho.setSimbolo("sdif");
			} else {
				// TODO erro
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
//		tokens.add(token);
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
		return  i >= line.length();
	}

	private Character pegaCaracter() throws Exception {
		Character character;
		if ((i + 1) >= line.length()) {
			i=0;
			if(!scanner.hasNextLine()) {
				System.out.println("Fim de arquivo");
				return null;
			}
			pegaLinha();
		} else {
			i++;
		}
		character = line.charAt(i);
		return character;
	}
}

