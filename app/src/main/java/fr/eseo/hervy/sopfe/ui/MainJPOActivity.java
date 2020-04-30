package fr.eseo.hervy.sopfe.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.hervy.sopfe.Adapters.ListProjectsAdapter;
import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.NoteViewModel;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;
import fr.eseo.hervy.sopfe.R;

import static android.view.View.GONE;

/**
 * Created on 15/10/2019 - 22:05
 *
 * @author : HERVY Tiffaine
 * @filename : MainJPOActivity
 */
public class MainJPOActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NoteViewModel noteViewModel;
    //private List<Project> projectList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //On rentre pas dans ce OnCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_projects);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);


        noteViewModel.getAllPseudoProject().observe(this, new Observer<List<PseudoProject>>() {
            @Override
            public void onChanged(List<PseudoProject> pseudoProjectList) {
                final List<String> projectTitleList = new ArrayList<>();
                final List<Project> projectList = new ArrayList<>();
                for(int i = 0; i < pseudoProjectList.size(); i++) {
                    projectList.add(new Project(pseudoProjectList.get(i).getProjectId(),pseudoProjectList.get(i).getTitle(),pseudoProjectList.get(i).getDescrip(),pseudoProjectList.get(i).getPoster()));
                    projectTitleList.add(pseudoProjectList.get(i).getTitle());
                }

                recyclerView = (RecyclerView) findViewById(R.id.recyclerlisteProjets);

                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                progressBar = (ProgressBar) findViewById(R.id.progressBarMyProject);
                Log.d("tag mainJPO",projectList.toString());
                ListProjectsAdapter myAdapter = new ListProjectsAdapter(getBaseContext(), projectList);
                recyclerView.setAdapter(myAdapter);
                progressBar.setVisibility(GONE);
            }
        });
        //List<PseudoProject> pseudoProjectList = SoPFEDatabase.getDatabase(this).PseudoProjectsJuryDao().findAllPseudoProject();


    }
}
