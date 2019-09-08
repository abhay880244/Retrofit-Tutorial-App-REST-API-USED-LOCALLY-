package com.abhay.retrofitandroidtutorial.models;

import com.google.gson.annotations.SerializedName;


public class DefaultResponse {
    @SerializedName("error")
    private boolean err;
    @SerializedName("message")
    private String msg;

    public DefaultResponse(boolean err, String msg) {
        this.err = err;
        this.msg = msg;
    }

    public boolean getErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
   /*
    //Or we can define fields with same name as keys of json object
    private String error;
    private String message;*/
}
