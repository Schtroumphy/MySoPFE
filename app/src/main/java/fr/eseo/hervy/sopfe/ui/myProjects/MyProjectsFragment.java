package fr.eseo.hervy.sopfe.ui.myProjects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.List;

import fr.eseo.hervy.sopfe.Adapters.ListProjectsAdapter;
import fr.eseo.hervy.sopfe.Adapters.ListPseudoProjectAdapter;
import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.NoteViewModel;
import fr.eseo.hervy.sopfe.Manager.ConnexionManager;
import fr.eseo.hervy.sopfe.Manager.RequestsClass;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.Models.Students;
import fr.eseo.hervy.sopfe.Models.Supervisor;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;
import fr.eseo.hervy.sopfe.R;
import fr.eseo.hervy.sopfe.ui.allProjects.AllProjectsViewModel;

import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;

public class MyProjectsFragment extends Fragment {

    private AllProjectsViewModel galleryViewModel;
    private TextView txt_error;
    private ListView listeMesProjets;
    private List<Project> projectList;
    private List<String> projectTitleList;
    private RecyclerView recyclerView;
    private ProgressBar progressBarMyProject;
    private NoteViewModel noteViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(AllProjectsViewModel.class);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        View root = inflater.inflate(R.layout.fragment_my_projects, container, false);
        SharedPreferences pref = getContext().getSharedPreferences("UserPref", 0);
        String username = pref.getString("username", "userDefault");
        String token = pref.getString("token", "tokenDefault");
        String password = pref.getString("password", "passwordDefault");

        txt_error = (TextView) root.findViewById(R.id.txt_error);

