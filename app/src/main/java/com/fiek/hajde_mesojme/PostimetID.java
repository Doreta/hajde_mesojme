package com.fiek.hajde_mesojme;


import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class PostimetID {

    @Exclude
    public String PostimetID;

    public <T extends PostimetID> T withId(@NonNull final String id) {
        this.PostimetID = id;
        return (T) this;
    }

}
