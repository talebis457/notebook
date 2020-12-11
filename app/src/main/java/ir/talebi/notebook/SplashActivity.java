package ir.talebi.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();


        Handler handler = new Handler();

        handler.postDelayed(() -> {
            if(fAuth.getCurrentUser() != null){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }else {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
            finish();
        },2000);
    }
}