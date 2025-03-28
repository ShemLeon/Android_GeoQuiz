package com.example.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geoquiz.util.NetworkConnectivityUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.geoquiz.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // связываем активность -> биндинг -> XML
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // инициализация аутентификации и БД с Firebase
        auth = FirebaseAuth.getInstance();

        // проверка на соединение с интернетом
        checkNetworkAndProceed();



        binding.tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //обращаемся к кнопке
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //проверяем или поля не пустые
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
                    // - мы прерываемся, если поля пустые
                    return;
                }
                // вызываем систему аутентификации ->передаем заполненные данные
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Вход успешен", Toast.LENGTH_SHORT).show();
                                    // Успешная аутентификация, переходим к ChooseActivity
                                    Toast.makeText(LoginActivity.this, "Вход успешен", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, ChooseActivity.class);
                                    startActivity(intent);
                                    finish(); // Закрываем LoginActivity, чтобы нельзя было вернуться назад

                                } else {
                                    Toast.makeText(LoginActivity.this, "Произошла какая-то оказия: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                        }});
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkAndProceed();
    }

    // Метод для проверки сети и перехода к нужной активности
    private void checkNetworkAndProceed() {
        Thread thread = new Thread(() -> {
            while (true) {
                if (!NetworkConnectivityUtil.isInternetAvailable(this)) {
                    runOnUiThread(() -> {
                        // Нет интернета: показываем картинку и скрываем элементы
                        binding.btnLogin.setVisibility(View.GONE);
                        binding.etEmail.setVisibility(View.GONE);
                        binding.etPassword.setVisibility(View.GONE);
                        binding.tvToRegister.setVisibility(View.GONE);
                        binding.ivNoInternet.setVisibility(View.VISIBLE);
                    });
                } else {
                    runOnUiThread(() -> {
                        // Интернет есть: скрываем картинку и проверяем аутентификацию
                        binding.btnLogin.setVisibility(View.VISIBLE);
                        binding.etEmail.setVisibility(View.VISIBLE);
                        binding.etPassword.setVisibility(View.VISIBLE);
                        binding.tvToRegister.setVisibility(View.VISIBLE);
                        binding.ivNoInternet.setVisibility(View.GONE);
                        // Проверяем аутентификацию только после восстановления интернет-соединения
                        if (auth.getCurrentUser() != null) {
                            Intent intent = new Intent(LoginActivity.this, ChooseActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;  // Выход из цикла после успешной проверки интернета
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();


        }
    }

