package fr.eseo.hervy.sopfe.Manager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import fr.eseo.hervy.sopfe.HomeActivity;
import fr.eseo.hervy.sopfe.MainActivity;
import fr.eseo.hervy.sopfe.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created on 24/09/2019 - 16:01.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : ConnexionManager
 */
public class ConnexionManager {
    public static String SERVER_IP = "https://192.168.4.240/pfe/webservice.php?q=";

    /* Allow https connexions */
    /* Code from : https://stackoverflow.com/questions/36043324/android-volley-error-trust-anchor-for-certification-path-not-found-only-in-r
    */
    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    /* Méthode permettant de reconnecter l'utilisateur par le biais de l'API en cas de token invalide
     *
     * */
    public static void reconnectUser(final String username, final String password, final Context context){
        boolean result=false;
        String url = SERVER_IP+ "LOGON&user="+ username + "&pass="+ password;
        Log.i("TAG 1",url);

        RequestQueue queue = Volley.newRequestQueue(context);
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
                                SharedPreferences userPref = context.getSharedPreferences("UserPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = userPref.edit();

                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.putString("token", response.getString("token"));

                                editor.apply();


                            }else{
                                String error_text = response.getString("error");
                                new AlertDialog.Builder(context)
                                        .setTitle("Erreur : "+ error_text)
                                        .setMessage("Eechec lors de la reconnexion...")
                                        .show();

                                context.startActivity(new Intent(context, MainActivity.class));

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

        queue.add(jsonObjectRequest);
    }



    public static void showNotification(View view, int notificationID, String titre, String contenu) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(view.getContext());

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(view.getContext(), MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sopfe_trans, 10)
                .setContentTitle(titre)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(contenu)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //NotificationId est un identificateur unique par notification qu'il faut définir (notificationID)
        notificationManager.notify(notificationID, notifBuilder.build());
    }
}
