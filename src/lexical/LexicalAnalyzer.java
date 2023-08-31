package lexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LexicalAnalyzer {

	private static String PATH = "C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\lexical\\file.txt";

	public static void main(String[] args) {
		
		try {
			File file = new File(PATH);
			Scanner scanner = new Scanner(file);

			// while pra pegar linhas
			while (scanner.hasNextLine()) {

				// for pra pegar caracteres
				String line = scanner.nextLine();
				for (int i =0; i<line.length(); i++) {
					char caractere = line.charAt(i);
					System.out.println(caractere);
				}
			}

//            while(line != null) {
//            	while(line == "{" || line == " " && (line = file.readLine()) != null) {
//            		if(line == "}") {
//            			while(line != "}" && (line = file.readLine()) != null) {
//            				readLine(file);
//            				readLine(file);
//            			}
//            		}
//            	}
//            }

//            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


	}
}
