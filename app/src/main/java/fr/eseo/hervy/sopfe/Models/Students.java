package fr.eseo.hervy.sopfe.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 26/09/2019 - 14:59
 *
 * @author : HERVY Tiffaine
 * @filename : Students
 */
public class Students implements Parcelable {
    Integer id;
    Integer myNote;
    Integer avgNote;
    String forename, surname;

    public Students(){

    }

    public Students(int id, String forename, String surname) {
        this.forename = forename;
        this.surname = surname;
    }

    public Students(int id, String forename, String surname, int myNote, int avgNote) {
        this.id = id;
        this.myNote = myNote;
        this.avgNote = avgNote;
        this.forename = forename;
        this.surname = surname;
    }

    protected Students(Parcel in) {
        id = in.readInt();
        myNote = in.readInt();
        avgNote = in.readInt();
        forename = in.readString();
        surname = in.readString();
    }

    public static final Creator<Students> CREATOR = new Creator<Students>() {
        @Override
        public Students createFromParcel(Parcel in) {
            return new Students(in);
        }

        @Override
        public Students[] newArray(int size) {
            return new Students[size];
        }
    };

    public Integer getMyNote() {
        return myNote;
    }

    public void setMyNote(Integer myNote) {
        this.myNote = myNote;
    }

    public Integer getAvgNote() {
        return avgNote;
    }

    public void setAvgNote(Integer avgNote) {
        this.avgNote = avgNote;
    }

    public Integer getId() {
        return id;
    }

    public void setId (Integer id) {

        this.id = id;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(myNote);
        parcel.writeInt(avgNote);
        parcel.writeString(forename);
        parcel.writeString(surname);
    }
}
