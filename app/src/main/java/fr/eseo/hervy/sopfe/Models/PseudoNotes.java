package fr.eseo.hervy.sopfe.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created on 15/10/2019 - 08:08
 *
 * @author : HERVY Tiffaine
 * @filename : PseudoNotes
 */
@Entity(tableName = "PseudoNotes")

public class PseudoNotes {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "idProject")
    int idProject;

    @ColumnInfo(name = "message")
    String message;

    public PseudoNotes(String username, int idProject, String message) {
        this.username = username;
        this.idProject = idProject;
        this.message = message;
    }

    public PseudoNotes() {
        this.setIdProject(0);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getIdProject() {
        return idProject;
    }

    public String getMessage() {
        return message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
