package fr.eseo.hervy.sopfe;

import androidx.appcompat.app.AppCompatActivity;

import fr.eseo.hervy.sopfe.Manager.RequestsClass;
import fr.eseo.hervy.sopfe.ui.MainJPOActivity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.view.View.GONE;
import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;
import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.handleSSLHandshake;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "125";
    private ProgressBar progressBar_main;
    EditText edt_password,edt_username;
    TextView txt_error,txt_visitor,txt_about;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_connect = (Button) findViewById(R.id.btn_connect);
        txt_visitor = (TextView) findViewById(R.id.txt_visitor);
        txt_about = (TextView) findViewById(R.id.txt_about);
        progressBar_main = (ProgressBar) findViewById(R.id.progressBar_main);

        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_username = (EditText)findViewById(R.id.edt_username);
        txt_error = (TextView) findViewById(R.id.txt_error);


        handleSSLHandshake();

        /* Création du canal de notifications */
        createNotificationChannel();

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar_main.setVisibility(View.VISIBLE);
                txt_error.setVisibility(View.INVISIBLE);
                /* Check the credentials entered by the user */
                String username = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                connectUser(username, password);
            }
        });
        txt_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On met dans les sharePref le username à visiteur pour plus tard
                SharedPreferences userPref = getApplicationContext().getSharedPreferences("UserPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = userPref.edit();
                editor.putString("username", "visiteur");
                editor.apply();
                //On renvoie dans l'activity MainJPO
                Intent intent = new Intent(MainActivity.this, MainJPOActivity.class);
                startActivity(intent);
            }
        });
        txt_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle(R.string.propos);
                builder1.setMessage(R.string.aboutTxt);
                builder1.setCancelable(true);
                builder1.setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    /* Méthode permettant de connecter l'utilisateur par le biais de l'API
    *
    * Penser à sauvegarder les identifiants de l'utilisateur dans les sharedPreferences pour reconnecter l'utilisateur afin
    * de regénérer
    * */
    public boolean connectUser(final String username, final String password) {
        boolean result=false;
        String url = SERVER_IP + "LOGON&user=" + username + "&pass=" + password;
        Log.d("[URL]", url);

        final RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                        /* Display the response from the server */
                        Log.i("TAG : ", "" + response.toString());
                            String result = response.getString(RequestsClass.RESULT_ATTRIBUTE);

                            if (result.equals(RequestsClass.OK_ATTRIBUTE)) {
                                /* Sauvegarderle username et le password pour pouvoir regénérer le token ainsi que le token */
                                final SharedPreferences userPref = getApplicationContext().getSharedPreferences("UserPref", 0); // 0 - for private mode
                                final SharedPreferences.Editor editor = userPref.edit();

                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.putString("token", response.getString("token"));

                                editor.apply();

                                /* Le rediriger vers la HomeActivity*/
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);

                                /* Après à mettre dans les Preferences */
                                intent.putExtra("TOKEN",response.getString("token"));
                                progressBar_main.setVisibility(GONE);
                                startActivity(intent);
                            } else if (result.equals("KO")) {
                                txt_error.setVisibility(View.VISIBLE);
                                String error_text = response.getString("error");
                                txt_error.setText(error_text);
                                progressBar_main.setVisibility(GONE);
                            } else {
                                txt_error.setVisibility(View.VISIBLE);
                                String error_text = response.getString("error");
                                txt_error.setText(error_text);
                                progressBar_main.setVisibility(GONE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("[ERROR] ", error.toString());
                        progressBar_main.setVisibility(GONE);

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setTitle(R.string.erreurCoTitre);
                        builder1.setMessage(R.string.txt_error_connexion);
                        builder1.setCancelable(true);
                        builder1.setNeutralButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                });

        queue.add(jsonObjectRequest);
        return result;
    }

    private void createNotificationChannel() {
        // Créer le NotificationChannel, seulement pour API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification channel name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Notification channel description");
            // Enregister le canal sur le système : attention de ne plus rien modifier après
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }
}
