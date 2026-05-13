package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FirstSceneController {

    @FXML
    private TextField firstNumberField;

    @FXML
    private TextField secondNumberField;

    private int firstNumber;
    private int secondNumber;
    private int sum;

    @FXML
    public void initialize() {
        System.out.println("FXML Controller loaded!");
    }

    @FXML
    private void handleCalculate() {
        try {
            int firstNumber = Integer.parseInt(firstNumberField.getText());
            int secondNumber = Integer.parseInt(secondNumberField.getText());

            int sum = firstNumber + secondNumber;

            System.out.println("sum: " + sum);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/secondScreen.fxml"));
            Scene resultScene = new Scene(loader.load());

            ResultController resultController = loader.getController();
            resultController.setResult(sum);

            Stage stage = (Stage) firstNumberField.getScene().getWindow();
            stage.setScene(resultScene);
            stage.setTitle("Result");
            stage.show();

        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}