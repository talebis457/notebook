package ir.talebi.notebook;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.talebi.notebook.entity.Note;

public class AddNoteActivity extends AppCompatActivity {
    EditText edtTitle,edtDesc;
    FloatingActionButton btnAdd;
    private ProgressDialog progressDialog;

    FirebaseFirestore fStore;
    FirebaseUser user;
    String keyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        edtTitle = findViewById(R.id.addNoteTitle);
        edtDesc = findViewById(R.id.addNoteContent);
        btnAdd = findViewById(R.id.fab);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("در حال افزودن یادداشت...");

        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTitle.getText().toString().isEmpty()){
                    edtTitle.setError("عنوان نمیتواند خالی باشد.");
                    return;
                }

                if (edtTitle.getText().toString().length() < 3){
                    edtTitle.setError("عنوان نمیتواند کمتر از 3 حرف  باشد.");
                    return;
                }

                if (edtDesc.getText().toString().isEmpty()){
                    edtDesc.setError(" نمیتواند خالی باشد.");
                    return;
                }

                if (edtDesc.getText().toString().length() < 3){
                    edtDesc.setError("نمیتواند کمتر از 3 حرف باشد.");
                    return;
                }


                Note note = new Note();
                note.setId(keyId);
                note.setTitle(edtTitle.getText().toString().trim());
                note.setDesc(edtDesc.getText().toString().trim());

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.getDefault());
                String formattedDate = df.format(c);

                note.setDate(formattedDate);

                addNote(note);
            }
        });

    }

    private void addNote(Note note) {
        DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document();
        docref.set(note).addOnSuccessListener(aVoid -> {
            Toast.makeText(AddNoteActivity.this, "Note Added.", Toast.LENGTH_SHORT).show();
            onBackPressed();

        }).addOnFailureListener(e -> Toast.makeText(AddNoteActivity.this, "Error, Try again.", Toast.LENGTH_SHORT).show());
    }
}