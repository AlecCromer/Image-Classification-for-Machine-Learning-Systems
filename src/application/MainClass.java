package application;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import controller.loginController;

public class MainClass extends Application {
	Random rand = new Random();
	AlertPopUps alert = new AlertPopUps();
	
	@Override
	public void start(Stage primaryStage) {
		try {

			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Login");
			primaryStage.setScene(scene);
			primaryStage.show();
			databaseConnector dbC = new databaseConnector();
			databaseConnector.getStartConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	//Activates when user presses the Register button
	public void RegisterUser(String username, String password, String name, String email) {

		//generates a random ID
		int random = rand.nextInt(1000) + 1;
		

		//generates query into the userinfo table
		String query = "INSERT INTO `userinfo` (`userId`, `username`, `password`, `name`, `email`)" + " VALUES (?, ?, ?, ?, ?)";

		// insert preparedstatement into database
		PreparedStatement preparedStmt;
		try {
			preparedStmt = databaseConnector.getConnection().prepareStatement(query);
			preparedStmt.setInt(1, random);
			preparedStmt.setString(2, username);
			preparedStmt.setString(3, password);
			preparedStmt.setString(4, name);
			preparedStmt.setString(5, email);
			preparedStmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			//If the ID already exists, it throws this stack trace, and then it reruns, and generates a new ID
			RegisterUser(username, password, name, email);
		}
		

	}
	
	//Check the database to see if user entered in information correctly
	public void LoginUser(String username, String password, ActionEvent event) throws IOException {
		//generates query into the userinfo table
				String query = "SELECT username, password FROM userinfo u WHERE u.username = '" +username+ "' and u.password='" +password+ "';";

				// insert preparedstatement into database
				PreparedStatement preparedStmt;
				try {
					preparedStmt = databaseConnector.getConnection().prepareStatement(query);
					ResultSet result = preparedStmt.executeQuery();
					if(result.next()) {
						alert.LoginSuccessful();
						loginController login = new loginController();
						login.photoView(event);
					}else {
						alert.LoginError();
					}

				} catch (SQLException e) {
					e.printStackTrace();

				}

	}


}
