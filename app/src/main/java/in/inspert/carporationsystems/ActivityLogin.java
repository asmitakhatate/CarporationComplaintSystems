package in.inspert.carporationsystems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityLogin extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    String email, password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(CommonKeys.USER);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                            String emailN = dataSnapshot1.child(CommonKeys.EMAIL).getValue(String.class);
                            Log.e("data",email+" -");

                            if (emailN.equals(email)) {

                                if (dataSnapshot1.child(CommonKeys.PASSWORD).getValue(String.class).equals(password)) {

                                    Toast.makeText(ActivityLogin.this, "Login Successful", Toast.LENGTH_LONG).show();

                                    String type = dataSnapshot1.child(CommonKeys.USER_TYPE).getValue(String.class);
                                    String name = dataSnapshot1.child(CommonKeys.NAME).getValue(String.class);
                                   // String address = dataSnapshot1.child(CommonKeys.ADDRESS).getValue(String.class);
                                   // String contact = dataSnapshot1.child(CommonKeys.CONTACT).getValue(String.class);

                                    SharedPreferences.Editor editor = getSharedPreferences(CommonKeys.SHARED, MODE_PRIVATE).edit();
                                    editor.putString(CommonKeys.USER_TYPE, type);
                                    editor.putString(CommonKeys.NAME, name);
                                    editor.apply();

                                    startActivity(new Intent(ActivityLogin.this, ActivityComplaintList.class));
                                    finish();

                                } else {
                                    Toast.makeText(ActivityLogin.this, "Password Not match", Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                       //     Toast.makeText(ActivityLogin.this, "Wrong Email", Toast.LENGTH_LONG).show();



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
