package model;


import application.databaseConnector;
import application.ImageParser;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;


public class Photo {

	private String Title, Description;
	private Image content;
	static String filePath;
	public static int photoId;
	static ArrayList<String> image_color = new ArrayList<String>();

	public Photo(String title, String description,  Image content) {
		this.Title = title;
		this.Description = description;
		this.content = content;
	}

	public Photo(String filePath){
		this.filePath = filePath;
	}

	////////////////////
	//Database Queries//
	////////////////////
	public static ResultSet queryImageList()throws Exception{
		return (databaseConnector.getConnection().prepareStatement("select title, description, content FROM item")).executeQuery();

	}

	public static ResultSet searchSubtract(String dedupedSearch, String dedupedSearchSubtract, int searchCounter )throws Exception{

		return (databaseConnector.getConnection().prepareStatement("SELECT title, description, content FROM item i, tagmap tm, tags t " +
				"WHERE i.itemId = tm.photo_id " +
				"AND tm.tag_id = t.tag_id " +
				"AND (t.tag_title IN ("+dedupedSearch+")) " +
				"AND i.itemId NOT IN " +
				"(SELECT i.itemId " +
				"FROM item i, tagmap tm, tags t " +
				"WHERE i.itemId = tm.photo_id " +
				"AND tm.tag_id = t.tag_id " +
				"AND t.tag_title = "+dedupedSearchSubtract+") " +
				"GROUP BY i.itemId " +
				"HAVING COUNT( i.itemId ) ="+searchCounter)).executeQuery();

	}

	public static ResultSet searchSubtractOnly(String dedupedSearchSubtract, int searchCounter )throws Exception{

		return (databaseConnector.getConnection().prepareStatement("SELECT title, description, content " +
				"FROM item i, tagmap tm, tags t " +
				"WHERE i.itemId = tm.photo_id AND tm.tag_id = t.tag_id " +
				"AND i.itemId " +
				"NOT IN " +
				"(SELECT i.itemId FROM item i, tagmap tm, tags t " +
				"WHERE i.itemId = tm.photo_id " +
				"AND tm.tag_id = t.tag_id " +
				"AND t.tag_title = "+dedupedSearchSubtract+") " +
				"GROUP BY i.itemId " +
				"HAVING COUNT( i.itemId ) ="+searchCounter)).executeQuery();

	}
	public static ResultSet searchAddOnly(String dedupedSearch, int searchCounter) throws Exception{
		return (databaseConnector.getConnection().prepareStatement("SELECT title, description, content " +
				"FROM tagmap tm, item i, tags t " +
				"WHERE tm.tag_id = t.tag_id " +
				"AND (t.tag_title IN ("+dedupedSearch+")) " +
				"AND i.itemId = tm.photo_id " +
				"GROUP BY i.itemId " +
				"HAVING COUNT( i.itemId )="+searchCounter)).executeQuery();
	}
	public static boolean parseImage(String file) throws SQLException{
		ImageParser parser = new ImageParser();

		image_color = parser.parse(file);

		if(image_color.get(image_color.size()-1) == "duplicate"){

			return false;
		}else{

			return true;
		}
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
	
	public static int insertPhotoIntoDatabase(String Title, String description) throws SQLException, FileNotFoundException {

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
		Random rand = new Random();
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
			insertPhotoIntoDatabase(Title, description);
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
		return photoId;
	}
}
