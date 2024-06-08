package com.inhatc.inhatime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private static final String TAG = "NoteAdapter";
    private ArrayList<Note> items;
    private DatabaseReference databaseReference;

    public NoteAdapter(ArrayList<Note> items) {
        this.items = items;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("todos");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note item = items.get(position);
        holder.setItem(item);

        holder.btnRemove.setOnClickListener(v -> {
            int id = item.get_id();
            databaseReference.child(String.valueOf(id)).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        if (position >= 0 && position < items.size()) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            Toast.makeText(v.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(v.getContext(), "삭제에 실패했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        holder.chkTodo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            databaseReference.child(String.valueOf(item.get_id())).setValue(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<Note> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutTodo;
        CheckBox chkTodo;
        Button btnRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutTodo = itemView.findViewById(R.id.layoutTodo);
            chkTodo = itemView.findViewById(R.id.chkTodo);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void setItem(Note item) {
            chkTodo.setText(item.getTodo());
            chkTodo.setChecked(item.isCompleted());
        }
    }
}
