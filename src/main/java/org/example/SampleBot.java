package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SampleBot extends TelegramLongPollingBot {
    private boolean start = false;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper mapper = new ObjectMapper();

    public SampleBot(String token) {
        final DefaultBotOptions options = new DefaultBotOptions();
        options.setMaxThreads(200);
        options.setRequestConfig(RequestConfig.custom()
                .setContentCompressionEnabled(true)
                .build());

        super(options, token);
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if(msg.isCommand()) {
            if(msg.getText().equals("/start")) start = true;
            return;
        }

        if (start) {
            try {
                getAnswer(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            copyMessage(id, msg.getMessageId());
        }
    }

    private void getAnswer(Message message) throws IOException {
        GPTRequest request = new GPTRequest("gpt-4o-mini", List.of(new ReqMessage("user", message.getText())));

        ByteArrayEntity entity = new ByteArrayEntity(mapper.writeValueAsBytes(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://api.aimlapi.com/v1/chat/completions");
        post.setHeader("Authorization", "Bearer 60383558d27d485c89466f07f89bbbb1");
        post.setEntity(entity);

        httpClient.execute(post, res -> {
            Map<String, Object> r = mapper.readValue(res.getEntity().getContent(), Map.class);

            List<Map<String, Object>> choises = (List<Map<String, Object>>) r.get("choices");
            Map<String, Object> answer = choises.getFirst();
            Map<String, Object> msg = (Map<String, Object>) answer.get("message");

           SendMessage sm = SendMessage.builder()
                   .chatId(message.getChatId())
                   .text(msg.get("content").toString()).build();

            try {
                execute(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            return res;
        });
    }

    @Override
    public String getBotUsername() {
        return "YaGPT(free?)";
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void copyMessage(Long who, Integer msgId){
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())  //We copy from the user
                .chatId(who.toString())      //And send it back to him
                .messageId(msgId)            //Specifying what message
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
