package lexical;

import java.io.File;
import java.util.Scanner;

public class LexicalAnalyzer {

//	private static String PATH = "file.txt";
	private static final String PATH ="C:\\Users\\gbidu\\OneDrive\\Documentos\\Programa.txt";

	public static void main(String[] args) {

		try {
			File file = new File(PATH);
			Scanner scanner = new Scanner(file);
			int flagComentario = 0;

			// while pra pegar linhas
			while (scanner.hasNextLine()) {

				// for pra pegar caracteres
				String line = scanner.nextLine();
				for (int i =0; i<line.length(); i++) {
					Character caractere = line.charAt(i);

					if(flagComentario == 1) {
						while (!caractere.equals('}')) {
							i++;
							if(i >= line.length()){
								break;
							}
							caractere = line.charAt(i);
							if (caractere.equals('}')) {
								flagComentario = 0;
							}
						}
					}

					if(caractere.equals('{') || caractere.equals(' ')) {
						if(caractere.equals('{')) {
							while (!caractere.equals('}')) {
								i++;
								if(i >= line.length()){
									flagComentario = 1;
									break;
								}
								caractere = line.charAt(i);
								if (caractere.equals('}')) {
									flagComentario = 0;
								}
							}
						}
						if (caractere.equals(' ')) {
							break;
						}
					}

					// TODO pega token e insere na lista
				}
			}
        } catch (Exception e) {
			System.out.println(e.getMessage());
        }
	}
}
