package fr.eseo.hervy.sopfe.Models;

/**
 * Created on 25/09/2019 - 17:05.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : Supervisor
 */
public class Supervisor {

    String forename, surname;

    Supervisor(){

    }

    public Supervisor(String forename, String surname) {
        this.forename = forename;
        this.surname = surname;
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
}
