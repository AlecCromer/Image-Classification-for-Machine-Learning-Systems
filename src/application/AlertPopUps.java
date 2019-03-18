package application;

import javax.swing.*;

public class AlertPopUps {
	JFrame f = new JFrame();
	
	public void RegisterError() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);  
		JOptionPane.showMessageDialog(dialog, "Please fill out the form correctly");
		System.out.println("User registered without filling the form properly");
	}
	
	public void RegisterSuccessful() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);  
		JOptionPane.showMessageDialog(dialog, "User registered successfully");
		System.out.println("User Successfully Registered");
	}
	public void LoginSuccessful() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);    
		JOptionPane.showMessageDialog(dialog, "Login successful");
		System.out.println("User Successfully Logged in");
}
	public void LoginError() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(dialog, "Username and/or password was incorrect");
		System.out.println("User failed to type in their login credentials correctly");
}

	public void UploadSuccessful() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);  
		JOptionPane.showMessageDialog(dialog, "Upload was successful");
		System.out.println("Upload was successful");
	}

	public void UploadError() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);  
		JOptionPane.showMessageDialog(dialog, "Please make sure you have filled out your image settings correctly.");
		System.out.println("User failed to upload the image correctly");
	}

	public void UserExists() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);  
		JOptionPane.showMessageDialog(dialog, "Sorry, that user already exists");
		System.out.println("User entered in an existing username");
	}

	public void ImageDuplicate() {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(dialog, "Duplicate Image");
		System.out.println("This image is a duplicate already found in the database");
	}
}