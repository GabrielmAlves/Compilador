package virtualMachine;

import java.io.File;
import java.util.Scanner;

public class VirtualMachine {
    private static final String PATH_CODIGO = "C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\arquivos\\obj\\cod.obj";
    private final File fileCod = new File(PATH_CODIGO);

    private String line;

    public void vM (){
        try {
            Scanner scanner = new Scanner(fileCod);

            while(scanner.hasNextLine()){
                String rotulo = "";
                String var1 = "";
                String var2 = "";
                String instrucao = "";
                line = scanner.nextLine();
                int i=0;
                if(line.charAt(0) != '\t') {
                    rotulo = line.substring(0,2);
                    i=3;
                } else {
                    i=1;
                }
                while (line.charAt(i)!= ' ') {
                    instrucao += line.charAt(i);
                    i++;
                }
                i++;
                int flag=0;
                while (i < line.length()) {
                    if(line.charAt(i) == ',') {
                        flag=1;
                        i++;
                    }
                    if(flag == 0) {
                        var1 += line.charAt(i);
                    } else {
                        var2 += line.charAt(i);
                    }
                    i++;
                }
            }

        }catch (Exception e ){
            System.out.println(e.getMessage());
        }
    }
}
