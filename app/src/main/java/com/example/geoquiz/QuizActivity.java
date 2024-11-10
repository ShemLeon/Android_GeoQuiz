package com.example.geoquiz;

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



public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private String currentRightAnswer;
    private int currentQuestion = 1;
    private int currentScore = 0;
    private boolean wasHintRequested = false;
    private RadioButton selectedRadioButton;
    private int selectedRadioButtonId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        //здесь добавить логику с режимами игры, интент после ChooseActivity
        dbReference = database.getReference("Questions").child("Type_game").child("Easy");

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

    }

    private void loadCurrentQuestion(){
        clearSelection(); // Сброс выбора RadioButton перед загрузкой нового вопроса

        dbReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult().child(Integer.toString(currentQuestion));
                    processDataSnapshot(snapshot);
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
        String pictureUrl = snapshot.child("picture").getValue() != null ? snapshot.child("picture").getValue().toString() : "";
        String question = snapshot.child("question").getValue() != null ? snapshot.child("question").getValue().toString() : "";
        currentRightAnswer = snapshot.child("right").getValue() != null ? snapshot.child("right").getValue().toString() : "";

        List<String> questionAnswers = new ArrayList<>();

        for (DataSnapshot answerSnapShot: snapshot.child("answers").getChildren()) {
            String answer = answerSnapShot.getValue().toString();
            questionAnswers.add(answer);
        }
        // Glide - фича для преобразования ссылки в картинку на телефон.
        Glide.with(QuizActivity.this).load(pictureUrl).into(binding.ivQuestion);
        binding.tvQuestion.setText(question);
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
        * 2. Проверка ответа с правильным
        * 3. Начисление и отображение баллов в Score
        * 4. Тост с правильным ответом в случае ошибки.
        * 5. переключение на следующий вопрос
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
        } else {
            Toast.makeText(QuizActivity.this, "Right answer " + currentRightAnswer, Toast.LENGTH_SHORT).show();
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
