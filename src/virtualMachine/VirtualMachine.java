package virtualMachine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VirtualMachine {
    private static final String PATH_CODIGO = "C:\\Users\\julia\\OneDrive\\Área de Trabalho\\PUCC\\Compiladores\\Prática\\compilador\\src\\arquivos\\obj\\cod.obj";
    private final File fileCod = new File(PATH_CODIGO);
    private List<Integer> memoria = new ArrayList<>();
    private int s;
    private String line;

    private int i;

    public void vM (){
        try {
            Scanner scanner = new Scanner(fileCod);

            while(scanner.hasNextLine()) {
                String rotulo = "";
                String var1 = "";
                String var2 = "";
                String instrucao = "";
                line = scanner.nextLine();
                int k=0;
                if(line.charAt(0) != '\t') {
                    rotulo = line.substring(0,2);
                    k=3;
                } else {
                    k=1;
                }
                while (line.charAt(k)!= ' ') {
                    instrucao += line.charAt(k);
                    k++;
                }
                k++;
                int flag=0;
                while (k < line.length()) {
                    if(line.charAt(k) == ',') {
                        flag=1;
                        k++;
                    }
                    if(flag == 0) {
                        var1 += line.charAt(k);
                    } else {
                        var2 += line.charAt(k);
                    }
                    k++;
                }

                int result;
                int num1;
                int num2;
                switch (instrucao) {
                    case "LDC":
                        s++;
                        memoria.set(s, Integer.valueOf(var1));
                        break;
                    case "LDV":
                        s++;
                        memoria.set(s, memoria.get(Integer.parseInt(var1)));
                        break;
                    case "ADD":
                        num1 = memoria.get(s - 1);
                        num2 = memoria.get(s);
                        result = num1 + num2;
                        memoria.set(s - 1, result);
                        s--;
                        break;
                    case "SUB":
                        num1 = memoria.get(s - 1);
                        num2 = memoria.get(s);
                        result = num1 - num2;
                        memoria.set(s - 1, result);
                        s--;
                        break;
                    case "MULT":
                        num1 = memoria.get(s - 1);
                        num2 = memoria.get(s);
                        result = num1 * num2;
                        memoria.set(s - 1, result);
                        s--;
                        break;
                    case "DIVI":
                        num1 = memoria.get(s - 1);
                        num2 = memoria.get(s);
                        result = num1 / num2;
                        memoria.set(s - 1, result);
                        s--;
                        break;
                    case "INV":
                        memoria.set(s, -memoria.get(s));
                        break;
                    case "AND":
                        if (memoria.get(s - 1) == 1 && memoria.get(s) == 1) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "OR":
                        if (memoria.get(s - 1) == 1 || memoria.get(s) == 1) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "NEG":
                        memoria.set(s, 1 - memoria.get(s));
                        break;
                    case "CME":
                        if (memoria.get(s - 1) < memoria.get(s - 1)) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "CMA":
                        if (memoria.get(s - 1) > memoria.get(s - 1)) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "CEQ":
                        if (memoria.get(s - 1) == memoria.get(s - 1)) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "CDIF":
                        if (memoria.get(s - 1) != memoria.get(s - 1)) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "CMEQ":
                        if (memoria.get(s - 1) <= memoria.get(s - 1)) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "CMAQ":
                        if (memoria.get(s - 1) >= memoria.get(s - 1)) {
                            memoria.set(s - 1, 1);
                        } else {
                            memoria.set(s - 1, 0);
                        }
                        s--;
                        break;
                    case "STR":
                        memoria.set(Integer.parseInt(var1), memoria.get(s));
                        s--;
                        break;
                    case "JMP":
                        i = p;
                        break;
                    case "JMPF":
                        if (memoria.get(s) == 0) {
                            i = p;
                        } else {
                            i++;
                        }
                        s--;
                        break;
                    case "RD":
                        s++;
                        memoria.set(s, Integer.parseInt(var1));
                        break;
                    case "PRN":
                        System.out.println(memoria.get(s));
                        break;
                    case "START":
                        s = -1;
                        break;
                    case "HLT":
                        // para exec da maquina virtual
                        break;
                    case "ALLOC":
                        for (int j = 0; j < Integer.parseInt(var2); j++) {
                            s++;
                            memoria.set(s, memoria.get(Integer.parseInt(var1) + j));
                        }
                        break;
                    case "DALLOC":
                        for (int j = Integer.parseInt(var2) - 1; j >= 0; j--) {
                            memoria.set(Integer.parseInt(var1) + j, memoria.get(s));
                            s--;
                        }
                        break;
                    case "CALL":
                        s++;
                        memoria.set(s,i+1);
                        i = p;
                        break;
                    case "RETURN":
                        i = memoria.get(s);
                        s--;
                        break;
                    default:
                        System.out.println("Instrução não encontrada");
                }

            }

        }catch (Exception e ){
            System.out.println(e.getMessage());
        }
    }
}
