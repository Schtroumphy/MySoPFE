package fr.eseo.hervy.sopfe.Models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created on 15/10/2019 - 08:37
 *
 * @author : tiffa
 * @filename : PseudoProject
 */
@Entity(tableName = "PseudoProject")

public class PseudoProject implements Parcelable {

    @PrimaryKey
    @NonNull
    int projectId;

    @ColumnInfo(name = "title")
    String title;
    @ColumnInfo(name = "descrip")
    String descrip;
    @ColumnInfo(name = "poster")
    String poster;


    public PseudoProject(int projectId, String title, String descrip, String poster) {
        this.projectId = projectId;
        this.title = title;
        this.descrip = descrip;
        this.poster = poster;
    }

    protected PseudoProject(Parcel in) {
        projectId = in.readInt();
        title = in.readString();
        descrip = in.readString();
    }

    public static final Creator<PseudoProject> CREATOR = new Creator<PseudoProject>() {
        @Override
        public PseudoProject createFromParcel(Parcel in) {
            return new PseudoProject(in);
        }

        @Override
        public PseudoProject[] newArray(int size) {
            return new PseudoProject[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(projectId);
        parcel.writeString(title);
        parcel.writeString(descrip);
    }
}
