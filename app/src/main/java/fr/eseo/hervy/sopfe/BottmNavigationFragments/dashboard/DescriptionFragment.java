package fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.eseo.hervy.sopfe.Manager.ConnexionManager;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.Supervisor;
import fr.eseo.hervy.sopfe.R;

import static android.view.View.GONE;
import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;
import static fr.eseo.hervy.sopfe.Manager.RequestsClass.getMembersList;

public class DescriptionFragment extends Fragment {

    private Supervisor supervisor;
    private Project projet;
    private TextView txt_supervisor, txt_description_project,txt_members, txt_confid, txt_error,txt_membersTitre,txt_confid_title;
    private int projectId;

    public DescriptionFragment(int projectId, Project projet, Supervisor supervisor) {
        this.projectId=projectId;
        this.supervisor = supervisor;
        this.projet = projet;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         final View root = inflater.inflate(R.layout.fragment_description, container, false);
        txt_members = (TextView) root.findViewById(R.id.txt_members);
        txt_confid = (TextView) root.findViewById(R.id.txt_confid);
        txt_error = (TextView) root.findViewById(R.id.txt_error);
        txt_membersTitre = (TextView) root.findViewById(R.id.txt_membersTitre);
        txt_confid_title = (TextView) root.findViewById(R.id.txt_confid_title);
        txt_supervisor = (TextView) root.findViewById(R.id.txt_supervisor);




        SharedPreferences pref = getContext().getSharedPreferences("UserPref", 0);
        String username = pref.getString("username", "userDefault");
        String token = pref.getString("token", "tokenDefault");
        String password = pref.getString("password", "passwordDefault");

        if(!username.equals("jpo")&&!username.equals("visiteur")){
            /* On remplit la cardView du Superviseur */
            txt_supervisor = (TextView) root.findViewById(R.id.txt_supervisor);
            txt_supervisor.setText(supervisor.getSurname() + " " + supervisor.getForename());
        }

        //on appelle la méthode pour ajouter la liste des membres

        getMembersList(projectId,username,token,password,txt_members,getContext());
        /* DESCRIPTION */
        txt_description_project = (TextView) root.findViewById(R.id.txt_description_project);

        if (!projet.getDescrip().equals("")) {
            txt_confid.setText("" + projet.getConfid());
            txt_description_project.setText(projet.getDescrip());
        } else {
            recoverDescription(username, password, token, projectId);
        }
        if(username.equals("visiteur")){
            txt_supervisor.setText("Vous êtes le jury aujourd'hui ! A vous de noter");
            txt_confid.setVisibility(GONE);
            txt_members.setVisibility(GONE);
            txt_confid_title.setVisibility(GONE);
            txt_membersTitre.setVisibility(GONE);
        }

        return root;
    }

    private void recoverDescription(final String username, final String password, final String token, final int projectId){
            String url = SERVER_IP+ "LIPRJ&user="+ username + "&token="+ token;
            Log.d("tag url",url);

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
                                    String title,descrip = "";
                                    int projectIdFound,confid = 0;

                                    Log.d("tag",response.getJSONArray("projects").toString());

                                    for (int i = 0; i < response.getJSONArray("projects").length(); i++) {
                                        projectIdFound = Integer.parseInt(response.getJSONArray("projects").getJSONObject(i).getString("projectId"));
                                        descrip=response.getJSONArray("projects").getJSONObject(i).getString("descrip");
                                        confid = response.getJSONArray("projects").getJSONObject(i).getInt("confid");

                                        if(projectIdFound == projectId){
                                            txt_confid.setText(""+confid);
                                            txt_description_project.setText(descrip);
                                        }
                                    }


                                }else{
                                    String error_text = response.getString("error");
                                    txt_error.setText(error_text);
                                    Log.i("Tag","ERROR");

                                    /* Reconnecter l'utilisateur afin d'avoir un token valide */
                                    /* Progress Bar "Reconnexion en cours"*/
                                    ConnexionManager.reconnectUser(username, password, getContext());
                                    recoverDescription(username,password,token, projectId);

                                }
                            } catch (JSONException e) {
                                Log.d("tag","catch ici");
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("TAG : ", error.toString());

                        }
                    });

            queue.add(jsonObjectRequest);
    }
}