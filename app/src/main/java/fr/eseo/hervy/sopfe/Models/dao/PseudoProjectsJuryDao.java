package fr.eseo.hervy.sopfe.Models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import fr.eseo.hervy.sopfe.Models.PseudoProject;

/**
 * Created on 15/10/2019 - 08:02
 *
 * @author : HERVY Tiffaine
 * @filename : PseudoProjectsJuryDao
 */
@Dao
public interface PseudoProjectsJuryDao {
    @Query("Select * from PseudoProject")
    LiveData<List<PseudoProject>> findAllPseudoProject();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProject(PseudoProject PseudoProject);

}