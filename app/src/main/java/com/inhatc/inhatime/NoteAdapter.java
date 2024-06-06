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

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private static final String TAG = "NotaAdapter";
    ArrayList<Note> items = new ArrayList<Note>();

    @NonNull
    @Override
    //CheckBox와 btnRemove를 포함한 아이템 띄우기
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        Note item = items.get(position);
        holder.setItem(item);
        holder.setLayout();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void setItems(ArrayList<Note> items) {
        this.items = items;
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
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String TODO = (String) chkTodo.getText();
                    removeToDo(TODO);
                    Toast.makeText(view.getContext(), " 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }

                private void removeToDo(String TODO){

                }
            });
        }
        public void setItem(Note item) {
            chkTodo.setText(item.getTodo());
        }
        public void setLayout(){
            layoutTodo.setVisibility(View.VISIBLE);
        }
    }
}
