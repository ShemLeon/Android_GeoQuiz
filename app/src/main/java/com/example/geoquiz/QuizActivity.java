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

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private String currentRightAnswer;
    private int currentQuestion = 1, currentScore = 0;
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
        // dbReference = database.getReference("Questions");
        dbReference = database.getReference("Questions").child("Type_game").child("Easy");
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

    private void processDataSnapshot(DataSnapshot snapshot) {
        /**
         * Загрузка и установка контента из фаербейса
         */
        String pictureUrl = snapshot.child("picture").getValue().toString();
        String question = snapshot.child("question").getValue().toString();
        currentRightAnswer = snapshot.child("answer").getValue().toString();

        List<String> questionAnswers = new ArrayList<>();

        for (DataSnapshot answerSnapShot: snapshot.child("answers").getChildren()) {
            String answer = answerSnapShot.getValue().toString();
            questionAnswers.add(answer);
        }

        Glide.with(QuizActivity.this).load(pictureUrl).into(binding.ivQuestion);
        binding.tvQuestion.setText(question);
        fillAnswerOptions(questionAnswers);
    }

    private void fillAnswerOptions(List<String> answerOptions) {
        if (answerOptions.size() < 6) {
            Toast.makeText(QuizActivity.this, "Not enough answer options", Toast.LENGTH_LONG).show();
            return;
        }

        binding.radioAns1.setText(answerOptions.get(0));
        binding.radioAns2.setText(answerOptions.get(1));
        binding.radioAns3.setText(answerOptions.get(2));
        binding.radioAns4.setText(answerOptions.get(3));
        binding.radioAns5.setText(answerOptions.get(4));
        binding.radioAns6.setText(answerOptions.get(5));
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
        // Проверяем, выбран ли какой-то RadioButton
        if (selectedRadioButtonId == -1) {
            Toast.makeText(QuizActivity.this, "Firstly choose the answer", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем текст выбранного RadioButton
        String choosedAnswer = ((RadioButton) findViewById(selectedRadioButtonId)).getText().toString();

        // Проверяем ответ
        if (choosedAnswer.equals(currentRightAnswer) && !wasHintRequested)
            currentScore += 2;
        else if (choosedAnswer.equals(currentRightAnswer))
            currentScore += 1;

        Toast.makeText(QuizActivity.this, "Right answer " + currentRightAnswer, Toast.LENGTH_SHORT).show();
    }
}
