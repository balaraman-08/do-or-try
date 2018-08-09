package com.example.android.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by BALARAMAN on 02-08-2018 with love.
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<TodoList> mTodoListArrayList;
    private ClickListener clickListener;

    TodoAdapter(Context context, ArrayList<TodoList> todoListArrayList) {
        this.mContext = context;
        this.mTodoListArrayList = todoListArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.titleView.setText(mTodoListArrayList.get(position).getmTitle());
        holder.descriptionView.setText(mTodoListArrayList.get(position).getDesc());
        holder.successView.setText(String.valueOf(mTodoListArrayList.get(position).getUpvotes()));
        holder.failureView.setText(String.valueOf(mTodoListArrayList.get(position).getDownvotes()));

        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null)
                    clickListener.upVoting(holder.getAdapterPosition());
            }
        });

        holder.downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null)
                    clickListener.downVoting(holder.getAdapterPosition());
            }
        });


        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle("Confirm")
                            .setMessage("Are sure to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clickListener.deleting(holder.getAdapterPosition());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTodoListArrayList.size();
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, descriptionView, successView, failureView;
        ImageView upButton, downButton, deleteButton;

        MyViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title);
            descriptionView = itemView.findViewById(R.id.desc);
            successView = itemView.findViewById(R.id.success_count);
            failureView = itemView.findViewById(R.id.failure_count);
            upButton = itemView.findViewById(R.id.upvote);
            downButton = itemView.findViewById(R.id.downvote);
            deleteButton = itemView.findViewById(R.id.delete);
        }
    }
}
