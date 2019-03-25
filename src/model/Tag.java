package model;

import application.databaseConnector;

import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class Tag {


	
	public String tag_title;



	static int tagId;
	static int photoId;

	public String getTag_title() {
		return tag_title;
	}

	public void setTag_title(String tag_title) {
		this.tag_title = tag_title;
	}

	public Tag(String tag_title) {

		this.tag_title = tag_title;

	}

	public Tag(int photoId) {

		this.photoId = photoId;

	}

	public static boolean scanTagList(List<String> deduped) throws SQLException, FileNotFoundException {
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
		return true;
	}

	// tag table gets updated
	private static void uploadTagDatabase(String object, boolean exists) throws SQLException {

		Random rand = new Random();
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

	private static void updateTagMap(boolean exists, String object) throws SQLException {
		Random rand = new Random();
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
}
