package com.abhay.retrofitandroidtutorial.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.abhay.retrofitandroidtutorial.R;
import com.abhay.retrofitandroidtutorial.activities.LoginActivity;
import com.abhay.retrofitandroidtutorial.activities.MainActivity;
import com.abhay.retrofitandroidtutorial.activities.ProfileActivity;
import com.abhay.retrofitandroidtutorial.api.RetrofitClient;
import com.abhay.retrofitandroidtutorial.models.DefaultResponse;
import com.abhay.retrofitandroidtutorial.models.LoginResponse;
import com.abhay.retrofitandroidtutorial.models.User;
import com.abhay.retrofitandroidtutorial.storage.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private EditText editTextEmail, editTextName, editTextSchool;
    private EditText editTextCurrentPassword, editTextNewPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextName = view.findViewById(R.id.editTextName);
        editTextSchool = view.findViewById(R.id.editTextSchool);
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);

        view.findViewById(R.id.buttonSave).setOnClickListener(this);
        view.findViewById(R.id.buttonChangePassword).setOnClickListener(this);
        view.findViewById(R.id.buttonLogout).setOnClickListener(this);
        view.findViewById(R.id.buttonDelete).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonSave:
                updateProfile();
                break;
            case R.id.buttonChangePassword:
                updatePassword();
                break;
            case R.id.buttonLogout:
                logout();
                break;
            case R.id.buttonDelete:
                deleteUser();
                break;
        }
    }

    private void deleteUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?");
        builder.setMessage("This action is irreverisble...");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                User user = SharedPrefManager.getInstance(getActivity()).getUser();
                Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().deleteUser(user.getId());

                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {

                        if (!response.body().getErr()) {
                            SharedPrefManager.getInstance(getActivity()).clear();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {

                    }
                });

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void logout() {

        SharedPrefManager.getInstance(getActivity()).clear();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void updatePassword() {
        String currentpassword = editTextCurrentPassword.getText().toString().trim();
        String newpassword = editTextNewPassword.getText().toString().trim();

        if (currentpassword.isEmpty()) {
            editTextCurrentPassword.setError("Password Required");
            editTextCurrentPassword.requestFocus();
            return;
        }
        if (newpassword.isEmpty()) {
            editTextNewPassword.setError("Enter New Password");
            editTextNewPassword.requestFocus();
            return;
        }

        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi()
                .updatePassword(currentpassword, newpassword, user.getEmail());

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                Toast.makeText(getActivity(), response.body().getMsg(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {

            }
        });


    }

    private void updateProfile() {

        String email = editTextEmail.getText().toString().trim();
        String school = editTextSchool.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if (school.isEmpty()) {
            editTextSchool.setError("School is reuired");
            editTextSchool.requestFocus();
            return;
        }


        User user = SharedPrefManager.getInstance(getActivity()).getUser();

        Call<LoginResponse> call = RetrofitClient.getInstance()
                .getApi().updateUser(
                        user.getId(),
                        email,
                        name,
                        school
                );

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                if (!response.body().isError()) {
                    SharedPrefManager.getInstance(getActivity()).saveUser(response.body().getUser());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });


    }
}
