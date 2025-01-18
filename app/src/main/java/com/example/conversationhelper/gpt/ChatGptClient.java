package com.example.conversationhelper.gpt;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.conversationhelper.BuildConfig;
import com.example.conversationhelper.db.MessageType;
import com.example.conversationhelper.db.model.Chat;
import com.example.conversationhelper.db.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class ChatGptClient {

    private static final String BASE_URL = "https://caila.io/api/mlpgate/account/just-ai/model/openai-proxy/";

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build();

    private interface ChatGptService {
        @Headers({
                "Content-Type: application/json; charset=utf-8",
                "Authorization: Bearer " + BuildConfig.API_KEY
        })
        @POST("predict")
        Call<ChatResponse> sendMessage(@Body ChatRequest body);
    }

    private static final ChatGptService service = retrofit.create(ChatGptService.class);

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
                    + " Следующая строчка : \"Пройдено успешно или Passed successfully\", а если ты решишь что его знаний не хватает на должность то: \"Пройдено неудачно или Passed unsuccessfully\"."
                    + " Дальше должны идти для каждого неправильного ответа пояснения в чем была допущена ошибка."
                    + " Закончи все это дополнительными советами для улучшения результатов собеседования.";

            messages.add(new RequestMessage(MessageType.system.name(), systemMessage));

            for (Message historyMessage : historyMessages) {
                if (historyMessage.getType() != MessageType.error) {
                    messages.add(new RequestMessage(historyMessage.getType().name(), historyMessage.getContent()));
                }
            }

            ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", messages);

            service.sendMessage(chatRequest).enqueue(new Callback<ChatResponse>() {
                @Override
                public void onResponse(@NonNull Call<ChatResponse> call, @NonNull retrofit2.Response<ChatResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().choices.get(0).message.content;
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError(new Exception("Unexpected response")));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(t));
                }
            });
        });
    }
}