        // set up the RecyclerView
        recyclerView = root.findViewById(R.id.recyclerlisteProjets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBarMyProject = (ProgressBar) root.findViewById(R.id.progressBarMyProject);
        List<Project> listProjects = listProjects(username,password,token);
        if(username.equals("jpo")){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setTitle("Oups");
            builder1.setMessage("Vous n'avez aucun projet à vous");
            builder1.setPositiveButton("Fermer",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        return root;
    }

    public List<Project> listProjects(final String username, final String password, final String token){
        final String url;
        final List<Project> projects = new ArrayList<>();
        if (username.equals("jpo")) {
            //url = SERVER_IP+ "PORTE&user="+ username + "&token="+ token;
            noteViewModel.getAllPseudoProject().observe(this, new Observer<List<PseudoProject>>() {
                @Override
                public void onChanged(List<PseudoProject> projectList) {

                    Log.d("tag [projectList] ", projectList.toString());
                    for (int k = 0; k < projectList.size();k++) {
                        projects.add(new Project(projectList.get(k).getProjectId(),projectList.get(k).getTitle(),projectList.get(k).getDescrip(),0));
                    }
                }
            });
            //List<PseudoProject> projectList = SoPFEDatabase.getDatabase(getContext()).PseudoProjectsJuryDao().findAllPseudoProject();

            //On envoie dans l'adapteur
            ListProjectsAdapter myAdapter = new ListProjectsAdapter(getContext(), projects);
            recyclerView.setAdapter(myAdapter);
            progressBarMyProject.setVisibility(View.GONE);


        }else {
            url = SERVER_IP + "MYPRJ&user=" + username + "&token=" + token;



            projectList = new ArrayList<Project>();
            projectTitleList = new ArrayList<String>();

            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                /* Display the repsonse from the server */
                                Log.d("tag reponse", response.toString());
                                String result = response.getString("result");
                                if (result.equals("OK")) {
                                    Log.d("tag result ok", "ok");
                                    String title, descrip, supervisorForename, supervisorSurname, studentsSurname, studentsForename;
                                    int projectId, confid;
                                    String poster;
                                    Supervisor supervisor;
                                    List<Students> listStudents;
                                    Log.d("tag", response.getJSONArray("projects").toString());
                                    if (response.getJSONArray("projects").length() > 0) {
                                        for (int i = 0; i < response.getJSONArray("projects").length(); i++) {
                                            if (username.equals("jpo")) {


                                        /*projectId = Integer.parseInt(response.getJSONArray("projects").getJSONObject(i).getString("idProject"));
                                        descrip=response.getJSONArray("projects").getJSONObject(i).getString("description");
                                        title = response.getJSONArray("projects").getJSONObject(i).getString("title");



                                        projectTitleList.add(response.getJSONArray("projects").getJSONObject(i).getString("title").toString());
                                        projectList.add(new Project(projectId,title,descrip));
                                        progressBarMyProject.setVisibility(View.GONE);*/

                                            } else {
                                                projectId = Integer.parseInt(response.getJSONArray("projects").getJSONObject(i).getString("projectId"));
                                                descrip = response.getJSONArray("projects").getJSONObject(i).getString("descrip");
                                                poster = response.getJSONArray(RequestsClass.PROJECTS_ATTRIBUTE).getJSONObject(i).getString(RequestsClass.POSTER_ATTRIBUTE);

                                                supervisorSurname = response.getJSONArray("projects").getJSONObject(i).getJSONObject("supervisor").getString("surname");
                                                supervisorForename = response.getJSONArray("projects").getJSONObject(i).getJSONObject("supervisor").getString("forename");
                                                supervisor = new Supervisor(supervisorForename, supervisorSurname);

                                                //confid = (response.getInt("confid"));

                                                Log.d("[POSTER] " + i, poster);

                                                JSONArray studentsArray = response.getJSONArray("projects").getJSONObject(i).getJSONArray("students");
                                                int sizeStudent = studentsArray.length();
                                                listStudents = new ArrayList<>();
                                                String forename, surname;
                                                int userId;
                                                for (int k = 0; k < sizeStudent; k++) {
                                                    userId = studentsArray.getJSONObject(k).getInt("userId");
                                                    forename = studentsArray.getJSONObject(k).getString("forename");
                                                    surname = studentsArray.getJSONObject(k).getString("surname");
                                                    Students student = new Students(userId, forename, surname);
                                                    listStudents.add(student);
                                                    Log.d("tag liste Students", listStudents.toString());
                                                }
                                                Log.d("tag liste etu complete", listStudents.toString());
                                                title = response.getJSONArray("projects").getJSONObject(i).getString("title");

                                                projectTitleList.add(response.getJSONArray("projects").getJSONObject(i).getString("title").toString());
                                                projectList.add(new Project(projectId, title, descrip, poster, supervisor,response.getJSONArray("projects").getJSONObject(i).getInt("confid") , listStudents));
                                                progressBarMyProject.setVisibility(View.GONE);
                                            }
                                        }
                                        ListProjectsAdapter myAdapter = new ListProjectsAdapter(getContext(), projectList);
                                        recyclerView.setAdapter(myAdapter);
                                    } else {
                                        progressBarMyProject.setVisibility(View.GONE);
                                    }
                                } else {
                                    String error_text = response.getString("error");
                                    txt_error.setText("Error : " + error_text + "\n Please logout and reconnect yourself.");
                                    Log.i("Tag", "ERROR");

                                    /* Reconnecter l'utilisateur afin d'avoir un token valide */
                                    /* Progress Bar "Reconnexion en cours"*/
                                    ConnexionManager.reconnectUser(username, password, getContext());
                                    listProjects(username, password, token);

                                }
                            } catch (JSONException e) {
                                Log.d("tag", "catch : " + e.toString());
                                progressBarMyProject.setVisibility(View.GONE);
                                txt_error.setText("Error : " + e.toString() + "\n Please logout and reconnect yourself.");
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("TAG : ", error.toString());

                        }
                    });

        /* Si l'utilisateur est connecté en tant que JPO, la fonction renverra une liste aléatoire de projets
           qui prend environ 8 secondes à afficher quelque chose donc on attend*/
        /*if (username.equals("jpo")) {
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }*/

            queue.add(jsonObjectRequest);
        }
        return projectList;
    }
}