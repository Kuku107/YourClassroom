package android.example.yourclassroom;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.example.yourclassroom.adapter.FileAdapter;
import android.example.yourclassroom.model.File;
import android.example.yourclassroom.utils.EncodeDecodeFile;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddExerciseActivity extends AppCompatActivity implements FileAdapter.OnItemClickListener {
    private static final int PICK_FILE_REQUEST = 1;
    private TextView tvDate;

    private RecyclerView rvListFile;
    private Button btnAssign;
    private final Calendar calendar = Calendar.getInstance();
    private FileAdapter fileAdapter;

    private LinearLayout uploadFromLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_exercise);
        setSupportActionBar(findViewById(R.id.toolbar));

        uploadFromLocal = findViewById(R.id.upload_from_local);
        tvDate = findViewById(R.id.tv_date);
        btnAssign = findViewById(R.id.btn_assign_exercise);

        LinearLayout datePickerContainer = findViewById(R.id.datePickerContainer);
        datePickerContainer.setOnClickListener(v -> showDatePickerDialog());

        uploadFromLocal.setOnClickListener(v -> openFilePicker());

        fileAdapter = new FileAdapter(this);
        fileAdapter.setOnItemClickListener(this);
        rvListFile = findViewById(R.id.rv_list_item_file);
        rvListFile.setLayoutManager(new LinearLayoutManager(this));
        rvListFile.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvListFile.setAdapter(fileAdapter);

        btnAssign.setOnClickListener(v -> {
            if (fileAdapter.getItemCount() == 0) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 file", Toast.LENGTH_SHORT).show();
                return;
            }
            fileAdapter.pushDataToFirebase();
        });
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvDate.setText(dateFormat.format(calendar.getTime()));
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData(); // Lấy URI của file
            if (fileUri != null) {
                String fileName = getFileName(fileUri);
                String dataFile = EncodeDecodeFile.encode(this, fileUri);
                fileAdapter.addFile(new File(fileUri, fileName, dataFile));
                Toast.makeText(this, dataFile, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(file.getUri(), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer app found. Please install one.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}