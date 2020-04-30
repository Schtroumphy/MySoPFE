package fr.eseo.hervy.sopfe.Models.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import fr.eseo.hervy.sopfe.Models.PseudoNotes;

/**
 * Created on 15/10/2019 - 08:07
 *
 * @author : HERVY Tiffaine
 * @filename : PseudoNotesDao
 */
@Dao
public interface PseudoNotesDao {

    @Query("Select * from PseudoNotes")
    List<PseudoNotes> findAllPseudoNotes();

    @Query("Select * from PseudoNotes where idProject=:idProject")
    LiveData<List<PseudoNotes>> findPseudoNotesIdProj(int idProject);

    @Insert
    long insertPseudoNotes(PseudoNotes pseudoNotes);
}
