package application;

import java.io.*;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.Photo;
import model.Search;

import javax.swing.*;

public class ImagePuller {

    private static String parent_directory;

    public static String getChild_directory() {
        return child_directory;
    }

    public static void setChild_directory(String child_directory) {
        ImagePuller.child_directory = child_directory;
    }

    private static String child_directory;
    private static Image image;

    //sets the base directory
    public static void directorySelect(String searchString) throws Exception{
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choosertitle");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
            setParentDirectory(String.valueOf(chooser.getSelectedFile()));
            addDirectory(searchString);
            parseText(searchString);
        } else {
            System.out.println("No Selection ");
        }

    }

    //parses string and then returns the call needed for the database
    private static void parseText(String searchString) throws Exception{
        ArrayList<String> Array = new ArrayList<String>();
        ArrayList<String> ArraySubtract = new ArrayList<String>();
        Search search = new Search();
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
                getPositiveImages(Photo.searchSubtract(search.getDedupedSearch(), search.getDedupedSearchSubtract(), search.getSearchCounter()));

            }else{
                search.searchStringAdd();

                getPositiveImages(Photo.searchAddOnly(search.getDedupedSearch(), search.getSearchCounter()));
            }
        }else {
            if(subtract){
                search.searchStringSubtract();
                getPositiveImages(Photo.searchSubtractOnly(search.getDedupedSearchSubtract(), search.getSearchCounter()));
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

    //creates sub directories for the positive and negative images
    public static void addDirectory(String searchTag) {
        setChild_directory(searchTag);
        File file = new File(getParentDirectory()+"\\"+getChild_directory());
        File add = new File(getParentDirectory()+"\\"+getChild_directory()+"\\positive");
        File subtract = new File(getParentDirectory()+"\\"+getChild_directory()+"\\negative");

        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Base directory is created!");
            }
        }
        if (!add.exists()) {
            if (add.mkdir()) {
                System.out.println("Positive directory is created!");
            }
        }
        if (!subtract.exists()) {
            if (subtract.mkdir()) {
                System.out.println("Negative directory is created!");
            }
        }
    }


    public static String getParentDirectory() {
        return parent_directory;
    }

    public static void setParentDirectory(String parent_directory) {
        ImagePuller.parent_directory = parent_directory;
    }

    public Image getImage() {
        return image;
    }


    //gets every images in the database
    public static void getPositiveImages(ResultSet rs) throws IOException {
        boolean exists = false;
        ObservableList<Photo>/*<String>*/ photos = FXCollections.observableArrayList();

        int i = 0;
        try(

                ResultSet resultSet = rs


        ){
            while (resultSet.next()){

                i++;
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                InputStream is = resultSet.getBinaryStream("content");

                String filename = resultSet.getString("title")+i;
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                String fileType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(buffer));
                fileType = "."+fileType.split("/")[1];
                File targetFile = new File(getParentDirectory()+"\\"+getChild_directory()+"\\"+"positive"+"\\", filename+fileType);
                if(!targetFile.exists()){
                    targetFile.createNewFile();
                    OutputStream outStream = new FileOutputStream(targetFile);
                    outStream.write(buffer);
                }

            }
            System.out.println(photos);
        }catch(SQLException ex){
            databaseConnector.displayException(ex);
            System.out.println("You glubbed up Alec");
        }

    }

}
