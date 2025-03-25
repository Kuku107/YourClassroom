package android.example.yourclassroom.adapter;

import android.annotation.SuppressLint;
import android.example.yourclassroom.R;
import android.example.yourclassroom.model.File;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder>{

    private List<File> fileList;

    public FileAdapter() {
        this.fileList = new ArrayList<>();
    }

    public FileAdapter(List<File> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        File file = fileList.get(position);

        holder.tvFileName.setText(file.getName());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileList.remove(file);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (fileList != null) {
            return fileList.size();
        }
        return 0;
    }

    public void addFile(File file) {
        fileList.add(file);
        notifyDataSetChanged();
    }

    class FileHolder extends RecyclerView.ViewHolder {
        private TextView tvFileName;
        private ImageButton btnDelete;

        public FileHolder(@NonNull View itemView) {
            super(itemView);
            this.tvFileName = itemView.findViewById(R.id.tv_item_file_name);
            this.btnDelete = itemView.findViewById(R.id.ib_item_file_delete);;
        }
    }
}
