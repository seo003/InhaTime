package com.inhatc.inhatime;

public class Note {
    // 데이터를 담아둘 객체 클래스 정의
    private int _id;
    private String todo;
    private boolean completed;
    private String date;

    // 기본 생성자 (Firebase에서 필요)
    public Note() {
    }

    public Note(int _id, String todo, boolean completed, String date) {
        this._id = _id;
        this.todo = todo;
        this.completed = completed;
        this.date = date;
    }

    // Getter 및 Setter 추가
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
