package ir.talebi.notebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText name,family,email,phone,password;
    private Button btnRegister;

    public static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String userID;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        name = findViewById(R.id.txt_name);
        family = findViewById(R.id.txt_family);
        email = findViewById(R.id.txt_email);
        phone = findViewById(R.id.txt_phone);
        password = findViewById(R.id.txt_password);
        btnRegister = findViewById(R.id.btn_register);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("در حال ثبت نام...");

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view -> {
            if (TextUtils.isEmpty(name.getText())) {
                name.setError("نام را وارد کنید.");
                return;
            }

            if (TextUtils.isEmpty(family.getText())) {
                family.setError("نام خانوادگی را وارد کنید.");
                return;
            }

            if (TextUtils.isEmpty(email.getText())) {
                email.setError("پست الکترونیکی را وارد کنید.");
                return;
            }

            if (TextUtils.isEmpty(phone.getText())) {
                phone.setError("شماره تلفن را وارد کنید.");
                return;
            }

            if (TextUtils.isEmpty(password.getText())) {
                password.setError("رمز عبور را وارد کنید.");
                return;
            }

            register(name.getText().toString(),family.getText().toString(),email.getText().toString(),phone.getText().toString(),password.getText().toString());
        });
    }

    private void register(String name,String family,String email,String phone,String password){
        if (!progressDialog.isShowing())
            progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fuser = firebaseAuth.getCurrentUser();
                        userID = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fName",name);
                        user.put("fFamily",family);
                        user.put("email",email);
                        user.put("phone",phone);
                        documentReference.set(user).addOnSuccessListener(aVoid -> {
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                            Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
                    } else {
                        Toast.makeText(this, "ایمیل معتبر و رمز عبور حداقل باید 6 رقم باشد.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
    }
}