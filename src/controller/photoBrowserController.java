package controller;
//Alec
//SELECT title, description, content FROM item i, tagmap tm, tags t WHERE i.itemId = tm.photo_id AND tm.tag_id = t.tag_id AND (t.tag_title IN ('smash')) AND i.itemId NOT IN (SELECT i.itemId FROM item i, tagmap tm, tags t WHERE i.itemId = tm.photo_id AND tm.tag_id = t.tag_id AND t.tag_title = 'falco') GROUP BY i.itemId HAVING COUNT( i.itemId ) =1
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javafx.scene.control.SingleSelectionModel;
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

public class photoBrowserController implements Initializable{
	
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

	ArrayList<String> tagArray = new ArrayList<String>();

	List<String> deduped;
	int tagId;
	int photoId;
	private Image image;
	private String red, green, blue;
	private ArrayList<String> image_color = new ArrayList<String>();
    String SQLQuery = "";
	

	private String text;
	List<String> dedupedList;
	List<String> dedupedListSubtract;
	ArrayList<String> Array = new ArrayList<String>();
	ArrayList<String> ArraySubtract = new ArrayList<String>();
	
	int searchCounter;


	String dedupedSearch ="";
	String dedupedSearchSubtract ="";

	@Override
	public void initialize(URL url, ResourceBundle arg1) {
			uploadImageDescription.setWrapText(true);
			activePhotoDescription.setWrapText(true);
			//setSQLQuery("select title, description, content FROM item");
			tableFill("select title, description, content FROM item");
		}

	public ObservableList<Photo> getItems() {
		return photoList.getItems();
	}

