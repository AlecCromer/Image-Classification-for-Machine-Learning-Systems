package model;


import javafx.scene.image.Image;


public class Photo {
	
	private String Title, Description;
	private Image content;


	public Photo(String title, String description,  Image content) {
		this.Title = title;
		this.Description = description;
		this.content = content;
	}

	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}

	public Image getContent() {
		return content;
	}
	public void setContent(Image content) {
		this.content = content;
	}
	

}
