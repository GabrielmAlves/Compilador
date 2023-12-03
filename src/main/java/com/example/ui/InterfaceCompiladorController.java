package com.example.ui;

import coreCompilador.sintatico.AnalisadorSintatico;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InterfaceCompiladorController {
    @FXML
    private Label textFiledPath;
    @FXML
    private TextArea fileArea;
    @FXML
    private TextArea textErrors;

    private boolean editavel = true;
    private File compilaFile;

    @FXML
    protected void openFile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha um arquivo");

        // Abrir o seletor de arquivos quando um botão for clicado
        // (substitua isso pela ação real que você deseja)
        File result = fileChooser.showOpenDialog(null);

        textFiledPath.setText(result.getAbsolutePath());

        compilaFile = result;
        writeFile(result);
   }

   protected void writeFile(File file) throws FileNotFoundException {
       Scanner scanner = new Scanner(file);
       String contentFile = "";

       while (scanner.hasNextLine()) {
           contentFile += scanner.nextLine() + "\n";
       }

       fileArea.setText(contentFile);
   }

   @FXML
   protected void editar() {
       editavel = !editavel;
       fileArea.setDisable(editavel);
   }

   @FXML
   protected void compilar() {
        try {
           AnalisadorSintatico sintatico = new AnalisadorSintatico(compilaFile.getAbsolutePath());
           sintatico.analisa();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alerta");
            alert.setHeaderText(null); // Não exibirá o cabeçalho
            alert.setContentText("Compilado com sucesso!");

            // Mostra o alerta quando o botão é clicado
            alert.show();

        }catch (Exception e) {
            textErrors.setText(e.getMessage());
        }
   }
}