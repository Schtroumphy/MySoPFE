package fr.eseo.hervy.sopfe.Models.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import fr.eseo.hervy.sopfe.Models.Annotation;

/**
 * Created on 08/10/2019 - 16:35
 *
 * @author : HERVY Tiffaine
 * @filename : AnnotationDao
 */

@Dao
public interface AnnotationDao {

    @Query("Select * from Annotation")
    LiveData<List<Annotation>> findAllAnnotations();

    @Query("Select * from Annotation where username =:usernameUser and idProject = :idProject ")
    LiveData<List<Annotation>> finfAllAnnotationByUsernameAndProjectId(String usernameUser, int idProject);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAnnotation(Annotation annotation);

    @Delete
    void deleteAnnotation(Annotation annotation);
}
