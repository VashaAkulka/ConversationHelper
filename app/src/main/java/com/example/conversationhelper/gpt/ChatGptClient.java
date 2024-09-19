package com.example.conversationhelper.gpt;

import android.os.Handler;
import android.os.Looper;

import com.example.conversationhelper.BuildConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGptClient  {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static void send(String userMessage, ChatGptCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> messages = Collections.singletonList(new Message("user", userMessage));
            String jsonBody = gson.toJson(new ChatRequest("gpt-3.5-turbo", messages));

            Request request = new Request.Builder()
                    .url("https://api.proxyapi.ru/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + BuildConfig.API_KEY)
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8")))
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String responseBody = response.body().string();
                String result = gson.fromJson(responseBody, ChatResponse.class)
                        .choices.get(0).message.content;

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }
        });
    }
}

