package com.example.geoquiz.recyclerView;

import android.content.Context;
import android.text.StaticLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geoquiz.R;
import com.example.geoquiz.model.TypeGame;

import java.util.List;

// Адаптер класс - мост между Data (List<TypeGame>) и RecyclerView + CardView
public class ChooseViewAdapter extends RecyclerView.Adapter<ChooseViewHolder> {
    private List<TypeGame> typeGameList;

    public ChooseViewAdapter(List<TypeGame> typeGameList) {
        this.typeGameList = typeGameList;

    }

    @NonNull
    @Override
    public ChooseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Наполняем слой датой для каждого item в RecyclerView
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.rv_item_layout, parent, false);
        return new ChooseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseViewHolder holder, int position) {
        // Вызывается для каждого элемента в списке и привязывает данные из объекта TypeGame
        TypeGame currentTypeGame = typeGameList.get(position);

        holder.tvTypeGameName.setText(currentTypeGame.getTypeGameName());
        holder.ivCard.setImageResource(currentTypeGame.getTypeGameImg());

        /*
        String imgUrlEasy = "https://firebasestorage.googleapis.com/v0/b/geobase-52793.appspot.com/o/RecyclerView%2Fcard_easy.jpg?alt=media&token=682c4c15-52c3-4ef0-97d5-8f45bfe015f7";
        String imgUrlHard = "https://firebasestorage.googleapis.com/v0/b/geobase-52793.appspot.com/o/RecyclerView%2Fcard_hard.jpg?alt=media&token=dbc84044-7065-4c43-990c-a2d6ceff972e";
        String imgUrNightmare = "https://firebasestorage.googleapis.com/v0/b/geobase-52793.appspot.com/o/RecyclerView%2Fcard_nigthmare.jpg?alt=media&token=0a1f892a-244e-4e6a-be0d-8bf7e4134411";
        String imgUrlClothing = "https://firebasestorage.googleapis.com/v0/b/geobase-52793.appspot.com/o/RecyclerView%2Fcard_clothing.jpg?alt=media&token=6bc2f1bf-a9c4-465c-b9e4-e213fedfcea8";
        String imgUrlHistory = "https://firebasestorage.googleapis.com/v0/b/geobase-52793.appspot.com/o/RecyclerView%2Fcard_history.jpg?alt=media&token=15a20aa7-f03c-439b-9337-f243904c9c9e";
        String imgUrlSport = "https://firebasestorage.googleapis.com/v0/b/geobase-52793.appspot.com/o/RecyclerView%2Fcard_sport.jpg?alt=media&token=d8d43311-ee20-4687-8c97-9aab3e5a0e0e";

        Glide.with(holder.itemView.getContext()).load(imgUrlEasy).into(holder.);
        */

    }

    @Override
    public int getItemCount() {
        // Возвращает количество элементов в списке
        return typeGameList.size();
    }
}