	public void tableFill(String string) {
		try {

			photoList.setItems(getPhotoList(string));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("UNABLE TO FILL TABLE");
			e.printStackTrace();
		}

		photoList.setOnMouseClicked((MouseEvent event) -> {
			//activePhotoDescription, activePhotoTitle, activePhoto
			//
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
	       imageview.setFitHeight(200);
	       //imageview.setFitWidth(200);

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
		root = FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
		scene = new Scene(root);
		stage.setScene(scene);
		
		
	}
	
	//takes formatted text and upodates the table
	public void updateTable(String searchString) {
		boolean subtract = false;
		boolean add = true;
		searchCounter = 0;
			photoBrowserController PHD = new photoBrowserController();
			
			Scanner scan = new Scanner(searchString);
			while (scan.hasNext()) {
				String text = scan.next();
				if (text.startsWith("-")) {
					System.out.println(text);
					ArraySubtract.add(text);

					// deduped is the tag list that has been removed of duplicates and blanks
					dedupedListSubtract = ArraySubtract.stream().distinct().collect(Collectors.toList());
					System.out.println("deduped subtract is" + dedupedListSubtract);
					subtract = true;
				} else {

					add = true;
					System.out.println(text);
					Array.add(text);

					// deduped is the tag list that has been removed of duplicates and blanks
					dedupedList = Array.stream().distinct().collect(Collectors.toList());
					System.out.println("deduped is" + dedupedList);
				}
			}

			if(add == true) {
				if(subtract ==true) {
					searchStringAdd();
					searchStringSubtract();

					tableFill("SELECT title, description, content FROM item i, tagmap tm, tags t WHERE i.itemId = tm.photo_id AND tm.tag_id = t.tag_id AND (t.tag_title IN ("+dedupedSearch+")) AND i.itemId NOT IN (SELECT i.itemId FROM item i, tagmap tm, tags t WHERE i.itemId = tm.photo_id AND tm.tag_id = t.tag_id AND t.tag_title = "+dedupedSearchSubtract+") GROUP BY i.itemId HAVING COUNT( i.itemId ) ="+searchCounter);
				}else{
					searchStringAdd();

					tableFill("SELECT title, description, content FROM tagmap tm, item i, tags t WHERE tm.tag_id = t.tag_id AND (t.tag_title IN ("+dedupedSearch+")) AND i.itemId = tm.photo_id GROUP BY i.itemId HAVING COUNT( i.itemId )="+searchCounter);
				}
			}else {
				
				tableFill("SELECT title, description, content FROM tagmap tm, item i, tags t WHERE tm.tag_id = t.tag_id AND (t.tag_title IN ("+dedupedSearch+")) AND i.itemId = tm.photo_id GROUP BY i.itemId HAVING COUNT( i.itemId )="+searchCounter);
			}

			dedupedSearchSubtract ="";
			searchCounter = 0;
			dedupedSearch = "";
			dedupedList.removeAll(dedupedList);
			Array.removeAll(Array);
			ArraySubtract.removeAll(ArraySubtract);
			
		}
	
	public void searchStringAdd() {
		searchCounter = 0;
		dedupedSearch = "";
		for (String object : dedupedList) {

			System.out.println(object);
			if (searchCounter == 0) {
				dedupedSearch = "'" + object + "'";
				searchCounter++;
			} else {
				dedupedSearch = dedupedSearch + ",'" + object + "'";
				searchCounter++;
			}

		}
		System.out.println(dedupedSearch);
	}
	
	public void searchStringSubtract() {
		searchCounter = 0;
		dedupedSearchSubtract ="";
		for (String object : dedupedListSubtract) {
			object = object.substring(1);
			System.out.println(object);
			if (searchCounter == 0) {
				dedupedSearchSubtract = "'" + object + "'";
				searchCounter++;
			} else {
				dedupedSearchSubtract = dedupedSearchSubtract + ",'" + object + "'";
				searchCounter++;
			}
		}
		System.out.println(dedupedSearchSubtract);

	}
	// User presses the select photo button and it gets displayed on screen
	public void selectPhoto(ActionEvent event){
		String file = fileChooser();
		Image image = new Image("file:" + file);
		uploadImageView.setImage(image);
		imageParser parser = new imageParser();
		image_color = parser.parse(file);

		System.out.println("The last value in the image_color array is "+image_color.get(image_color.size()-1));

			if(image_color.get(image_color.size()-1) == "duplicate"){
				uploadImageView.setImage(null);
				filePath = null;
				alert.ImageDuplicate();
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
		if (uploadCheck()) {
			stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
			root = FXMLLoader.load(getClass().getClassLoader().getResource("photoBrowser.fxml"));
			scene = new Scene(root);
			stage.setScene(scene);

		}

	}

	// The photo gets checked to see if the user added a title and description
	public boolean uploadCheck() throws Exception {

		System.out.println("User pressed the register submit button.");
		System.out.println("The user uploaded " + uploadImageTitle.getText());
		System.out.println(
				"The description for " + uploadImageTitle.getText() + " is " + uploadImageDescription.getText());

		// checks if the user left something blank


			if (filePath == null || uploadImageTitle.getText().isEmpty() || uploadImageDescription.getText().isEmpty()) {
				alert.UploadError();
				return false;
			} else {
				uploadToDatabase(uploadImageTitle.getText(), uploadImageDescription.getText());
				alert.UploadSuccessful();
				return true;
			}
	}

	// photo gets sent to database as a blob
	private void uploadToDatabase(String Title, String description) throws SQLException, FileNotFoundException {

			javaxt.io.Image imageStat = new javaxt.io.Image(filePath);
			java.util.HashMap<Integer, Object> exif = imageStat.getExifTags();
			String latitude = "NA";
			String longitude = "NA";
			String camera = "NA";
			try {
				double[] gps = imageStat.getGPSCoordinate();

				System.out.println("Camera: " + exif.get(0x0110));
				System.out.println("Latitude: " + gps[1]);
				System.out.println("Longitude: " + gps[0]);

				latitude = String.valueOf(gps[1]);
				longitude = String.valueOf(gps[0]);

				if (exif.get(0x0110) == null || exif.get(0x0110) == ""){
					camera = "NA";
				}else{
					camera = String.valueOf(exif.get(0x0110));
				}

			}catch(Exception e){
				latitude = "NA";
				longitude = "NA";
			}


			// generates a random ID
			int random = rand.nextInt(10000000) + 1;

				// generates query into the userinfo table
				String query = "INSERT INTO `item` (`itemId`, `title`, `description`, `content`, `camera`, `latitude`, `longitude`)" + " VALUES (?, ?, ?, ?, ?, ?, ?)";

			// insert preparedstatement into database
			PreparedStatement preparedStmt = null;
			try {
				preparedStmt = databaseConnector.getConnection().prepareStatement(query);
				InputStream inputStream = new FileInputStream(new File(filePath));
				preparedStmt.setInt(1, random);
				preparedStmt.setString(2, Title);
				preparedStmt.setString(3, description);
				preparedStmt.setBlob(4, inputStream);
				preparedStmt.setString(5, camera);
				preparedStmt.setString(6, latitude);
				preparedStmt.setString(7, longitude);
				preparedStmt.execute();
				photoId = random;
			} catch (SQLException e) {
				e.printStackTrace();
				// If the ID already exists, it throws this stack trace, and then it reruns, and
				// generates a new ID
				preparedStmt.close();
				uploadToDatabase(Title, description);
			}
				int counter = 0;
				for(int i = 0;i<16;i++) {
					preparedStmt = null;
					query = "INSERT INTO `item_color` (`item_id`, `section_number`, `red`, `green`, `blue`)" + " VALUES (?, ?, ?, ?, ?)";

					String red, green, blue;
					red = image_color.get(counter);
					counter++;
					green = image_color.get(counter);
					counter++;
					blue = image_color.get(counter);
					counter++;

					try {
						preparedStmt = databaseConnector.getConnection().prepareStatement(query);
						preparedStmt.setInt(1, random);
						preparedStmt.setInt(2, i);
						preparedStmt.setString(3, red);
						preparedStmt.setString(4, green);
						preparedStmt.setString(5, blue);
						preparedStmt.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						preparedStmt.close();

					}

				}

				// tags get added
				ForLoopArrayList();


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

	private void ForLoopArrayList() throws SQLException, FileNotFoundException {

		// scans each tag is the fixed arrayList
		for (String object : deduped) {
			String userName = "SELECT tags.tag_title FROM tags WHERE tag_title = '" + object + "';";
			PreparedStatement preparedStmt;
			preparedStmt = databaseConnector.getConnection().prepareStatement(userName);
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				System.out.println("Tag exists");
				uploadTagDatabase(object, true);
			} else {
				System.out.println("Tag doesn't exists");
				uploadTagDatabase(object, false);
			}
		}
	}

	// tag table gets updated
	private void uploadTagDatabase(String object, boolean exists) throws SQLException {
		// generates query into the userinfo table
		String query = "INSERT INTO `tags` (`tag_id`, `tag_title`)" + " VALUES (?, ?)";

		// generates a random ID
		int random = rand.nextInt(10000000) + 1;

		// insert preparedstatement into database
		if (!exists) {
			PreparedStatement preparedStmt = null;
			try {
				preparedStmt = databaseConnector.getConnection().prepareStatement(query);

				preparedStmt.setInt(1, random);
				preparedStmt.setString(2, object);
				preparedStmt.execute();

				tagId = random;

			} catch (SQLException e) {
				e.printStackTrace();
				// If the ID already exists, it throws this stack trace, and then it reruns, and
				// generates a new ID
				preparedStmt.close();
				uploadTagDatabase(object, exists);

			} finally {
				preparedStmt.close();
				// tag has been successfully added, now to update the tagmap

				updateTagMap(false, object);
			}
		} else {
			updateTagMap(true, object);
		}

	}

	private void updateTagMap(boolean exists, String object) throws SQLException {
		// generates query into the tagmap table
		String query = "INSERT INTO `tagmap` (`tagmap_id`, `photo_id`, `tag_id`)" + " VALUES (?, ?, ?)";

		// generates a random ID
		int random = rand.nextInt(1000000) + 1;
		System.out.println(random + " " + photoId + " " + tagId);
		if (!exists) {
			// If the tag did not exist before upload

			// insert preparedstatement into database
			PreparedStatement preparedStmt = null;
			try {
				preparedStmt = databaseConnector.getConnection().prepareStatement(query);

				preparedStmt.setInt(1, random);
				preparedStmt.setInt(2, photoId);
				preparedStmt.setInt(3, tagId);
				preparedStmt.execute();

			} catch (SQLException e) {
				e.printStackTrace();
				// If the ID already exists, it throws this stack trace, and then it reruns, and
				// generates a new ID
				preparedStmt.close();
				updateTagMap(exists, object);

			} finally {
				preparedStmt.close();

			}
			// tag map has been updated
		} else {
			System.out.println("The tag exists in database, retrieve info");
			String tagIdQuery = "SELECT tags.tag_id FROM tags WHERE tag_title = '" + object + "';";
			System.out.println(object);

			PreparedStatement preparedStmt;
			preparedStmt = databaseConnector.getConnection().prepareStatement(tagIdQuery);
			ResultSet result = preparedStmt.executeQuery();
			result.next();
			String tag_id = result.getString(1);
			System.out.println(tag_id);
			tagId = Integer.parseInt(tag_id);
			System.out.println(random + " " + photoId + " " + tagId);
			updateTagMap(!exists, object);

		}
	}
	
    public ObservableList<Photo>/*<String>*/  getPhotoList(String SQL) throws IOException
    {
        ObservableList<Photo>/*<String>*/ photos = FXCollections.observableArrayList();

        
        int i = 0;
        try(
                Connection conn = databaseConnector.getConnection();
                PreparedStatement displayprofile = conn.prepareStatement(SQL);
                ResultSet resultSet = displayprofile.executeQuery()

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

    		tableFill("select title, description, content FROM item");
    	}else {
    			System.out.println(getItems());
    			updateTable(searchTagBox.getText());

    	}
    }

}
