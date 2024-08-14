package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SurahAudioAdapter extends RecyclerView.Adapter<SurahAudioAdapter.ViewHolder> {

    private List<SurahAudio> dataList;

    public SurahAudioAdapter(List<SurahAudio> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SurahAudio surahAudio = dataList.get(position);
        holder.textViewSurah.setText(surahAudio.getSurah());
        holder.textViewTarannum.setText(surahAudio.getTarannum());
        // ... (bind other views)
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSurah, textViewTarannum;
        // Add other views as needed

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSurah = itemView.findViewById(R.id.textViewSurah);
            textViewTarannum = itemView.findViewById(R.id.textViewTarannum);
            // Initialize other views
            // ...

            // Set click listeners for update and delete actions
            // ... (implement your logic here)
        }
    }
}

