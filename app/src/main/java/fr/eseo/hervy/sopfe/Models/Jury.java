package fr.eseo.hervy.sopfe.Models;

import java.util.List;

/**
 * Created on 25/09/2019 - 17:02.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : Jury
 */
public class Jury {

    int id;
    String date;
    List<Members> memberList;
    List<Project> projectList;

    public Jury() {
            }



    public Jury(int id, String date, List<Project> listProjects) {
        this.id = id;
        this.date = date;
        this.projectList = listProjects;
    }

    public Jury(int id, String date, List<Members> memberList, List<Project> projectList) {
        this.id = id;
        this.date = date;
        this.memberList = memberList;
        this.projectList = projectList;
    }

    public List<Members> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Members> memberList) {
        this.memberList = memberList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }
}
