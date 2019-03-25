package application;

import controller.AlertPopUps;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


public class ImageParser {
    AlertPopUps alert = new AlertPopUps();
    ArrayList<String> color = new ArrayList<String>();
    ArrayList<String> duplicateList = new ArrayList<String>();

    public ArrayList<String> parse(String file) throws SQLException {

        String imageValue = "";
        //instantiates the color averages
        //grey colors still have RGB values, but they are all equal to each other
        int avgRED = 0;
        int avgGREEN = 0;
        int avgBLUE = 0;

        try {
            //width then height
            BufferedImage image = ImageIO.read(new File(file));


            //takes image dimensions
            int width = image.getWidth();
            int height = image.getHeight();

            //if image resolution is not divisible by 6, slightly alters dimension
            while (width % 4 != 0) {
                width -= 1;
            }
            while (height % 4 != 0) {
                height -= 1;
            }
            System.out.println(width);
            System.out.println(height);


            //divides the image by 4 to get the section size
            int sectionWidth = width / 4;
            int sectionHeight = height / 4;


            //for loop for the 36 sections

            for (int x = 1; x <= 4; x++) {
                for (int y = 1; y <= 4; y++) {

                    //for loop that parses the image using the specified section sizes
                    for (int i = sectionWidth * x - sectionWidth; i < sectionWidth * x; i++) {
                        for (int j = sectionHeight * y - sectionHeight; j < sectionHeight * y; j++) {

                            //stores the total color value of specified pixel (i,j)
                            Color mycolor = new Color(image.getRGB(i, j));
                            avgRED += mycolor.getRed();
                            avgGREEN += mycolor.getGreen();
                            avgBLUE += mycolor.getBlue();



                        }

                    }
                    //takes the average colors of each section as it passes
                    avgRED = avgRED / (sectionHeight * sectionWidth);
                    avgGREEN = avgGREEN / (sectionHeight * sectionWidth);
                    avgBLUE = avgBLUE / (sectionHeight * sectionWidth);
                    color.add(String.valueOf(avgRED));
                    color.add(String.valueOf(avgGREEN));
                    color.add(String.valueOf(avgBLUE));

                    System.out.println(avgRED + " " + avgGREEN + " " + avgBLUE);



                }
            }
            if(checkDatabase()){
                color.add("duplicate");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return color;
    }

    private boolean checkDatabase() throws SQLException{
        String avgRED = "";
        String avgGREEN = "";
        String avgBLUE = "";
        int counter = 0;
        int DuplicateListCounter = 0;
        //tolerance range (number/255)
        int tolerance = 5;

        boolean PossibleDuplicate = false;
        int duplicate = 0;

        String query = "SELECT DISTINCT ic1.item_id from item_color AS ic0," +
                "item_color AS ic1, item_color AS ic2," +
                "item_color AS ic3, item_color AS ic4," +
                "item_color AS ic5, item_color AS ic6," +
                "item_color AS ic7, item_color AS ic8," +
                "item_color AS ic9, item_color AS ic10," +
                "item_color AS ic11, item_color AS ic12," +
                "item_color AS ic13, item_color AS ic14," +
                "item_color AS ic15 where ";
        //generates query into the image table
        //creates an array with the ids for every possible duplicate
        for (int i = 0; i < 16; i++) {
            avgRED = color.get(counter);
            counter++;
            avgGREEN = color.get(counter);
            counter++;
            avgBLUE = color.get(counter);
            counter++;

            query+= "ic"+i+".red BETWEEN " + (Integer.parseInt(avgRED) - tolerance) + " AND " + (Integer.parseInt(avgRED) + tolerance) + " AND "+"ic"+i+".green BETWEEN " + (Integer.parseInt(avgGREEN) - tolerance)
                    + " AND " + (Integer.parseInt(avgGREEN) + tolerance) + " AND "+"ic"+i+".blue BETWEEN " + (Integer.parseInt(avgBLUE) - tolerance) + " AND " + (Integer.parseInt(avgBLUE) + tolerance) + " AND "+"ic"+i+".section_number = "+i+ " AND ";
        }
        //finalized query to find duplicate in system
        query = query.substring(0, query.length()-5)+";";
        System.out.println(query);
        // insert preparedstatement into database
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = databaseConnector.getConnection().prepareStatement(query);
            ResultSet result = preparedStmt.executeQuery();
            if (result.next()) {
                return true;
            }else{
                return false;
            }



        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
        preparedStmt.close();

    }
        return false;
    }
}