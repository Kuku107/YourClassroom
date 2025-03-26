package android.example.yourclassroom.adapter;

import android.content.Context;
import android.example.yourclassroom.R;
import android.example.yourclassroom.model.Attachment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder>{

    private List<Attachment> fileList;
    private Context context;

    private OnItemClickListener listener;

    public FileAdapter(Context context) {
        this.fileList = new ArrayList<>();
        this.context = context;
    }

    public FileAdapter(List<Attachment> fileList) {
        this.fileList = fileList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        Attachment file = fileList.get(position);

        holder.tvFileName.setText(file.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(file);
                }
            }
        });

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

    public void addFile(Attachment file) {
        fileList.add(file);
        notifyDataSetChanged();
    }

    public void pushDataToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://demoyourclassroom-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("files");

        for (Attachment file : fileList) {
            String key = myRef.push().getKey();
            File fileFire = new File(file.getName(), file.getData());
            myRef.child(key).setValue(fileFire);
        }
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

    public interface OnItemClickListener {
        void onItemClick(Attachment file);
    }
}
