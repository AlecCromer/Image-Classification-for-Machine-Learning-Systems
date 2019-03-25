package controller;


import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import model.Photo;
import model.Tag;
import model.Search;

public class PhotoBrowserController implements Initializable{
	
	@FXML
    private TabPane tabPane;

    @FXML
    private Tab photosPane, ActivePhotoPane, InsertPhotoPane;


	@FXML
	private Button loginReturnPhotos, searchButton;

	@FXML
	private TableColumn<Photo, String> imageTitle, imageDescription;

	@FXML
	private TableColumn<Photo, Image> photoImage;

	@FXML
	private Button loginReturnSearchTags;

	@FXML
	private Button selectPhotoButton;

	@FXML
	private TextField uploadImageTitle, searchTagBox;

	@FXML
	private TextArea uploadImageDescription, activePhotoDescription, activePhotoTitle;

	@FXML
	private TextField uploadImageTag;

	//button 
	@FXML
	private Button addButton, activePhotoBackButton;

	@FXML
	private ImageView uploadImageView, activePhoto;

	@FXML
	private Button uploadPhotoButton;

	@FXML
	private Button insertPhotoReturnToLogin;

	@FXML
	private TableView<Tag> TagListTable;
	
	@FXML
	private TableView<Photo> photoList;
	
	@FXML
	private TableColumn<Tag, String> tagListInsert;


    
	@FXML
	private Stage stage;
	private AnchorPane root;
	private Scene scene;

	
	
	String filePath;

	AlertPopUps alert = new AlertPopUps();
	MainClass main = new MainClass();
	Random rand = new Random();
	Search search = new Search();

	ArrayList<String> tagArray = new ArrayList<String>();

	ArrayList<String> Array = new ArrayList<String>();
	ArrayList<String> ArraySubtract = new ArrayList<String>();
	List<String> deduped;

	private Image image;
	private String red, green, blue;
	private ArrayList<String> image_color = new ArrayList<String>();
    String SQLQuery = "";
	

	private String text;


