package racearoundyou.ray;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database extends Application {
        private FirebaseDatabase mDatabase;
        private DatabaseReference Markers;
        private DatabaseReference Users;
        private DatabaseReference Clubs;
        FirebaseUser uid;

        public void setUPDB(){
            mDatabase = FirebaseDatabase.getInstance();
            Markers = mDatabase.getReference("markers");
            Users = mDatabase.getReference("users");
        }

        public void AddEvent(Event event){
            Markers.push().setValue(event);
        }

        public void setUserPosition(User user){
            uid = FirebaseAuth.getInstance().getCurrentUser();
                    if (uid !=null ){Users.child(uid.getUid()).setValue(user);}
            }

        public DatabaseReference MarkersRef(){
            Markers = mDatabase.getReference("markers");
            return Markers;
        }
        public DatabaseReference UserRef(){
            Users = mDatabase.getReference("users");
            return Users;
        }
    public DatabaseReference ClubRef(){
        Clubs = mDatabase.getReference("clubs");
        return Clubs;
    }
}