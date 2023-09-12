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
					i = pegaToken(line, i);

					if(i>= line.length()) {
						i = 0;
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
		} else {
			i++;
		}
		return i;
	}

	private int pegaToken(String line, int i) {
		if(Character.isDigit(line.charAt(i))) {
			// trata digito
			i = trataDigito(line,i);

		} else if(Character.isLetter(line.charAt(i))) {
			// trata letra
			i = trataLetra(line,i);

		} else if(caractereIn(operadoresAritimeticos,line.charAt(i))) {
			// TODO trata operador aritmetico

		} else if(caractereIn(operadoresRelacionais,line.charAt(i))) {
			// TODO trata operador relacional

		} else if(caractereIn(pontuacoes,line.charAt(i))) {
			// TODO trata pontuacao

		} else {
			// TODO erro
		}
		return i;
	}

	private int trataDigito(String line, int i) {
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

	private int trataLetra(String line, int i) {
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

	private boolean caractereIn(Set<Character> lista, Character caractere) {
		for(Character c : lista) {
			if(c == caractere) {
				return true;
			}
		}
		return false;
	}


}
