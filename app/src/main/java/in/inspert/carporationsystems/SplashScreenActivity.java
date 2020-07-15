package in.inspert.carporationsystems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(CommonKeys.SHARED, MODE_PRIVATE);
        String userType = prefs.getString(CommonKeys.USER_TYPE, "none");

        if (userType.equals("none")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ActivityComplaintList.class);
            startActivity(intent);
        }

        finish();
    }
}
