package ir.talebi.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ir.talebi.notebook.entity.Note;

public class EditeNoteActivity extends AppCompatActivity {
    Intent data;
    EditText editNoteTitle,editNoteContent;
    FirebaseFirestore fStore;
    ProgressBar spinner;
    FirebaseUser user;

    Note note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = fStore.getInstance();
        spinner = findViewById(R.id.progressBar2);
        editNoteContent = findViewById(R.id.editNoteContent);
        editNoteTitle = findViewById(R.id.editNoteTitle);
        user = FirebaseAuth.getInstance().getCurrentUser();

        data = getIntent();
        note = (Note) data.getSerializableExtra("note");

        editNoteTitle.setText(note.getTitle());
        editNoteContent.setText(note.getDesc());


        FloatingActionButton fab = findViewById(R.id.saveEditedNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nTitle = editNoteTitle.getText().toString();
                String nContent = editNoteContent.getText().toString();

                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(EditeNoteActivity.this, "Can not Save note with Empty Field.", Toast.LENGTH_SHORT).show();
                    return;
                }

                spinner.setVisibility(View.VISIBLE);
                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(note.getId());
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.getDefault());
                String formattedDate = df.format(c);
                Map<String,Object> note = new HashMap<>();

                note.put("title",nTitle);
                note.put("desc",nContent);
                note.put("date",formattedDate);

                docref.update(note).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditeNoteActivity.this, "Note Updated.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                }).addOnFailureListener(e -> {
                    Toast.makeText(EditeNoteActivity.this, "Error, Try again.", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.VISIBLE);
                });


            }
        });
    }
}