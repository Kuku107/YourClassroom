package android.example.yourclassroom;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.example.yourclassroom.adapter.FileAdapter;
import android.example.yourclassroom.model.File;
import android.example.yourclassroom.utils.EncodeDecodeFile;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
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

public class AddExerciseActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
    private TextView tvDate;

    private RecyclerView rvListFile;
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
        LinearLayout datePickerContainer = findViewById(R.id.datePickerContainer);
        datePickerContainer.setOnClickListener(v -> showDatePickerDialog());

        uploadFromLocal.setOnClickListener(v -> openFilePicker());

        List<File> files = List.of(
                new File("file1", "data1"),
                new File("file2", "data2"),
                new File("file3", "data3")
        );
        fileAdapter = new FileAdapter(files);
        rvListFile = findViewById(R.id.rv_list_item_file);
        rvListFile.setLayoutManager(new LinearLayoutManager(this));
        rvListFile.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvListFile.setAdapter(fileAdapter);
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
                String dataFile = EncodeDecodeFile.encode(fileUri);
                fileAdapter.addFile(new File(fileName, dataFile));
                Toast.makeText(this, "File " + fileName + " đã được thêm", Toast.LENGTH_SHORT).show();
            }
        }
    }
}