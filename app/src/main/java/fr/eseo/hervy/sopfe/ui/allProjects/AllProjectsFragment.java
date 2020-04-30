package fr.eseo.hervy.sopfe.ui.allProjects;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.hervy.sopfe.Adapters.ListProjectsAdapter;
import fr.eseo.hervy.sopfe.Manager.ConnexionManager;
import fr.eseo.hervy.sopfe.Manager.RequestsClass;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.Students;
import fr.eseo.hervy.sopfe.Models.Supervisor;
import fr.eseo.hervy.sopfe.R;

import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;

public class AllProjectsFragment extends Fragment {

    private AllProjectsViewModel galleryViewModel;
    private CollationElementIterator txt_error;
    private ListView listeTousProjets;
    private List<Project> projectList;
    private List<String> projectTitleList;
    private RecyclerView recyclerView;
    private ProgressBar progressBarMyProject;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(AllProjectsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_projects, container, false);
        SharedPreferences pref = getContext().getSharedPreferences("UserPref", 0);
        String username = pref.getString("username", "userDefault");
        String token = pref.getString("token", "tokenDefault");
        String password = pref.getString("password", "passwordDefault");
        List<Project> listProjects = listProjects(username,password,token);

        // set up the RecyclerView
        progressBarMyProject = (ProgressBar) root.findViewById(R.id.progressBarMyProject);

        recyclerView = root.findViewById(R.id.recyclerlisteProjets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    public List<Project> listProjects(final String username, final String password, final String token) {
        final String url = SERVER_IP + "LIPRJ&user=" + username + "&token=" + token;
        Log.d("tag url",url);

        projectList = new ArrayList<Project>();
        projectTitleList = new ArrayList<String>();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            /* Display the repsonse from the server */
                            Log.d("tag reponse",response.toString());
                            String result = response.getString("result");
                            if (result.equals("OK")) {
                                Log.d("tag result ok","ok");
                                String title,descrip,supervisorForename,supervisorSurname,studentsSurname,studentsForename;
                                int projectId,confid;
                                String poster;
                                Supervisor supervisor;
                                List<Students> listStudents;
                                Log.d("tag",response.getJSONArray("projects").toString());

                                for (int i = 0; i < response.getJSONArray("projects").length(); i++) {
                                    projectId = Integer.parseInt(response.getJSONArray("projects").getJSONObject(i).getString("projectId"));
                                    title = response.getJSONArray("projects").getJSONObject(i).getString("title");
                                    descrip=response.getJSONArray("projects").getJSONObject(i).getString(RequestsClass.DESCRIP_ATTRIBUTE);
                                    confid = response.getJSONArray("projects").getJSONObject(i).getInt("confid");
                                    poster=response.getJSONArray("projects").getJSONObject(i).getString(RequestsClass.POSTER_ATTRIBUTE);

                                    supervisorSurname = response.getJSONArray("projects").getJSONObject(i).getJSONObject("supervisor").getString("surname");
                                    supervisorForename = response.getJSONArray("projects").getJSONObject(i).getJSONObject("supervisor").getString("forename");
                                    supervisor = new Supervisor(supervisorForename,supervisorSurname);

                                    JSONArray studentsArray = response.getJSONArray("projects").getJSONObject(i).getJSONArray("students");
                                    int sizeStudent = studentsArray.length();
                                    listStudents = new ArrayList<>();
                                    String forename, surname;
                                    int userId;
                                    for (int k = 0; k < sizeStudent; k++) {
                                        userId = studentsArray.getJSONObject(k).getInt("userId");
                                        forename = studentsArray.getJSONObject(k).getString("forename");
                                        surname = studentsArray.getJSONObject(k).getString("surname");
                                        Students student = new Students(userId,forename,surname);
                                        listStudents.add(student);
                                    }
                                    projectTitleList.add(response.getJSONArray("projects").getJSONObject(i).getString("title"));
                                    projectList.add(new Project(projectId, title, descrip, poster, supervisor, confid, listStudents));
                                }
                                ListProjectsAdapter myAdapter = new ListProjectsAdapter(getContext(), projectList);
                                recyclerView.setAdapter(myAdapter);
                                progressBarMyProject.setVisibility(View.GONE);

                            } else {
                                String error_text = response.getString("error");
                                txt_error.setText(error_text);
                                Log.i("[ERROR] ",error_text);

                                /* Reconnecter l'utilisateur afin d'avoir un token valide */
                                /* Progress Bar "Reconnexion en cours"*/
                                ConnexionManager.reconnectUser(username, password, getContext());
                                listProjects(username,password,token);

                            }
                        } catch (JSONException e) {
                            Log.d("[EXCEPTION] ","JsonException : "+ e.toString());
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("[RESPONSE ERROR] ", error.toString());

                    }
                });

        queue.add(jsonObjectRequest);
        return projectList;
    }
}