package ir.talebi.notebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ir.talebi.notebook.adapter.NoteAdapter;
import ir.talebi.notebook.entity.Note;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton btnAddNote;
    RecyclerView noteList;
    NoteAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    List<Note> notes;
    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddNote = findViewById(R.id.btnAddNote);
        noteList = findViewById(R.id.note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("در حال دریافت یادداشت ها...");
        progressDialog.show();

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
         user = mAuth.getCurrentUser();

        noteList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        noteList.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(this,user,mAuth,fStore);
        notes = new ArrayList<>();
        noteList.setAdapter(adapter);

        btnAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AddNoteActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        notes.clear();
        adapter.notifyDataSetChanged();
        Query query = fStore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("date", Query.Direction.DESCENDING);
        query.addSnapshotListener(this, (value, error) -> {
            for (DocumentSnapshot document : value.getDocuments()) {
                Note note = document.toObject(Note.class);
                note.setId(document.getId());
                notes.add(note);
            }

            adapter.setNoteList(notes);
            adapter.notifyDataSetChanged();

            if (progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(Gravity.RIGHT);
        switch(item.getItemId()){
            case R.id.addNote:
                startActivity(new Intent(this,AddNoteActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                break;

            default:
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}