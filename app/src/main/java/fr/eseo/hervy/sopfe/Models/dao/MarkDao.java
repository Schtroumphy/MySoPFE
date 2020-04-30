package fr.eseo.hervy.sopfe.Models.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import fr.eseo.hervy.sopfe.Models.Mark;

/**
 * Created on 19/10/2019 - 17:01.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : MarkDao
 */
@Dao
public interface MarkDao {

    @Query("Select * from Mark")
    LiveData<List<Mark>> findAllMarks();

    @Query("Select * from Mark where username =:usernameUser and projectId = :idProject ")
    LiveData<List<Mark>> findAllMarkByUsernameAndIdProject(String usernameUser, int idProject);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMark(Mark mark);

    @Delete
    void deleteMark(Mark mark);

}
