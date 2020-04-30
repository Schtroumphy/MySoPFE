package fr.eseo.hervy.sopfe.Manager;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.hervy.sopfe.Models.Members;

import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;

/**
 * Created on 08/10/2019 - 13:02.
 * Cette classe contiendra quelques requêtes faites au webservices
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : RequestsClass
 */
public class RequestsClass {

    public static final String POSTER_ATTRIBUTE = "poster";
    public static final String OK_ATTRIBUTE = "OK";
    public static final String KO_ATTRIBUTE = "KO";
    public static String PROJECTS_ATTRIBUTE = "projects";
    public static String PROJECT_ID_ATTRIBUTE = "projectId";
    public static String INFO_ATTRIBUTE = "info";
    public static String DESCRIP_ATTRIBUTE = "descrip";
    public static String JURIES_ATTRIBUTE = "juries";
    public static String FORENAME_ATTRIBUTE = "forename";
    public static String SURNAME_ATTRIBUTE = "surname";
    public static String MY_NOTE_ATTRIBUTE = "mynote";
    public static String AVG_NOTE_ATTRIBUTE = "avgNote";
    public static String USER_ID_ATTRIBUTE = "userId";
    public static String MEMBERS_ATTRIBUTE = "members";
    public static String DESCRIPTION_ATTRIBUTE = "description";
    public static String RESULT_ATTRIBUTE = "result";
    public static String ERROR_ATTRIBUTE = "error";

    private static long sleepTime = 3000;

    public static void getMembersList(final int projectIdParam, String username, String token, String password, final TextView txt_members, Context context) {
        final String url = SERVER_IP + "LIJUR&user=" + username + "&token=" + token;
        Log.i("[URL Connection] ", url);
        final RequestQueue queue = Volley.newRequestQueue(context);
        final List<String> memberList = new ArrayList<>();
        queue.getCache().clear();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try { /* Display the response from the server */
                    final String result = response.getString(RESULT_ATTRIBUTE);
                    if (result.equals(OK_ATTRIBUTE)) {
                        for (int i = 0; i < response.getJSONArray(JURIES_ATTRIBUTE).length(); i++) {
                            for (int j = 0; j < response.getJSONArray(JURIES_ATTRIBUTE).getJSONObject(i).getJSONObject(INFO_ATTRIBUTE).getJSONArray(PROJECTS_ATTRIBUTE).length(); j++) {
                                final int projectId = Integer.parseInt(response.getJSONArray(JURIES_ATTRIBUTE).getJSONObject(i).getJSONObject(INFO_ATTRIBUTE).getJSONArray(PROJECTS_ATTRIBUTE).getJSONObject(j).getString(PROJECT_ID_ATTRIBUTE));
                                if (projectId == projectIdParam) {
                                    for (int k = 0; k < response.getJSONArray(JURIES_ATTRIBUTE).getJSONObject(i).getJSONObject(INFO_ATTRIBUTE).getJSONArray(MEMBERS_ATTRIBUTE).length(); k++) {
                                        final String forename = response.getJSONArray(JURIES_ATTRIBUTE).getJSONObject(i).getJSONObject(INFO_ATTRIBUTE).getJSONArray(MEMBERS_ATTRIBUTE).getJSONObject(k).getString(FORENAME_ATTRIBUTE);
                                        final String surname = response.getJSONArray(JURIES_ATTRIBUTE).getJSONObject(i).getJSONObject(INFO_ATTRIBUTE).getJSONArray(MEMBERS_ATTRIBUTE).getJSONObject(k).getString(SURNAME_ATTRIBUTE);

                                        memberList.add(forename + " " + surname);
                                    }
                                }
                            }
                        }
                        txt_members.setText(memberList.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("[ERROR]", error.toString());
            }
        });

        queue.add(jsonObjectRequest);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
