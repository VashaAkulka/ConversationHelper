package com.example.conversationhelper.gpt;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.conversationhelper.BuildConfig;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Message;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatGptClient {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
            .build();

    private static final Gson gson = new Gson();

    public static void send(Chat chat, List<Message> historyMessages, ChatGptCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {

            List<RequestMessage> messages = new ArrayList<>();

            String systemMessage = "Возьми на себя роль технического специалиста "
                    + chat.getSpecialization() + ", который должен провести техническое интервью на "
                    + chat.getLanguage().replace("ий", "ом") + " языке, задай ровно "
                    + chat.getNumberQuestions() + " вопросов поочереди, после каждого ты должен ждать ответ, уровень квалификации собеседника "
                    + chat.getDifficulty() + ", подстрой вопросы для его уровня."
                    + " Будь в меру строгим и не позволяй уходить от темы разговора."
                    + " После всех вопросов ты должен подвести итоги собеседования точно по этому формату: \"Ваш результат: или Your result: количество правильных ответов/количество вопросов\"."
                    + " Следующая строчка : \"Пройденно успешно или Passed successfully\", а если ты решишь что его знаний не хватает на должность то: \"Пройденно неудачно или Passed unsuccessfully\"."
                    + " Дальше должны идти для каждого неправильного ответа пояснения в чем была допущена ошибка."
                    + " Закончи все это дополнительными советами для улучшения результатов собеседования.";

            messages.add(new RequestMessage("system", systemMessage));

            for (Message historyMessage : historyMessages) {
                if (!historyMessage.getType().equals("error")) {
                    messages.add(new RequestMessage(historyMessage.getType(), historyMessage.getContent()));
                }
            }

            String jsonBody = gson.toJson(new ChatRequest("gpt-4-turbo", messages));

            Request request = new Request.Builder()
                    .url("https://api.proxyapi.ru/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + BuildConfig.API_KEY)
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8")))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful() || responseBody == null) {
                            throw new IOException("Unexpected code " + response);
                        }

                        String responseBodyString = responseBody.string();
                        String result = gson.fromJson(responseBodyString, ChatResponse.class)
                                .choices.get(0).message.content;

                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                    }
                }
            });
        });
    }
}
