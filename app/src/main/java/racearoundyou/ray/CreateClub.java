package racearoundyou.ray;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateClub extends Fragment {
    private EditText name;
    private EditText desc;
    private RelativeLayout RL;
    private Button  btn;
    private Club mClub;
    private HashMap<String, String>users;
    private FirebaseUser fbUser;
    private ClubDataSetListener mCallback;

    public CreateClub() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_club, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RL = (RelativeLayout) view;
        name = (EditText) RL.findViewById(R.id.club_name);
        desc = (EditText) RL.findViewById(R.id.club_desc);
        btn = (Button) RL.findViewById(R.id.create);
        users = new HashMap<>();
        mClub = new Club();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mClub.setName(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mClub.setName(name.getText().toString());
            }
        });
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mClub.setDescription(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mClub.setDescription(desc.getText().toString());
            }
        });
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        users.put(fbUser.getUid(), "Admin");
        mClub.setUsers(users);
        mClub.setUserAmount(users.size());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.SendClub(mClub);
                (getActivity()).getFragmentManager().beginTransaction().remove(CreateClub.this).commit();
            }
        });
    }

    public interface ClubDataSetListener{
        void SendClub(Club mClub);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            mCallback = (ClubDataSetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");

        }
    }

}
