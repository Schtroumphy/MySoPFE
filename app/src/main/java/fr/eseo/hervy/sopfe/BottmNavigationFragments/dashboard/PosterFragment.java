package fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.hervy.sopfe.MainActivity;
import fr.eseo.hervy.sopfe.Manager.ConnexionManager;
import fr.eseo.hervy.sopfe.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;

public class PosterFragment extends Fragment {

    private ImageView imagePoster;
    private int projectId;
    private String urlPoster;
    private TextView text_error;
    private ProgressBar progressBarPoster;
    private BottomNavigationView bottomNavigationView;

    public PosterFragment(int projectId,BottomNavigationView bottomNavigationView) {
        this.projectId = projectId;
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_poster, container, false);

        progressBarPoster = (ProgressBar) root.findViewById(R.id.progressBarPoster);
        imagePoster=(ImageView) root.findViewById(R.id.imagePoster);
        text_error = (TextView) root.findViewById(R.id.text_error);

        imagePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                final View mView = getLayoutInflater().inflate(R.layout.poster_custom_layout, null);
                final PhotoView photoView = mView.findViewById(R.id.imageView);
                Picasso.with(getContext()).load(urlPoster).into(photoView);
                mBuilder.setView(mView);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        recoverPoster();
        return root;
    }

    private void recoverPoster() {
        /* Récupérer le username et le token pour pouvoir lancer la requête
         * On récupère également le pwd car si le token a expiré on relance la requête pour connecter l'utilisateur
         * */
        SharedPreferences pref = getActivity().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        final String token = pref.getString("token", "tokenDefault");
        final String password = pref.getString("password", "passwordDefault");

        //Si c'est visiteur, renvoyer un username et token valide
        if(username.equals("visiteur")){
            //Se connecter et avoir un token valide
            String url = SERVER_IP+ "LOGON&user="+ "jpo" + "&pass="+ "Lsm5hs51s9ks";
            Log.i("TAG 1",url);

            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // textView.setText("Response: " + response.toString());
                            try {
                                /* Display the repsonse from the server */
                                Log.i("TAG : ", ""+response.toString());
                                String result = response.getString("result");

                                if(result.equals("OK")){
                                    /* Sauvegarderle username et le password pour pouvoir regénérer le token ainsi que le token */
                                    SharedPreferences userPref = getContext().getSharedPreferences("UserPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = userPref.edit();

                                    editor.putString("username", username);
                                    editor.putString("password", password);
                                    editor.putString("token", response.getString("token"));

                                    editor.apply();

                                }else{
                                    String error_text = response.getString("error");
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Erreur : "+ error_text)
                                            .setMessage("Eechec lors de la reconnexion...")
                                            .show();

                                    getContext().startActivity(new Intent(getContext(), MainActivity.class));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("TAG : ", error.toString());

                        }
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest);

            //fin de connexion avec un token valide
            SharedPreferences newPref = getActivity().getSharedPreferences("UserPref", 0);
            final String newToken = newPref.getString("token", "tokenDefault");
            this.urlPoster = SERVER_IP + "POSTR&user=" + "jpo" + "&proj=" + projectId + "&style&token=" + newToken ;
        }
        else{
            this.urlPoster = SERVER_IP + "POSTR&user=" + username + "&proj=" + projectId + "&style&token=" + token;
        }


        Log.d("tag url",urlPoster);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlPoster, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            /* Display the repsonse from the server */
                            Log.i("TAG [RESPONSE POSTER] ", "" + response.toString());
                            final String result = response.getString("result");

                            /* Afficher un message d'erreur si le projet n'a pas de poster ou si l'utilisateur n'y a pas accès */
                            if ("KO".equals(result)) {
                                final String error_text = response.getString("error");
                                text_error.setVisibility(VISIBLE);
                                text_error.setText(error_text);

                                if (response.getString("error").equals(R.string.txt_error_invalid_credentials)) {
                                    text_error.setText(R.string.txt_reconnection);
                                    ConnexionManager.reconnectUser(username, password, getContext());
                                    recoverPoster();
                                    bottomNavigationView.getMenu().findItem(R.id.navigation_description).setEnabled(true);
                                    bottomNavigationView.getMenu().findItem(R.id.navigation_notes).setEnabled(true);
                                } else if (response.getString("error").equals("No Poster")) {
                                    text_error.setText(R.string.txt_no_data);
                                    progressBarPoster.setVisibility(GONE);
                                    bottomNavigationView.getMenu().findItem(R.id.navigation_description).setEnabled(true);
                                    bottomNavigationView.getMenu().findItem(R.id.navigation_notes).setEnabled(true);
                                } else {
                                    text_error.setText(R.string.txt_error_fail);
                                    progressBarPoster.setVisibility(GONE);
                                    bottomNavigationView.getMenu().findItem(R.id.navigation_description).setEnabled(true);
                                    bottomNavigationView.getMenu().findItem(R.id.navigation_notes).setEnabled(true);
                                }
                            }
                        } catch (JSONException ex) {
                            Log.i("TAG ERROR : ", ex.toString());
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imagePoster.setVisibility(View.VISIBLE);
                        Picasso.with(getContext()).load(urlPoster).into(imagePoster,new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                //do smth when picture is loaded successfully
                                bottomNavigationView.getMenu().findItem(R.id.navigation_description).setEnabled(true);
                                bottomNavigationView.getMenu().findItem(R.id.navigation_notes).setEnabled(true);
                                //isPosterCharged();
                            }

                            @Override
                            public void onError() {
                                //do smth when there is picture loading error
                                bottomNavigationView.getMenu().findItem(R.id.navigation_description).setEnabled(true);
                                bottomNavigationView.getMenu().findItem(R.id.navigation_notes).setEnabled(true);
                            }
                        });
                        try {
                            Thread.sleep(4500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("TAG ERROR ", error.toString());
                        progressBarPoster.setVisibility(GONE);

                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }

}