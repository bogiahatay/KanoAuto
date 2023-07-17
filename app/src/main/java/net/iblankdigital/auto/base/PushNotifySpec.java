package net.iblankdigital.auto.base;

import android.util.Log;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushNotifySpec {

    private static String KEY = "AAAASctu1z4:APA91bEYXMc-nxATAkx1VuSNYKqX-Pc01a90VqmmN5ROEcNJ7Ca0wp9X4NzRsKV49woOl3vPQ1FRZCGF9RLmYWanDeV4lfa8Ta6GtkwAXCXXBLk6Qrm-_I8C-Bk-5I-EVRXHkqTri-9p";
    private static String TOPIC_ALL = "/topics/all";

    public synchronized static void execute(String title, String msg) {
        execute(TOPIC_ALL, title, msg);
    }

    public synchronized static void execute(String topic, String title, String msg) {
        try {
            Log.e("PushNotifySpec", title + " - " + msg);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("key", KEY)
                    .addFormDataPart("topic", topic)
                    .addFormDataPart("title", title)
                    .addFormDataPart("body", msg)
                    .build();
            Request request = new Request.Builder()
                    .url("https://kanoteam.com/crypto/push.php")
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
