package com.massimo.ronzulli.turnironzulli.models;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.massimo.ronzulli.turnironzulli.ModifyCardsActivity;
import com.massimo.ronzulli.turnironzulli.R;

import java.util.ArrayList;

public class RecyclerViewAdapterCards extends RecyclerView.Adapter<RecyclerViewAdapterCards.ViewHolder> {
    private ArrayList<String> cards;
    private Context context;

    public RecyclerViewAdapterCards(ArrayList<String> cardList, Context context) {
        cards=cardList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cards, viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.card_text.setText(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView card_text;

        public ViewHolder(@NonNull View v) {
            super(v);
            v.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                Intent intent = new Intent(context, ModifyCardsActivity.class);
                putSinglePref(context,"newCard", "false", "Cards");
                putSinglePref(context,"position", String.valueOf(position), "Cards");
                putSinglePref(context,"Selected Card", cards.get(position), "Cards");
                context.startActivity(intent);
                ((Activity)context).finish();
            });
            card_text = (TextView) v.findViewById(R.id.card_text);
        }
    }
}
