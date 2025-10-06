package com.example.abamailapp.api;

import androidx.lifecycle.MutableLiveData;

import com.example.abamailapp.entities.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private BackendAPI backendAPI;

    private MutableLiveData<User> userData = new MutableLiveData<>();

    public UserAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        backendAPI = retrofit.create(BackendAPI.class);
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public void createUser(User user) {
        backendAPI.createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userData.postValue(response.body());
                } else {
                    userData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userData.postValue(null);
            }
        });
    }

    public void fetchUserById(int id) {
        backendAPI.getUserById(id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userData.postValue(response.body());
                } else {
                    userData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userData.postValue(null);
            }
        });
    }
}