	@Override
	public void initialize(URL url, ResourceBundle arg1){
			uploadImageDescription.setWrapText(true);
			activePhotoDescription.setWrapText(true);
		try {
			tableFill(Photo.queryImageList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ObservableList<Photo> getItems() {
		return photoList.getItems();
	}

	public void tableFill(ResultSet rs) {
		try {

			photoList.setItems(getPhotoList(rs));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("UNABLE TO FILL TABLE");
			e.printStackTrace();
		}

		photoList.setOnMouseClicked((MouseEvent event) -> {

            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
            	tabPane.getSelectionModel().select(ActivePhotoPane);
            	Photo photo = photoList.getSelectionModel().getSelectedItem();
            	System.out.println("User selected " + photo.getTitle() + " - "+photo.getDescription());
            	activePhotoTitle.setText(photo.getTitle());
            	activePhotoDescription.setText(photo.getDescription());
            	//convert 'content' to image
            	activePhoto.setImage(photo.getContent());
            }
        });
	imageTitle.setCellValueFactory(new PropertyValueFactory<Photo, String>("title"));
	imageDescription.setCellValueFactory(new PropertyValueFactory<Photo, String>("description"));
	photoImage.setCellFactory(param -> {
	       //Set up the ImageView
	       final ImageView imageview = new ImageView();
        imageview.setPreserveRatio(true);
	       imageview.setFitHeight(200);


	       //Set up the Table
	       TableCell<Photo, Image> cell = new TableCell<Photo, Image>() {
	           public void updateItem(Image item, boolean empty) {
	             if (item != null) {
	                  imageview.setImage(item);
	             }
	           }
	        };
	        // Attach the imageview to the cell
	        cell.setGraphic(imageview);
	        return cell;
	   });
	photoImage.setCellValueFactory(new PropertyValueFactory<Photo, Image>("content"));
	}



	// logs the user out of the app
	public void returnToLogin(ActionEvent event) throws Exception {
		System.out.println("User went back to the login page.");
		stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		root = FXMLLoader.load(getClass().getClassLoader().getResource("view/login.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
		
		
	}
	public void SaveImages() throws Exception{
		ImagePuller imgPuller = new ImagePuller();
		//selects the directory for the images to be stored
		imgPuller.directorySelect(searchTagBox.getText());


	}
	//takes formatted text and upodates the table
	public void updateTable(String searchString) throws Exception {
		boolean subtract = false;
		boolean add = false;
		search.setSearchCounter(0);

			
			Scanner scan = new Scanner(searchString);
			while (scan.hasNext()) {
				String text = scan.next();
				if (text.startsWith("-")) {
					System.out.println(text);
					ArraySubtract.add(text);

					// deduped is the tag list that has been removed of duplicates and blanks
					search.setDedupedListSubtract(ArraySubtract.stream().distinct().collect(Collectors.toList()));
					System.out.println("deduped subtract is" + search.getDedupedListSubtract());
					subtract = true;
				} else {

					add = true;
					System.out.println(text);
					Array.add(text);

					// deduped is the tag list that has been removed of duplicates and blanks
					search.setDedupedList(Array.stream().distinct().collect(Collectors.toList()));
					System.out.println("deduped is" + search.getDedupedList());
				}
			}

			if(add) {
				if(subtract) {
					search.searchStringAdd();
					search.searchStringSubtract();

					tableFill(Photo.searchSubtract(search.getDedupedSearch(), search.getDedupedSearchSubtract(), search.getSearchCounter()));
				}else{
					search.searchStringAdd();

					tableFill(Photo.searchAddOnly(search.getDedupedSearch(), search.getSearchCounter()));
				}
			}else {
				if(subtract){
					search.searchStringSubtract();
					tableFill(Photo.searchSubtractOnly(search.getDedupedSearchSubtract(), search.getSearchCounter()));
				}
			}

			search.setSearchCounter(0);
			if(add){
				search.removeAddList();
				Array.removeAll(Array);
				search.setDedupedSearch("");
			}
			if(subtract){
				search.removeSubtractList();
				ArraySubtract.removeAll(ArraySubtract);
				search.setDedupedSearchSubtract("");
			}

			
		}


	// User presses the select photo button and it gets displayed on screen
	public void selectPhoto(ActionEvent event) throws SQLException{


		String file = fileChooser();
		Image image = new Image("file:" + file);
		uploadImageView.setImage(image);

		if(!Photo.parseImage(file)){
            alert.ImageDuplicate();
            uploadImageView.setImage(null);
        }
	}

	// popup for user to select photo
	public String fileChooser() {

			Frame JFrame = new Frame();
			FileDialog fd = new FileDialog(JFrame, "Choose a file", FileDialog.LOAD);
			fd.setDirectory("C:\\");
			fd.setFile("*.jpg;*.jpeg;*.png");
			fd.setVisible(true);
			String filename = fd.getFile();
			String director = fd.getDirectory().replace('\\', '/');
			System.out.println(director + fd.getFile());
			if (filename == null) {
				System.out.println("User Cancelled the choice");
			} else {
				System.out.println("User chose " + filename);
				filePath = director + fd.getFile();
			}
			return director + fd.getFile();


	}

	// user presses the upload photo button
	public void uploadPhoto(ActionEvent event) throws Exception {
		System.out.println("User pressed the upload photo button");
		if (uploadCheck(uploadImageTitle.getText(),uploadImageDescription.getText())) {
			stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
			root = FXMLLoader.load(getClass().getClassLoader().getResource("view/photoBrowser.fxml"));
			scene = new Scene(root);
			stage.setScene(scene);

		}

	}

	// The photo gets checked to see if the user added a title and description
	public boolean uploadCheck(String title, String description) throws Exception {

		System.out.println("User pressed the register submit button.");
		System.out.println("The user uploaded " + title);
		System.out.println(
				"The description for " + title + " is " + description);

		// checks if the user left something blank
			if (filePath == null || title.isEmpty() || description.isEmpty()) {
				alert.UploadError();
				return false;
			} else {
                // photo and tags get added
			    Photo photoInsert = new Photo(filePath);
                Tag taginsert = new Tag(photoInsert.insertPhotoIntoDatabase(title, description));

                taginsert.scanTagList(deduped);

				alert.UploadSuccessful();
				return true;
			}
	}

	//when user presses "add tag"
	public void addTag(ActionEvent event) {

		//if not empty, scans the entered tag
		Scanner scan = new Scanner(uploadImageTag.getText());
		if (!uploadImageTag.getText().isEmpty()) {
			
			// add tag to array
			tagArray.add(scan.next().replaceAll("[^a-zA-Z]",""));

		}
		scan.close();
		
		// deduped is the tag list that has been removed of duplicates and blanks
		deduped = tagArray.stream().distinct().collect(Collectors.toList());
		System.out.println(deduped);

		//adds tag to the visible table
		TagListTable.setItems(getTagList());
		tagListInsert.setCellValueFactory(new PropertyValueFactory<Tag, String>("Tag_title"));
		
		//clears the entered tag from the add tag area
		uploadImageTag.clear();


	}

	// get tag list for table
	public ObservableList<Tag>/* <String> */ getTagList() {
		ObservableList<Tag>/* <String> */ tagList = FXCollections.observableArrayList();

		for (String object : deduped) {
			tagList.add(new Tag(object));
		}
		return tagList;
	}

	
    public ObservableList<Photo>/*<String>*/  getPhotoList(ResultSet rs) throws IOException
    {
        ObservableList<Photo>/*<String>*/ photos = FXCollections.observableArrayList();

        try(

                ResultSet resultSet = rs

		){
            while (resultSet.next()){
            	InputStream is = resultSet.getBinaryStream("content");
            	image = SwingFXUtils.toFXImage(ImageIO.read(is), null);
            	photos.add(new Photo(resultSet.getString("title"), resultSet.getString("description"), image ));


            }
        }catch(SQLException ex){
            databaseConnector.displayException(ex);
            System.out.println("You glubbed up Alec");
            return null;
        }
        return photos;


    }
    
    
    public void backToPhotoList() {
    	tabPane.getSelectionModel().select(photosPane);
    }
    
    public void search() throws Exception {

    	//if the search button is empty, it resets it to the full list
    	if (searchTagBox.getText().isEmpty()){
    		System.out.println("search box is empty");

			tableFill(Photo.queryImageList());
    	}else {
    			System.out.println(getItems());
    			updateTable(searchTagBox.getText());

    	}
    }

}
