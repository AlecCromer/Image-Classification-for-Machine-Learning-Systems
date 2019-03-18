package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import controller.photoBrowserController;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import model.Photo;
import model.Tag;

/*
 
 //AND
SELECT i.*
FROM tagmap tm, item i, tags t
WHERE tm.tag_id = t.tag_id
AND (t.tag_title IN ('falco','smash'))
AND i.itemId = tm.photo_id
GROUP BY i.itemId
HAVING COUNT( i.itemId )=2

//OR

SELECT i.*
FROM tagmap tm, item i, tags t
WHERE tm.tag_id = t.tag_id
AND (t.tag_title IN ('falco', 'smash'))
AND i.itemId = tm.photo_id
GROUP BY i.itemId

//minus

SELECT i. *
FROM item i, tagmap tm, tags t
WHERE i.itemId = tm.photo_id
AND tm.tag_id = t.tag_id 
AND (t.tag_title IN ('Programming', 'Algorithms'))
AND i.itemId NOT IN (SELECT i.itemId FROM item i, tagmap tm, tags t WHERE i.itemId = tm.photo_id AND tm.tag_id = t.tag_id AND t.tag_title = 'Python')
GROUP BY i.itemId
HAVING COUNT( i.itemId ) =2
 */

public class searchDatabase {
	private String text;
	List<String> dedupedList;
	ArrayList<String> Array = new ArrayList<String>();

	int i;


	public void search(String searchString) {
		i = 0;
		String dedupedSearch = "";
		photoBrowserController PHD = new photoBrowserController();
		
		Scanner scan = new Scanner(searchString);
		while (scan.hasNext()) {
			String text = scan.next();
			if (text.startsWith("-")) {
				subtractString(text);
				System.out.println(text);
			} else {

				System.out.println(text);

				Array.add(text);

				// deduped is the tag list that has been removed of duplicates and blanks
				dedupedList = Array.stream().distinct().collect(Collectors.toList());
				System.out.println("deduped is" + dedupedList);
			}
		}
		for (String object : dedupedList) {

			System.out.println(object);
			if (i == 0) {
				dedupedSearch = "'" + object + "'";
				i++;
			} else {
				dedupedSearch = dedupedSearch + ",'" + object + "'";
				i++;
			}
		}

		// this needs to be in the search function
		// deduped search is ready to be entered\
		System.out.println(dedupedSearch);
		System.out.println(i);
		
        //PHD.updateTable(dedupedSearch, i);



		dedupedList.removeAll(dedupedList);
		Array.removeAll(Array);

	}

	private void searchSQL(String deduped) {
		photoBrowserController PHD = new photoBrowserController();
		// generates query into the userinfo table
		        //PHD.setSQLQuery("SELECT title, description, content FROM tagmap tm, item i, tags t WHERE tm.tag_id = t.tag_id AND (t.tag_title IN ("+deduped+")) AND i.itemId = tm.photo_id GROUP BY i.itemId HAVING COUNT( i.itemId )="+i);
		        //System.out.println("SELECT title, description, content FROM tagmap tm, item i, tags t WHERE tm.tag_id = t.tag_id AND (t.tag_title IN ("+deduped+")) AND i.itemId = tm.photo_id GROUP BY i.itemId HAVING COUNT( i.itemId )="+i);
		        //PHD.updateTable(deduped, i);
	}

	private void subtractString(String subtractText) {
			i++;

	}

}
