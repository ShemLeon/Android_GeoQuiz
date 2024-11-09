package com.example.geoquiz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoquiz.databinding.ActivityChooseBinding;
import com.example.geoquiz.recyclerView.ChooseViewAdapter;
import com.example.geoquiz.model.TypeGame;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {
    // 1- Adapter View
    private RecyclerView recyclerView;
    // 2- Data Source
    private List<TypeGame> typeGameList;
    // 3- Adapter
    private ChooseViewAdapter rvAdapter;
    private ActivityChooseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityChooseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.rv_choose);

        typeGameList = new ArrayList<>();

        typeGameList.add(new TypeGame(1,"Easy", R.drawable.card_easy));
        typeGameList.add(new TypeGame(2, "Hard", R.drawable.card_hard));
        typeGameList.add(new TypeGame(3, "Nightmare", R.drawable.card_nigthmare));
        typeGameList.add(new TypeGame(4, "Сlothing", R.drawable.card_clothing));
        typeGameList.add(new TypeGame(5, "History", R.drawable.card_history));
        typeGameList.add(new TypeGame(6, "Sport", R.drawable.card_sport));

        rvAdapter = new ChooseViewAdapter(typeGameList);
        binding.rvChoose.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChoose.setAdapter(rvAdapter);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
}