package com.example.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.geoquiz.databinding.ActivityQuizBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;


public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private RadioButton selectedRadioButton;
    private String currentRightAnswer;
    private int currentQuestion = 1;
    private int currentScore = 0;
    private boolean wasHintRequested = false;
    private int selectedRadioButtonId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Используем DataBinding для инициализации
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получение режима игры из Intent
        String gameType = getIntent().getStringExtra("selected_game_type");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        //здесь добавить логику с режимами игры, интент после ChooseActivity

        if ("Easy".equals(gameType)) {
            dbReference = database.getReference("Questions").child("Type_game").child("Easy");
        } else if ("Hard".equals(gameType)) {
            dbReference = database.getReference("Questions").child("Type_game").child("Hard");
        } else if ("Nightmare".equals(gameType)) {
            dbReference = database.getReference("Questions").child("Type_game").child("Nightmare");
        } else if ("Clothing".equals(gameType)) {
            dbReference = database.getReference("Questions").child("Type_game").child("Clothing");
        } else if ("History".equals(gameType)) {
            dbReference = database.getReference("Questions").child("Type_game").child("History");
        } else if ("Sport".equals(gameType)) {
            dbReference = database.getReference("Questions").child("Type_game").child("Sport");

        } else {
            // Обработка случая, если режим не распознан
            Toast.makeText(this, "Неизвестный режим игры", Toast.LENGTH_SHORT).show();
            finish(); // Возврат к предыдущему экрану, если режим не распознан
            return;
        }

        loadCurrentQuestion();

        // Устанавливаем слушатели для RadioButton
        binding.radioAns1.setOnClickListener(this::onRadioButtonClicked);
        binding.radioAns2.setOnClickListener(this::onRadioButtonClicked);
        binding.radioAns3.setOnClickListener(this::onRadioButtonClicked);
        binding.radioAns4.setOnClickListener(this::onRadioButtonClicked);
        binding.radioAns5.setOnClickListener(this::onRadioButtonClicked);
        binding.radioAns6.setOnClickListener(this::onRadioButtonClicked);

        binding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processApplyButtonClick();
            }
        });

        // Обработчик для кнопки "Finish"
        binding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishQuiz();
            }
        });

        // Обработчик для кнопки "Hint"
        binding.btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHint();
            }
        });
    }

    private void showHint() {
        dbReference.child(Integer.toString(currentQuestion)).child("hint").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            String hint = task.getResult().getValue(String.class);
                            if (hint != null && !hint.isEmpty()) {
                                Toast.makeText(QuizActivity.this, " " + hint, Toast.LENGTH_LONG).show();
                                wasHintRequested = true; // Устанавливаем флаг, если использован hint
                            } else {
                                Toast.makeText(QuizActivity.this, "No hint available for this question.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(QuizActivity.this, "Failed to load hint.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void finishQuiz() {
        Intent intent = new Intent(QuizActivity.this, FinishActivity.class);
        intent.putExtra("SCORE", currentScore);
        startActivity(intent);
    }
    private void loadCurrentQuestion(){
        clearSelection(); // Сброс выбора RadioButton перед загрузкой нового вопроса

        dbReference.child(Integer.toString(currentQuestion)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        processDataSnapshot(snapshot);
                    } else {
                        // Переход к FinishActivity, если вопросов больше нет
                        finishQuiz();
                    }
                } else {
                    throw new RuntimeException(task.getException().getMessage());
                }
            }
        });


    }
    private void processDataSnapshot(DataSnapshot snapshot) {
        /**
         * Загрузка и установка контента из фаербейса
         */
        String pictureUrl = "";
        List<String> pictureUrls = new ArrayList<>();
        for (DataSnapshot pictureSnapShot : snapshot.child("pictures").getChildren()) {
            String url = pictureSnapShot.getValue().toString();
            pictureUrls.add(url);
        }
        if (!pictureUrls.isEmpty()) {
            int randomIndex = new Random().nextInt(pictureUrls.size());
            pictureUrl = pictureUrls.get(randomIndex);
        }

        String question = snapshot.child("question").getValue() != null ? snapshot.child("question").getValue().toString() : "";
        currentRightAnswer = snapshot.child("right").getValue() != null ? snapshot.child("right").getValue().toString() : "";

        List<String> questionAnswers = new ArrayList<>();
        for (DataSnapshot answerSnapShot : snapshot.child("answers").getChildren()) {
            String answer = answerSnapShot.getValue().toString();
            questionAnswers.add(answer);
        }

        // Glide - фича для преобразования ссылки в картинку на телефон.
        Glide.with(QuizActivity.this).load(pictureUrl).into(binding.ivQuestion);

        fillAnswerOptions(questionAnswers);
    }

    private void fillAnswerOptions(List<String> answerOptions) {
        // Заполнение XML данными из фаербейса
        if (answerOptions.size() < 6) {
            Toast.makeText(QuizActivity.this, "Not enough answer options", Toast.LENGTH_LONG).show();
            return;
        }
        // Создаем список индексов от 0 до 5 для рандомизации ответов
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            indices.add(i);
        }
        // Перемешиваем индексы случайным образом
        Collections.shuffle(indices);
        // Устанавливаем текст для радиокнопок, используя случайные индексы
        // Преобразуем текст, чтобы каждое слово начиналось с новой строки
        binding.radioAns1.setText(answerOptions.get(indices.get(0)));
        binding.radioAns2.setText(answerOptions.get(indices.get(1)));
        binding.radioAns3.setText(answerOptions.get(indices.get(2)));
        binding.radioAns4.setText(answerOptions.get(indices.get(3)));
        binding.radioAns5.setText(answerOptions.get(indices.get(4)));
        binding.radioAns6.setText(answerOptions.get(indices.get(5)));

    }

    private void onRadioButtonClicked(View view) {
        RadioButton radioButton = (RadioButton) view;
        // Снимаем выбор с предыдущего выбранного RadioButton, если он есть
        if (selectedRadioButton != null) {
            selectedRadioButton.setChecked(false);
        }
        // Устанавливаем новый выбранный RadioButton и его id
        selectedRadioButton = radioButton;
        selectedRadioButtonId = radioButton.getId();
        radioButton.setChecked(true);
    }
    private void processApplyButtonClick() {
        /*
         * Функция описывает собития после нажатия основной кнопки:
        * 1. Очистка выбора RadioButton
        * 2. Сравнение ответа с правильным
        * 3. Начисление и отображение баллов в Score
        * 4. Переключение на следующий вопрос
        */
        // Проверяем, выбран ли вообще какой-то RadioButton
        if (selectedRadioButtonId == -1) {
            Toast.makeText(QuizActivity.this, "Firstly choose the answer", Toast.LENGTH_SHORT).show();
            return;
        }
        // Получаем текст выбранного RadioButton
        String chosenAnswer = ((RadioButton) findViewById(selectedRadioButtonId)).getText().toString();

        // Проверяем ответ
        if (chosenAnswer.equals(currentRightAnswer) && !wasHintRequested) {
            currentScore += 2;
        }else if (chosenAnswer.equals(currentRightAnswer)) {
            currentScore += 1;
        }

        // Установка текста в поле Score:
        binding.tvScore.setText("Score: " + currentScore);

        //переключение на следующий вопрос
        currentQuestion++;
        loadCurrentQuestion();

    }
    private void clearSelection() {
        if (selectedRadioButton != null) {
            selectedRadioButton.setChecked(false);
            selectedRadioButton = null;
            selectedRadioButtonId = -1;
        }
    }

}
