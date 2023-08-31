package lexical;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
	
	public static String readLine(BufferedReader file) throws IOException {
		String line;
		List<String> lista = new ArrayList();
        line = file.readLine();
        System.out.println(line);
        //for (char caractere : line.toCharArray()) {
        	//lista.add(line);
           // System.out.print(caractere);
        //}
        return line;
	}
	
	
	public static void main(String[] args) {
		
		try {
            
			BufferedReader file = new BufferedReader(new FileReader("C:\\\\Users\\\\gbidu\\\\OneDrive\\\\Documentos\\\\Programa.txt"));
			readLine(file);
            String line;
            line = file.readLine();
            while((line = file.readLine()) != null) {
            	while(line == "{" || line == " " && (line = file.readLine()) != null) {
            		if(line == "}") {
            			while(line != "}" && (line = file.readLine()) != null) {
            				readLine(file);
            				readLine(file);
            			}
            		}
            	}
            }

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


	}
}
