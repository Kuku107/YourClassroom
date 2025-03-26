package android.example.yourclassroom.model;

import android.net.Uri;

public class File {
    private Uri uri;
    private String name;
    private String data;

    public File() {
    }

    public File(Uri uri, String name, String data) {
        this.uri = uri;
        this.name = name;
        this.data = data;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
