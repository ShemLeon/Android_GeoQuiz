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
    public TypeGameClickListener clickListener;
    public void  setClickListener(TypeGameClickListener myListener){
        this.clickListener = myListener;
    }


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

        // Устанавливаем слушатель для каждого элемента
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Возвращает количество элементов в списке
        return typeGameList.size();
    }

}
