package org.example;

import org.apache.http.client.config.RequestConfig;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);

        final DefaultBotOptions options = new DefaultBotOptions();
        options.setMaxThreads(200);
        options.setRequestConfig(RequestConfig.custom()
                .setContentCompressionEnabled(true)
                .build());

        String key = System.getenv("TELEGRAM_API_KEY");

        SampleBot bot = new SampleBot(key, options);
        api.registerBot(bot);
    }
}