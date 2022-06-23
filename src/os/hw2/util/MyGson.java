package os.hw2.util;

import com.google.gson.Gson;

public class MyGson {
    private final static Gson gson = new Gson();

    public static void testGson() {
        Object obj = gson.fromJson("{}", Object.class);
        Logger.getInstance().log("Gson Tested");
    }

    public static Gson getGson() {
        return gson;
    }
}
