package com.fiek.hajde_mesojme;

import java.util.Date;


public class Postimet extends PostimetID {

    public String user_id, foto_url, pershkrimi, foto_thumb;
    public Date timestamp;//Date timestamp

    public Postimet() {
    }

    public Postimet(String user_id, String foto_url, String pershkrimi, String foto_thumb, Date timestamp) {
        this.user_id = user_id;
        this.foto_url = foto_url;
        this.pershkrimi = pershkrimi;
        this.foto_thumb = foto_thumb;
        this.timestamp = timestamp;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getfoto_url() {
        return foto_url;
    }

    public void setfoto_url(String foto_url) {
        this.foto_url = foto_url;
    }

    public String getpershkrimi() {
        return pershkrimi;
    }

    public void setpershkrimi(String pershkrimi) {
        this.pershkrimi = pershkrimi;
    }

    public String getfoto_thumb() {
        return foto_thumb;
    }

    public void setfoto_thumb(String foto_thumb) {
        this.foto_thumb = foto_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
