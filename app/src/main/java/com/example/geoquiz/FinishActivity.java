package com.example.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.geoquiz.databinding.ActivityFinishBinding;

public class FinishActivity extends AppCompatActivity {
    private ActivityFinishBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finish);

        // Инициализируем View Binding
        binding = ActivityFinishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем переданное значение Score из Intent
        int score = getIntent().getIntExtra("SCORE", 0);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Устанавливаем только цифру в TextView tv_finalScore
        binding.tvFinalScore.setText(String.valueOf(score));

        // Переход на ChooseActivity при нажатии на кнопку "Start"
        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(FinishActivity.this, ChooseActivity.class);
            startActivity(intent);
            finish(); // Закрыть FinishActivity, если не требуется возвращение к ней
        });
    }
}