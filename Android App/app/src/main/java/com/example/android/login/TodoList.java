package com.example.android.login;

/**
 * Created by BALARAMAN on 02-08-2018 with love.
 */
public class TodoList {
    private String id, mTitle, desc;
    private int upvotes = 0, downvotes = 0;

    TodoList(String id, String title, String desc, int up, int down) {
        this.id = id;
        this.mTitle = title;
        this.desc = desc;
        this.upvotes = up;
        this.downvotes = down;
    }

    public String getId() {
        return id;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getDesc() {
        return desc;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

}