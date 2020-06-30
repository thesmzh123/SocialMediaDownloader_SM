package en.all.social.downloader.app.online.utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class AppUtils extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
