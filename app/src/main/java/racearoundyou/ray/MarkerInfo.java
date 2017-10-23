package racearoundyou.ray;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MarkerInfo extends BottomSheetDialogFragment {
    private Event mEvent;
    private String mKey;
    private Database db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mKey = getArguments().getString("eKey");
       return inflater.inflate(R.layout.event_info_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final LinearLayout main = (LinearLayout) view;
        final TextView name = (TextView) main.findViewById(R.id.nameView);
        final TextView desc = (TextView) main.findViewById(R.id.desc);
        final TextView datetime = (TextView) main.findViewById(R.id.datetime);
        db = new Database();
        db.setUPDB();
        db.MarkersRef().child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEvent = dataSnapshot.getValue(Event.class);
                name.setText(mEvent.getName());
                desc.setText(String.valueOf(mEvent.getLongtitude()));
                datetime.setText(String.valueOf(mEvent.getLatitude()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
