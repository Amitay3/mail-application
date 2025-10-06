package com.example.abamailapp.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.abamailapp.AppDB;
import com.example.abamailapp.DatabaseClient;
import com.example.abamailapp.SessionManager;
import com.example.abamailapp.api.BackendAPI;
import com.example.abamailapp.api.LoginRequest;
import com.example.abamailapp.entities.LoginResponse;
import com.example.abamailapp.entities.RegisterResponse;
import com.example.abamailapp.entities.User;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRepository {

    private final AppDB db;
    private final Context context;

    private final BackendAPI backendAPI;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String BASE_URL = "http://10.0.2.2:8080/api/";

    public UserRepository(Context context) {
        this.context = context.getApplicationContext();
        db = DatabaseClient.getInstance(this.context);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        backendAPI = retrofit.create(BackendAPI.class);
    }


    public void login(String email, String password, MutableLiveData<String> error, MutableLiveData<User> result) {
        LoginRequest request = new LoginRequest(email, password);

        backendAPI.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User backendUser = response.body().getUser();
                    String token = response.body().getToken();

                    SessionManager.setToken(token);

                    executor.execute(() -> {
                        // Try to find user in Room by email
                        User existing = db.userDao().getUserByEmail(backendUser.getMailAddress());
                        User userToSave;

                        if (existing == null) {
                            // Construct a proper Room user
                            userToSave = new User(
                                    backendUser.getUserName(),
                                    request.getPassword(),
                                    backendUser.getMailAddress(),
                                    backendUser.getImage() != null ? backendUser.getImage() : "default"
                            );
                            db.userDao().insert(userToSave);
                        } else {
                            // Update existing
                            userToSave = new User(
                                    backendUser.getUserName(),
                                    existing.getPassword(),
                                    backendUser.getMailAddress(),
                                    backendUser.getImage() != null ? backendUser.getImage() : existing.getImage()
                            );
                            userToSave.setId(existing.getId());
                            db.userDao().update(userToSave);
                        }

                        // Set logged in userId properly
                        SessionManager.setLoggedInUserId(userToSave.getId());

                        result.postValue(userToSave);
                    });

                } else {
                    error.postValue("Invalid credentials");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Registration
    public void register(String userName, String password, String verifiedPassword, String mailAddress, Uri imageUri,
                         MutableLiveData<String> error, MutableLiveData<User> result) throws IOException {

        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), userName);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), mailAddress);
        RequestBody passwordPart = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody verifiedPasswordPart = RequestBody.create(MediaType.parse("text/plain"), verifiedPassword);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = getFileFromUri(context, imageUri);
            Log.d("Register", "Sending image file: " + file.getAbsolutePath() + ", size: " + file.length());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        } else {
            Log.d("Register", "No image selected");
        }
        backendAPI.registerUser(namePart, passwordPart, verifiedPasswordPart, emailPart, imagePart)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("Register", "Response body: " + new Gson().toJson(response.body()));

                            User user = response.body().toUser();

                            executor.execute(() -> {
                                long id = db.userDao().insert(user);
                                user.setId((int) id);
//                                SessionManager.setLoggedInUserId(user.getId());
                                result.postValue(user);
                            });

                        } else {
                            // Read the error body returned by backend
                            try {
                                if (response.errorBody() != null) {
                                    String errorJson = response.errorBody().string();
                                    error.postValue("Registration failed: " + errorJson);
                                } else {
                                    error.postValue("Registration failed: Unknown error");
                                }
                            } catch (Exception e) {
                                error.postValue("Registration failed: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        error.postValue("Network error: " + t.getMessage());
                    }
                });
    }

    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload", ".tmp", context.getCacheDir());
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.close();
        inputStream.close();
        return tempFile;
    }

    public User getUserById(int userId) {
        return db.userDao().getUserById(userId);
    }

    public void insertUser(User user) {
        executor.execute(() -> db.userDao().insert(user));
    }


    public void fetchUserById(int userId, Consumer<User> callback) {
        backendAPI.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.accept(response.body());
                } else {
                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.accept(null);
            }
        });
    }







}
