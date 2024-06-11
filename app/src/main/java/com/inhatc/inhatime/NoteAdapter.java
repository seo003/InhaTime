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

    private ArrayList<Note> items;
    private DatabaseReference databaseReference;

    // 생성자
    public NoteAdapter(ArrayList<Note> items) {
        this.items = items;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("todos");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // todo_item 레이아웃을 인플레이트하여 ViewHolder 생성
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 현재 위치의 Note 객체를 가져와서 ViewHolder에 설정
        Note item = items.get(position);
        holder.setItem(item);

        // 삭제 버튼 클릭 리스너 설정
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

        // 체크박스 상태 변경 리스너 설정
        holder.chkTodo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            databaseReference.child(String.valueOf(item.get_id())).setValue(item);
        });
    }

    // 아이템의 총 개수 반환
    @Override
    public int getItemCount() {
        return items.size();
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
