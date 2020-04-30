package fr.eseo.hervy.sopfe.Models.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fr.eseo.hervy.sopfe.Models.Members;
import fr.eseo.hervy.sopfe.Models.Project;

/**
 * Created on 14/10/2019 - 14:13.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename :ProjectDao
 */
@Dao
public interface ProjectDao {

    @Query("Select * from Project")
    List<Project> findAllProjects();

    @Query("Select * from Project where idJury = :idJury ")
    List<Members> findAllProjectsBuIdJury(int idJury);

    @Insert
    long insertMembers(Members members);

    @Delete
    void deleteMembers(Members members);

}
