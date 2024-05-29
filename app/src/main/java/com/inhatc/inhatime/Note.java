package com.inhatc.inhatime;

public class Note {
    // 데이터를 담아둘 객체 클래스 정의
    int _id;
    String todo;

    public Note(int _id, String todo) {
        this._id = _id;
        this.todo = todo;
    }

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
}
