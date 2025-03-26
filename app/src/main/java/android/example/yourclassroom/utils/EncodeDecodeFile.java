package android.example.yourclassroom.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class EncodeDecodeFile {

    public static String encode(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                throw new IOException("Unable to open input stream from URI: " + uri.toString());
            }
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File decode(String base64Data, String fileName, Context context) {
        try {
            byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
            File file = new File(context.getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decodedBytes);
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
