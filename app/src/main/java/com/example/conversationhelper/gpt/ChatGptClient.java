package com.example.conversationhelper.gpt;

import android.os.Handler;
import android.os.Looper;

import com.example.conversationhelper.BuildConfig;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Message;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
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

    public static void send(Chat chat, List<Message> historyMessages, ChatGptCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {

            List<RequestMessage> messages = new ArrayList<>();

            String systemMessage = "Возьми на себя роль технического специалиста "
                    + chat.getSpecialization() + ", который должен провести технической интервью на "
                    + chat.getLanguage().replace("ий", "ом") + " языке, задай ровно "
                    + chat.getNumberQuestions() + " вопросов поочереди, после каждого ты должен ждать ответ, уровень квалификации собеседника "
                    + chat.getDifficulty() + ", подстрой вопросы для его уровня."
                    + " Будь в меру строгим и не позволяй уходить от темы разговора."
                    + " После всех вопросов ты должен подвести итоги собеседования точно по этому формату: \"Ваш результат: или Your result: количество правильных ответов/количество вопросов\""
                    + " Дальше должны идти для каждого неправильного ответа пояснения в чем была допущена ошибка."
                    + " Закончи все это дополнительными советами для улучшения результатов собеседования.";

            messages.add(new RequestMessage("system", systemMessage));
            for (int i = 0; i < historyMessages.size(); i++) {
                messages.add(new RequestMessage(historyMessages.get(i).getType(), historyMessages.get(i).getContent()));
            }

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

