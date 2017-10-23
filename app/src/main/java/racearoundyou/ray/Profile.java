package racearoundyou.ray;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String email = bundle.getString("email");
        ArrayList<String> clubs = bundle.getStringArrayList("clubs");
        setContentView(R.layout.activity_profile);
        TextView nameView = (TextView) findViewById(R.id.nameView);
        TextView emailView = (TextView) findViewById(R.id.emailView);
        ListView clubsView = (ListView) findViewById(R.id.clubview);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.clublistprofile, clubs);
        nameView.setText(name);
        clubsView.setAdapter(adapter);
        emailView.setText(email);
    }
    @Override
    public void onStart(){
        super.onStart();
    }
}
