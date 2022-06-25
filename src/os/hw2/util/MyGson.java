package os.hw2.util;

import com.google.gson.Gson;

public class MyGson {
    private final static Gson gson = new Gson();

    public static void testGson() {
        Message testMessage = gson.fromJson("{\"type\":\"ASSIGN\",\"task\":{\"cells\":[4],\"sleeps\":[50],\"initialCells\":[4],\"id\":0,\"sum\":0,\"currentTask\":\"SLEEP\"},\"taskID\":0,\"cellValue\":0,\"workerID\":0,\"permission\":false}", Message.class);
    }

    public static Gson getGson() {
        return gson;
    }
}
