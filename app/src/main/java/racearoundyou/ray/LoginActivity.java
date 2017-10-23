package racearoundyou.ray;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNickname;
    private Database db;
    private FirebaseUser user;
    private User dbUser;
    private Map<String, Integer> interests = new HashMap<>();

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new Database();
        db.setUPDB();
        dbUser = new User();
        //load icon
        ImageView Icon = (ImageView) findViewById(R.id.icon);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/racearoundyou-164620.appspot.com/o/ray_nobackground.png?alt=media&token=767e5690-6279-47e6-92ed-9aef00a04f00")
                .into(Icon);
        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mNickname = (EditText) findViewById(R.id.field_nickname);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            dbUser.setFirstAuth(bundle.getBoolean("isFirstAuth"));
            if (dbUser.isFirstAuth()){
                if (user != null){
                    updateUI(user);
                }
            }
        }
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop()
    {
        super.onStop();
    }

    // [END on_stop_remove_listener]

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                mPasswordField.setError("Ненадёжный пароль.");
                                mPasswordField.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                mEmailField.setError("Неправильный e-mail или пароль.");
                                mEmailField.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                mEmailField.setError("Пользователь с таким e-mail уже зарегестрирован.");
                                mEmailField.requestFocus();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }

                    }
                });
        dbUser.setFirstAuth(true);
        signIn(email, password);
        // [END create_user_with_email]
    }

    public void getUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.UserRef().child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dbUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "CHECK DB RIGHTS", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        getUser();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else if (dbUser.isFirstAuth())
                            {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                db.UserRef().child(user.getUid()).setValue(dbUser);
                                updateUI(user);
                            } else
                                {
                                    finish();
                                }
                        }
                    });
                    // [END sign_in_with_email]
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.profile_fill).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.profile_fill).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v)
    {
        int i = v.getId();
        switch (i){
            case R.id.email_create_account_button:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.email_sign_in_button:
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.fill_profile_button:
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest updateUser = new UserProfileChangeRequest.Builder().setDisplayName(mNickname.getText().toString()).build();
                    user.updateProfile(updateUser).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()) {
                              dbUser.setFirstAuth(false);
                              dbUser.setNickname(user.getDisplayName());
                              dbUser.setEmail(user.getEmail());
                              dbUser.setInterests(interests);
                              db.UserRef().child(user.getUid()).setValue(dbUser);
                              LoginActivity.this.finish();
                          }
                      }
                    });
                } else
                    {
                        Log.d(TAG, "USER NULL!!!");
                    }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onCheckBoxClicked(View view){
        int counter = 0;
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.competetive:
                if (checked){
                    counter++;
                    interests.put(((CheckBox) view).getText().toString(), 5-counter);
                } else {
                    interests.put(((CheckBox) view).getText().toString(), 0);
                }

            case R.id.exhibition:
                if (checked){
                    counter++;
                    interests.put(((CheckBox) view).getText().toString(), 5-counter);
                }  else {
                    interests.put(((CheckBox) view).getText().toString(), 0);
                }
            case R.id.game:
                if (checked){
                    counter++;
                    interests.put(((CheckBox) view).getText().toString(), 5-counter);
                } else {
                    interests.put(((CheckBox) view).getText().toString(), 0);
                }
            case R.id.tourism:
                if (checked){
                    counter++;
                    interests.put(((CheckBox) view).getText().toString(), 5-counter);
                } else {
                    interests.put(((CheckBox) view).getText().toString(), 0);
                }
        }
    }

}

