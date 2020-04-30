package fr.eseo.hervy.sopfe.Models.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import fr.eseo.hervy.sopfe.Models.Members;

/**
 * Created on 14/10/2019 - 14:08.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : MembersDao
 */
@Dao
public interface MembersDao {

    @Query("Select * from Members")
    List<Members> findAllMembers();

    @Query("Select * from Members where idJury = :idJury ")
    List<Members> findAllMmebersById(int idJury);

    @Insert
    long insertMembers(Members members);

    @Delete
    void deleteMembers(Members members);
}
