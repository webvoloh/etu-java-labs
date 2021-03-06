package javalabs.classes;

import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javalabs.libraries.Database;

public class Staff extends User{
    private String division;
    private String position;

    public Staff(int id, String firstName, String lastName, String division, String position, String cardNumber, ImageView photo) {
        super(id, firstName, lastName, cardNumber, photo);
        this.division = division;
        this.position = position;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public static int create(String firstName, String lastName, Integer divisionId, Integer positionId, Blob photo) throws Exception{
        InputStream inputStream = photo != null ? photo.getBinaryStream() : null;
        String sql = "INSERT INTO staff (firstname, lastname, division_id, position_id, photo) values (?, ?, ?, ?, ?)";
        Connection connect = new Database().unsafeGetConnection();
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setInt(3, divisionId);
        ps.setInt(4, positionId);
        ps.setBlob(5, inputStream);
        if(ps.executeUpdate() > 0){
            connect.close();
            return 1;
        }
        connect.close();
        return 0;
    }

    public static int update(int id, String firstName, String lastName, Integer divisionId, Integer positionId, Blob photo) throws Exception{
        InputStream inputStream = photo.getBinaryStream();
        String sql = "UPDATE staff SET firstname = ?, lastname = ?, division_id = ?, position_id = ?, photo = ? WHERE id = ?";
        Connection connect = new Database().unsafeGetConnection();
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setInt(3, divisionId);
        ps.setInt(4, positionId);
        ps.setBlob(5, inputStream);
        ps.setInt(6, id);
        if(ps.executeUpdate() > 0){
            connect.close();
            return 1;
        }
        connect.close();
        return 0;
    }

    public static int delete(int id){
        String unlinkCardSql = "UPDATE cards SET staff_id = NULL, is_active = 0 WHERE staff_id = " + id;
        String dropStaffSql = "DELETE FROM staff WHERE id = " + id;
        Database db = new Database();
        try{
            db.update(unlinkCardSql);
            db.update(dropStaffSql);
            return 0;
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

}
