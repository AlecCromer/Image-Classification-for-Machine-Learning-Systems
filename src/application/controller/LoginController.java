package controller;

import java.io.IOException;

import application.MainClass;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField username;
    @FXML
    private Button loginButton, registerButton;
    @FXML
    private PasswordField password;
    private Stage stage;
    private AnchorPane root;
    private Scene scene;
    @FXML
    private Node anyNode ;
    
	MainClass main = new MainClass();
    
    public void registerButton(ActionEvent event) throws Exception {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        root = FXMLLoader.load(getClass().getClassLoader().getResource("view/register.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
    }
    public void loginButton(ActionEvent event) throws Exception{
		System.out.println("User pressed the Login Button.");
		System.out.println(username.getText());
		System.out.println(password.getText());
    	main.LoginUser(username.getText(), password.getText(), event);

    }
	public void photoView(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        root = FXMLLoader.load(getClass().getClassLoader().getResource("view/photoBrowser.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
		
	}
}
