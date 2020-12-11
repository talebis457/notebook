package ir.talebi.notebook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ir.talebi.notebook.R;
import ir.talebi.notebook.entity.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> noteList = new ArrayList<>();
    private SetOnClickNoteListIte setOnClickNoteListIte;
    FirebaseUser user;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    public NoteAdapter(Context context, FirebaseUser user, FirebaseAuth fAuth, FirebaseFirestore fStore) {
        this.context = context;
        this.user = user;
        this.fAuth = fAuth;
        this.fStore = fStore;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_note_layout,parent,false);
        return new NoteViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.desc.setText(note.getDesc());
        holder.date.setText(note.getDate());

        holder.itemView.setOnClickListener(view -> setOnClickNoteListIte.onClick(view,note));

        holder.delete.setOnClickListener(view -> {
            Log.i("Id =>",note.getId());
            DocumentReference docRef = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(note.getId());
            docRef.delete().addOnSuccessListener(aVoid -> {
                remove(position);
            }).addOnFailureListener(e -> Toast.makeText(context, "Error in Deleting Note.", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        if (noteList != null)
            return noteList.size();
        return 0;
    }

    public void remove(int position) {
        noteList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void setSetOnClickNoteListIte(SetOnClickNoteListIte setOnClickNoteListIte) {
        this.setOnClickNoteListIte = setOnClickNoteListIte;
    }

   public class NoteViewHolder extends RecyclerView.ViewHolder{
        public TextView title,desc,date;
        public ImageView delete;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            date = itemView.findViewById(R.id.date);
            delete = itemView.findViewById(R.id.menuIcon);
        }
    }

    public interface SetOnClickNoteListIte{
        void onClick(View view, Note note);
    }
}
