package fr.eseo.hervy.sopfe.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;

/**
 * Created on 11/10/2019 - 10:19.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : MembersClass
 */
@Entity(tableName = "Members")
public class Members implements Parcelable {

    String forename, surname;
    int idJury;

    public Members(String forename, String surname) {
        this.forename = forename;
        this.surname = surname;
    }

    protected Members(Parcel in) {
        forename = in.readString();
        surname = in.readString();
    }

    public static final Creator<Members> CREATOR = new Creator<Members>() {
        @Override
        public Members createFromParcel(Parcel in) {
            return new Members(in);
        }

        @Override
        public Members[] newArray(int size) {
            return new Members[size];
        }
    };

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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(forename);
        dest.writeString(surname);
    }
}
