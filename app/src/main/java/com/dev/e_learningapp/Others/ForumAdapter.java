package com.dev.e_learningapp.Others;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.e_learningapp.Database.UserHelperClass;
import com.dev.e_learningapp.R;
import com.dev.e_learningapp.User.Forum.ForumPage;
import com.dev.e_learningapp.User.HomePage;
import com.dev.e_learningapp.User.VideoPlayer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<ArrayList<String>> data;
    private String user_phoneNo;
    private Context c;

    public ForumAdapter(Context context, ArrayList<ArrayList<String>> data,String user_phoneNo){
        this.layoutInflater = LayoutInflater.from(context);
        this.c = context;
        this.data = data;
        this.user_phoneNo = user_phoneNo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.forum_custom_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String s_key = data.get(position).get(0);
        final String s_content = data.get(position).get(2);
        String s_post_author = data.get(position).get(1);
        String s_phoneNo = data.get(position).get(3);

        holder.content.setText(s_content);

        if(!s_phoneNo.equals(user_phoneNo)){
            holder.constrain.setBackgroundColor(Color.parseColor("#F1ECED"));

            holder.del_post.setVisibility(View.GONE);

            holder.post_author.setText("By " + s_post_author);
        }
        else{
            holder.post_author.setText("You");

            holder.del_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                    final DatabaseReference reference = rootNode.getReference("Posts");

                    final ForumPage p = (ForumPage)c;
                    p.swipeRefreshLayout.setRefreshing(true);
                    reference.child(s_key).removeValue();
                    p.setEmptyList();

                    Toast.makeText(c,"Post deleted",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() { return data.size(); }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        TextView content,post_author;
        ImageButton del_post;
        CardView cardView;
        ConstraintLayout constrain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            post_author = itemView.findViewById(R.id.post_author);
            del_post = itemView.findViewById(R.id.del_post);
            cardView = itemView.findViewById(R.id.card);
            constrain = itemView.findViewById(R.id.constrain);
        }
    }

}
