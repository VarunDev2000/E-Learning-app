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

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<ArrayList<String>> data;

    public ForumAdapter(Context context, ArrayList<ArrayList<String>> data){
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.forum_custom_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s_content = data.get(position).get(1);
        String s_post_author = data.get(position).get(0);

        holder.content.setText(s_content);
        holder.post_author.setText("By " + s_post_author);
    }

    @Override
    public int getItemCount() { return data.size(); }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        TextView content,post_author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            post_author = itemView.findViewById(R.id.post_author);
        }
    }
}
