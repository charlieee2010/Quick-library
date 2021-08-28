package com.sidduron.easy.Model.Entities;

public class BookAdapter {
    private String id;
    private String title;

    public BookAdapter(String id0, String title0){
        id = id0;
        title= title0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
