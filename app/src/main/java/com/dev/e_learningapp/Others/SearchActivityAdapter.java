package com.dev.e_learningapp.Others;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.VideoPlayer;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivityAdapter extends RecyclerView.Adapter<SearchActivityAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<ArrayList<String>> data;

    public SearchActivityAdapter(Context context, ArrayList<ArrayList<String>> data){
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String title = data.get(position).get(1);
        holder.cardTitle.setText(title);

    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView card;
        TextView cardTitle;
        ImageView imageView;
        ImageButton play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card);
            Animation scaleUp = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.search_activity_scale_up);
            card.startAnimation(scaleUp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Card Effects
                    final Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.card_bounce);
                    card.startAnimation(anim);

                    Intent intent = new Intent(v.getContext(), VideoPlayer.class);
                    intent.putExtra("videoUri",data.get(getAdapterPosition()).get(2));
                    v.getContext().startActivity(intent);
                }
            });

            cardTitle = itemView.findViewById(R.id.cardTitle);
            imageView = itemView.findViewById(R.id.imageView);
            play = itemView.findViewById(R.id.play);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Button Effects
                    final Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                    play.startAnimation(anim);

                    Intent intent = new Intent(v.getContext(), VideoPlayer.class);
                    intent.putExtra("videoUri",data.get(getAdapterPosition()).get(2));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
