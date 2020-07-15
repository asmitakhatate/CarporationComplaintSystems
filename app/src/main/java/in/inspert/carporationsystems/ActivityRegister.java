package in.inspert.carporationsystems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityRegister extends AppCompatActivity {
    EditText name, address, email, password, contact;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        name = findViewById(R.id.editTextName);
        address = findViewById(R.id.editTextAddress);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        contact = findViewById(R.id.editTextNumber);




        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid()) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(CommonKeys.USER).push();
                    //String key = myRef.push().getKey();
                    myRef.child(CommonKeys.NAME).setValue(name.getText().toString());
                    myRef.child(CommonKeys.ADDRESS).setValue(address.getText().toString());
                    myRef.child(CommonKeys.EMAIL).setValue(email.getText().toString());
                    myRef.child(CommonKeys.PASSWORD).setValue(password.getText().toString());
                    myRef.child(CommonKeys.CONTACT).setValue(contact.getText().toString());
                    myRef.child(CommonKeys.USER_TYPE).setValue(CommonKeys.USER_PEOPLE);

                    Toast.makeText(ActivityRegister.this, "Register successful", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(ActivityRegister.this, ActivityLogin.class));
                    finish();
                }

            }
        });

    }

    private boolean isValid(){
        if (!isValidEmail(email.getText().toString())){
            Toast.makeText(ActivityRegister.this, "Enter Valid email", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isValidMobile(contact.getText().toString())){
            Toast.makeText(ActivityRegister.this, "Enter Valid Number", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isValidPassword(password.getText().toString())){
            Toast.makeText(ActivityRegister.this, "Enter Valid password", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidMobile(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            Log.e("datata",phone+ " "+ phone.length());
            return phone.length() == 10;
        }
        return false;
    }
}
