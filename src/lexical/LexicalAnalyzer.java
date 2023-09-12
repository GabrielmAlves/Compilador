package lexical;

import java.io.File;
import java.util.*;

public class LexicalAnalyzer {

//	private static String PATH = "file.txt";
	private static final String PATH ="C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\lexical\\file.txt";
	private static final Set<Character> operadoresAritimeticos = new HashSet<>(Arrays.asList('+', '-', '*'));
	private static final Set<Character> operadoresRelacionais = new HashSet<>(Arrays.asList('!', '<', '>', '='));
	private static final Set<Character> pontuacoes = new HashSet<>(Arrays.asList(';', ',', '(', ')', '.'));

	private Scanner scanner;
	private String line;
	private final File file = new File(PATH);;

	private List<Token> tokens = new ArrayList<>();

	public void analyzer() {

		try {
			scanner = new Scanner(file);
			int i = 0;

			if(scanner.hasNextLine()) {
				line = scanner.nextLine();
			}

			// while pra pegar linhas
			while (scanner.hasNextLine()) {

				while (i<line.length()) {
					Character caractere = line.charAt(i);

					if (caractere.equals('{') || caractere.equals(' ')) {
						if (caractere.equals('{')) {
							i =	trataComentario(i);
							caractere = line.charAt(i);
						}
						while (caractere.equals(' ')) {
							i++;
							caractere = line.charAt(i);
						}
					}
					// pega token e insere na lista
					i = pegaToken(i);

					if(i>= line.length() && scanner.hasNextLine()) {
						i = 0;
						line = scanner.nextLine();
					}
				}
			}


        } catch (Exception e) {
			System.out.println(e.getMessage());
        }
	}

	private int trataComentario(int i) {
		Character caractere = line.charAt(i);
		while (!caractere.equals('}')) {
			i++;
			if(i >= line.length()) {
				line = scanner.nextLine();
				i = 0;
			}
			caractere = line.charAt(i);
		}
		i++;
		if(i >= line.length()) {
			line = scanner.nextLine();
			i = 0;
		}
		return i;
	}

	private int pegaToken(int i) {
		Character character = line.charAt(i);
		if(Character.isDigit(character)) {
			// trata digito
			i = trataDigito(i);

		} else if(Character.isLetter(character)) {
			// trata letra
			i = trataLetra(i);

		} else if (character.equals(':')) {
			// trata atribuicao
			i = trataAtibuicao(i);

		} else if(caractereIn(operadoresAritimeticos,character)) {
			// trata operador aritmetico
			i = trataAritmetico(i);

		} else if(caractereIn(operadoresRelacionais,character)) {
			// trata operador relacional
			i = trataRelacional(i);

		} else if(caractereIn(pontuacoes,character)) {
			// trata pontuacao
			i = trataPontuacao(i);

		} else {
			// TODO erro
		}
		return i;
	}

	private int trataDigito(int i) {
		Character character = line.charAt(i);
		String numero = String.valueOf(character);
		i++;
		character = line.charAt(i);
		while(Character.isDigit(character)) {
			numero = numero + character;
			i++;
			character = line.charAt(i);
		}
		Token token = new Token("snumero", numero);
		this.tokens.add(token);
		return i;
	}

	private int trataLetra(int i) {
		Character character = line.charAt(i);
		String id = String.valueOf(character);
		i++;
		character = line.charAt(i);
		while(Character.isLetter(character) || Character.isDigit(character) || character.equals('_')) {
			id = id + character;
			i++;
			character = line.charAt(i);
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
				token = new Token("var", id);
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
		return i;
	}

	private int trataAtibuicao(int i) {
		i++;
		Character character = line.charAt(i);

		Token token;
		if(character.equals('=')) {
			token = new Token("satribuicao",":=");
			tokens.add(token);
			i++;
		} else {
			token = new Token("sdoispontos",":");
			tokens.add(token);
		}

		return i;
	}

	private int trataAritmetico(int i) {
		Character character = line.charAt(i);

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
		i++;

		return i;
	}

	private int trataRelacional(int i) {
		Character character = line.charAt(i);

		Token token = new Token();

		if (character.equals('!')) {
			i++;
			character = line.charAt(i);
			if(character.equals('=')) {
				token.setLexema("!=");
				token.setSimbolo("sdif");
				i++;
			} else {
				// TODO erro
			}
		} else if (character.equals('<')) {
			i++;
			character = line.charAt(i);
			if (character.equals('=')) {
				token.setLexema("<=");
				token.setSimbolo("smenorig");
				i++;
			} else {
				token.setLexema("<");
				token.setSimbolo("smenor");
			}
		} else if (character.equals('>')) {
			i++;
			character = line.charAt(i);
			if (character.equals('=')) {
				token.setLexema(">=");
				token.setSimbolo("smaiorig");
				i++;
			} else {
				token.setLexema(">");
				token.setSimbolo("smaior");
			}
		} else if (character.equals('=')) {
			token.setLexema("=");
			token.setSimbolo("sig");
			i++;
		}

		return i;
	}

	private int trataPontuacao(int i) {
		Character character = line.charAt(i);

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
		i++;

		return i;
	}

	private boolean caractereIn(Set<Character> lista, Character caractere) {
		for(Character c : lista) {
			if(c == caractere) {
				return true;
			}
		}
		return false;
	}
}
