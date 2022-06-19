package os.hw2.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGson {
    private static GsonBuilder gsonBuilder;
    private static Gson gson;

    private static MyGson instance;

    private MyGson() {
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public static Gson getGson() {
        if (instance == null) {
            new MyGson();
            return gson;
        }
        return gson;
    }
}
