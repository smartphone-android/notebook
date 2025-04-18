package hku.cs.notebook.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import hku.cs.notebook.MainActivity;
import hku.cs.notebook.R;

public class LoginActivity extends AppCompatActivity {


    EditText username, password, reg_username, reg_password,
            reg_firstName, reg_lastName, reg_email, reg_confirmemail;
    Button login, signUp, reg_register;
    TextInputLayout txtInLayoutUsername, txtInLayoutPassword, txtInLayoutRegPassword;
    CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        txtInLayoutUsername = findViewById(R.id.txtInLayoutUsername);
        txtInLayoutPassword = findViewById(R.id.txtInLayoutPassword);
        rememberMe = findViewById(R.id.rememberMe);


        ClickLogin();


        //SignUp's Button for showing registration page
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickSignUp();
            }
        });


    }

    //This is method for doing operation of check login
    private void ClickLogin() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;
                if (username.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                            Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                    txtInLayoutUsername.setError("Username should not be empty");
                } else {
                    //Here you can write the codes for checking username
                }
                if (password.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                            Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                    txtInLayoutPassword.setError("Password should not be empty");
                } else {
                    //Here you can write the codes for checking password
                }

                if (rememberMe.isChecked()) {
                    //Here you can write the codes if box is checked
                } else {
                    //Here you can write the codes if box is not checked
                }

                if(isValid) {
                    //Here you can write the codes for checking login
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                }

            }

        });

    }

    //The method for opening the registration page and another processes or checks for registering
    private void ClickSignUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.register, null);
        builder.setView(dialogView);
        
        // 创建 AlertDialog 对象
        AlertDialog alertDialog = builder.create();

        reg_username = dialogView.findViewById(R.id.reg_username);
        reg_password = dialogView.findViewById(R.id.reg_password);
        reg_firstName = dialogView.findViewById(R.id.reg_firstName);
        reg_lastName = dialogView.findViewById(R.id.reg_lastName);
        reg_email = dialogView.findViewById(R.id.reg_email);
        reg_confirmemail = dialogView.findViewById(R.id.reg_confirmemail);
        reg_register = dialogView.findViewById(R.id.reg_register);
        txtInLayoutRegPassword = dialogView.findViewById(R.id.txtInLayoutRegPassword);

        reg_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;
                if (reg_username.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    reg_username.setError("Please fill out this field");
                } else {
                    //Here you can write the codes for checking username
                }
                if (reg_password.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    txtInLayoutRegPassword.setPasswordVisibilityToggleEnabled(false);
                    reg_password.setError("Please fill out this field");
                } else {
                    txtInLayoutRegPassword.setPasswordVisibilityToggleEnabled(true);
                    //Here you can write the codes for checking password
                }
                if (reg_firstName.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    reg_firstName.setError("Please fill out this field");
                } else {
                    //Here you can write the codes for checking firstname

                }
                if (reg_lastName.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    reg_lastName.setError("Please fill out this field");
                } else {
                    //Here you can write the codes for checking lastname
                }
                if (reg_email.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    reg_email.setError("Please fill out this field");
                } else {
                    //Here you can write the codes for checking email
                }
                if (reg_confirmemail.getText().toString().trim().isEmpty()) {
                    isValid = false;
                    reg_confirmemail.setError("Please fill out this field");
                } else {
                    //Here you can write the codes for checking confirmemail
                }
                if(isValid) {
                    alertDialog.dismiss(); // 使用 alertDialog 而不是 dialog
                }
            }
        });

        alertDialog.show(); // 使用 alertDialog 显示对话框
    }


}
