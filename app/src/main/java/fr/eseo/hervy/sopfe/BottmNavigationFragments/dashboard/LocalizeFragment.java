package fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import fr.eseo.hervy.sopfe.R;

import static fr.eseo.hervy.sopfe.R.drawable.plan_poster;

public class LocalizeFragment extends Fragment {
    private String poster;
    private TextView txt_poster_position;
    private ImageView plan_poster;

    public LocalizeFragment(String poster) {
        this.poster = poster;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_localize, container, false);
        txt_poster_position = (TextView) root.findViewById(R.id.txt_poster_position);
        plan_poster = (ImageView) root.findViewById(R.id.plan_poster);

        txt_poster_position.setText("" + poster);

        plan_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                final View mView = getLayoutInflater().inflate(R.layout.poster_custom_layout, null);
                final PhotoView photoView = mView.findViewById(R.id.imageView);
                photoView.setBackgroundResource(R.drawable.plan_poster);
                mBuilder.setView(mView);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });


        return root;
    }

}
