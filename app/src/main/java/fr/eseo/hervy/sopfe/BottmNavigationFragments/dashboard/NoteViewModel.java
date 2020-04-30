package fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.eseo.hervy.sopfe.Models.Annotation;
import fr.eseo.hervy.sopfe.Models.Mark;
import fr.eseo.hervy.sopfe.Models.PseudoNotes;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.Models.dao.AnnotationDao;
import fr.eseo.hervy.sopfe.Models.dao.PseudoNotesDao;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;

/**
 * Created on 22/10/2019 - 09:42.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : NoteViewModel
 */
public class NoteViewModel extends AndroidViewModel {
    private SoPFEDatabase soPFEDatabase;
    private LiveData<List<Mark>> markList;
    private LiveData<List<Annotation>> annotationsList;
    private LiveData<List<Annotation>> annotationListByUsernameAndProjectId;
    private LiveData<List<PseudoNotes>> pseudoNotesByIdPprojectList;
    private MutableLiveData<Integer> insertResult = new MutableLiveData<>();
    private LiveData<List<PseudoProject>> pseudoProjectList;

    private AnnotationDao annotationDao;
    private PseudoNotesDao pseudoNotesDao;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        soPFEDatabase = SoPFEDatabase.getDatabase(application.getApplicationContext());
        markList = soPFEDatabase.markDao().findAllMarks();
        annotationsList = soPFEDatabase.annotationDao().findAllAnnotations();
        markList = soPFEDatabase.markDao().findAllMarks();
        pseudoProjectList = soPFEDatabase.PseudoProjectsJuryDao().findAllPseudoProject();
        annotationDao = soPFEDatabase.annotationDao();
        pseudoNotesDao = soPFEDatabase.PseudoNotesDao();
    }

    public LiveData<List<PseudoProject>> getAllPseudoProject(){
        return pseudoProjectList;
    }

    public LiveData<List<Mark>> getLiveDataAllMarks() {
        return markList;
    }

    public LiveData<List<Annotation>> getLiveDataAllAnnotationByUsernameAndProjectId(String username, int projectId) {
        return annotationListByUsernameAndProjectId = soPFEDatabase.getDatabase(getApplication().getApplicationContext())
                .annotationDao().finfAllAnnotationByUsernameAndProjectId(username, projectId);
    }

    public LiveData<List<PseudoNotes>> getPseudoNotesByIdProjectList(int projectId) {
        pseudoNotesByIdPprojectList = soPFEDatabase.PseudoNotesDao().findPseudoNotesIdProj(projectId);
        return pseudoNotesByIdPprojectList;
    }

    public LiveData<List<Mark>> getAllMarkByUsernameAndIdProject(String username, int projectId) {
        markList = soPFEDatabase.markDao().findAllMarkByUsernameAndIdProject(username, projectId);
        return markList;
    }

    public void insertAnnotation(Annotation annotation) {
        //insertAnnotationAsync(annotation);
        new insertAnnotationAsync2(annotationDao).execute(annotation);
    }

    public void deleteAnnotation(Annotation annotation) {
        deleteAnnotationAsync(annotation);
    }

    public void insertPseudoNotes(PseudoNotes pseudoNotes) {
        insertPseudoNotesAsync(pseudoNotes);
    }

    private void insertAnnotationAsync(final Annotation annotation) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soPFEDatabase.annotationDao().insertAnnotation(annotation);
                    insertResult.postValue(1);
                } catch (Exception e) {
                    insertResult.postValue(0);
                }
            }
        }).start();
    }

    private static class insertAnnotationAsync2 extends AsyncTask<Annotation, Void, Void> {

        private AnnotationDao annotationDao;

        insertAnnotationAsync2(AnnotationDao dao) {
            this.annotationDao = dao;
        }

        @Override
        protected Void doInBackground(final Annotation... params) {
            annotationDao.insertAnnotation(params[0]);
            return null;
        }
    }
    private void insertPseudoNotesAsync(final PseudoNotes pseudoNotes) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soPFEDatabase.PseudoNotesDao().insertPseudoNotes(pseudoNotes);
                    insertResult.postValue(1);
                } catch (Exception e) {
                    insertResult.postValue(0);
                }
            }
        }).start();

    }

    private void deleteAnnotationAsync(final Annotation annotation) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soPFEDatabase.annotationDao().deleteAnnotation(annotation);
                    insertResult.postValue(1);
                } catch (Exception e) {
                    insertResult.postValue(0);
                }
            }
        }).start();

    }

}
