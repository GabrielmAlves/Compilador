
import sintatico.AnalisadorSintatico;
import virtualMachine.VirtualMachine;

public class Main {
    public static void main(String[] args) throws Exception {
        AnalisadorSintatico analisador = new AnalisadorSintatico();

        analisador.analisa();

        VirtualMachine vm = new VirtualMachine();
        vm.vM();
    }
}