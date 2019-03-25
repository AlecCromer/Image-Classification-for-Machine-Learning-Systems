package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.MainClass;
import application.databaseConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegisterController {

	@FXML
	private TextField usernameRegister, passwordRegister, emailRegister, nameRegister;
	@FXML
	private Button registerButton, loginReturn;
	@FXML
	private Stage stage;
	private AnchorPane root;
	private Scene scene;
	@FXML

	AlertPopUps alert = new AlertPopUps();
	MainClass main = new MainClass();

	public void returnToLogin(ActionEvent event) throws Exception {
		System.out.println("User went back to the login page.");
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		root = FXMLLoader.load(getClass().getClassLoader().getResource("view/login.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
	}

	public void registerCheck(ActionEvent event) throws Exception {

		System.out.println("User pressed the register submit button.");
		System.out.println(nameRegister.getText());
		System.out.println(usernameRegister.getText());
		System.out.println(passwordRegister.getText());
		System.out.println(emailRegister.getText());

		// checks if the user left something blank
		if (nameRegister.getText().isEmpty() || usernameRegister.getText().isEmpty()
				|| passwordRegister.getText().isEmpty() || emailRegister.getText().isEmpty()) {

			alert.RegisterError();
		} else {
			String userName = "SELECT username FROM userinfo WHERE username = '" + usernameRegister.getText() + "';";
			PreparedStatement preparedStmt;
			preparedStmt = databaseConnector.getConnection().prepareStatement(userName);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				alert.UserExists();
			} else {
				main.RegisterUser(usernameRegister.getText(), passwordRegister.getText(), nameRegister.getText(),
						emailRegister.getText());
				alert.RegisterSuccessful();

				stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
				root = FXMLLoader.load(getClass().getClassLoader().getResource("view/login.fxml"));
				scene = new Scene(root);
				stage.setScene(scene);
			}
		}
	}
}
