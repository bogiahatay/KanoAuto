package com.kano.auto.db.api;


import androidx.annotation.NonNull;

public interface IOnApiRequest {
    void onApiSuccess(@NonNull String data) throws Exception;

    void onApiError(@NonNull String why) throws Exception;
}