package com.example.bogdan.tasklocalstorage.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bogdan.tasklocalstorage.domain.Task;
import com.example.bogdan.tasklocalstorage.R;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.WordViewHolder> {

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.taskView);
        }
    }


    private final LayoutInflater mInflater;
    private List<Task> mTasks; // Cached copy of words

    public TaskListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        if (mTasks != null) {
            Task current = mTasks.get(position);

            String description = current.getDescription();
            int priority = current.getPriority();

            String outputText = "Description: " + description + "  " + "Priority: " + priority;

            holder.wordItemView.setText(outputText);
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Task YET");
        }
    }

    public void setTasks(List<Task> tasks){
        mTasks = tasks;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        return 0;
    }


}
