package fr.eseo.hervy.sopfe.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created on 19/10/2019 - 16:55.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : Mark Class
 */
@Entity(tableName = "Mark")
public class Mark {
    @PrimaryKey
    @NonNull
    String username;

    @ColumnInfo(name = "projectId")
    int projectId;

    @ColumnInfo(name = "studentId")
    int studentId;

    @ColumnInfo(name = "mark")
    int mark;

    @ColumnInfo(name = "message")
    String comment;

    public Mark(@NonNull String username, int projectId, int studentId, int mark, String comment) {
        this.username = username;
        this.projectId = projectId;
        this.studentId = studentId;
        this.mark = mark;
        this.comment = comment;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
