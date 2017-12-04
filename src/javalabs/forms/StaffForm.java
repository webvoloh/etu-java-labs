package javalabs.forms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.*;
import java.io.File;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import javalabs.classes.Staff;
import javalabs.libraries.Database;
import javalabs.libraries.Images;
import javalabs.libraries.CropImage;
import javalabs.models.StaffModel;


public class StaffForm{

    private HashMap<String, Integer> divisionMap = new HashMap< String, Integer>();

    private HashMap<String, Integer> positionMap = new HashMap< String, Integer>();

    private StaffModel staffTable;

    private ImageView photo;

    private TextField firstname;

    private TextField lastname;

    private ComboBox division;

    private ComboBox position;

    private Button saveButton;

    private Button uploadPhoto;

    public StaffForm(StaffModel parentContext){
        this.staffTable = parentContext;
    }

    private void chooseFile(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG files", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) photo.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        Image image = new Image(file.toURI().toString());
        CropImage cropped = new CropImage(image);
        photo.setImage(cropped.getImageView());
    }

    private void saveForm(){
        Stage stage = (Stage) saveButton.getScene().getWindow();
        String firstName = firstname.getText();
        String lastName = lastname.getText();
        int divisionId = divisionMap.get((String) division.getValue());
        int positionId = positionMap.get((String) position.getValue());
        try {
            Blob photoStream = Images.imageToMysqlBlob(photo);
            Staff.create(firstName, lastName, divisionId, positionId, photoStream);
        } catch (Exception e){
            e.printStackTrace();
        }
        staffTable.refresh();
        stage.close();
    }

    @SuppressWarnings("unchecked")
    public void init() throws Exception{
        Parent rooter = FXMLLoader.load(getClass().getResource("staffform.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(rooter);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Добавление сотрудника");
        firstname   = (TextField)   scene.lookup("#firstname");
        lastname    = (TextField)   scene.lookup("#lastname");
        division    = (ComboBox)    scene.lookup("#division");
        position    = (ComboBox)    scene.lookup("#position");
        photo       = (ImageView)   scene.lookup("#photo");
        uploadPhoto = (Button)      scene.lookup("#uploadPhoto");
        saveButton  = (Button)      scene.lookup("#saveButton");
        // Обработчики
        uploadPhoto.setOnMouseClicked(event -> {
            chooseFile();
        });
        saveButton.setOnMouseClicked(event -> {
            saveForm();
        });
        putDivisions();
        putPositions();
        stage.show();
    }

    @SuppressWarnings("unchecked")
    private void putDivisions(){
        ObservableList divisionsData = FXCollections.observableArrayList();
        Database db = new Database();
        List<Object[]> divisionList = db.query("SELECT id, division_name FROM divisions");
        for(Object[] row : divisionList){
            // Заполнение строк таблицы
            divisionMap.put((String)row[1], (Integer)row[0]);
            divisionsData.add(row[1]);
        }
        division.setItems(divisionsData);
    }

    @SuppressWarnings("unchecked")
    private void putPositions(){
        ObservableList positionsData = FXCollections.observableArrayList();
        Database db = new Database();
        List<Object[]> positionList = db.query("SELECT id, position_name FROM positions");
        for(Object[] row : positionList){
            // Заполнение строк таблицы
            positionMap.put((String)row[1], (int)row[0]);
            positionsData.add(row[1]);
        }
        position.setItems(positionsData);
    }
}
