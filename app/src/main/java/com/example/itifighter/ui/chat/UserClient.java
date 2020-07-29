package com.example.itifighter.ui.chat;

import android.app.Application;

public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
