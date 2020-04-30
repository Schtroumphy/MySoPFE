package fr.eseo.hervy.sopfe.Models;

import java.util.List;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created on 25/09/2019 - 17:03.
 * Modified on 26/09/2019 - 14:51.
 * @author : JEAN-LOUIS Thessalène & HERVY Tiffaine
 * @filename : Project
 */
@Entity(tableName = "Project")
public class Project implements Parcelable {

    int projectId;

    String title;
    String descrip;
    int confid;
    String poster;
    int idJury;
    Supervisor supervisor;
    List<Students> ListStudent;


    public Project(int projectId, String title, String descrip, int confid, String poster, int idJury, Supervisor supervisor) {
        this.projectId = projectId;
        this.title = title;
        this.descrip = descrip;
        this.confid = confid;
        this.poster = poster;
        this.idJury = idJury;
        this.supervisor = supervisor;
    }

    public Project(int projectId, String title, String descrip, int confid, String poster, Supervisor supervisor) {
        this.projectId = projectId;
        this.title = title;
        this.descrip = descrip;
        this.confid = confid;
        this.poster = poster;
        this.supervisor = supervisor;
    }

    public Project(int projectId, String title, String descrip, int confid) {
        this.projectId = projectId;
        this.title = title;
        this.descrip = descrip;
        this.confid = confid;
    }

    public Project(int projectId, String title, String descrip, String poster) {
        this.projectId = projectId;
        this.title = title;
        this.descrip = descrip;
        this.poster = poster;
    }

    public Project(int projectId, String title, int confid, String poster, Supervisor supervisor) {
        this.projectId = projectId;
        this.title = title;
        this.confid = confid;
        this.poster = poster;
        this.supervisor = supervisor;
    }

    public Project(int projectId, String title, String descrip, String poster, Supervisor supervisor, int confid, List<Students> ListStudent) {
        this.projectId = projectId;
        this.title = title;
        this.descrip = descrip;
        this.poster = poster;
        this.supervisor = supervisor;
        this.confid = confid;
        this.ListStudent = ListStudent;

    }

    public Project(int projectId, String title, String descrip){
        this.projectId = projectId;
        this.title = title;
        this.descrip = descrip;
    }

    protected Project(Parcel in) {
        projectId = in.readInt();
        title = in.readString();
        confid = in.readInt();
        descrip = in.readString();
        poster = in.readString();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getConfid() {
        return confid;
    }

    public void setConfid(int confid) {
        this.confid = confid;
    }



    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(projectId);
        parcel.writeString(title);
        parcel.writeInt(confid);
        parcel.writeString(descrip);
        parcel.writeString(poster);
    }
}
