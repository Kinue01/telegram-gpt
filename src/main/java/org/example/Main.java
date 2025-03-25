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

        SampleBot bot = new SampleBot("7252935986:AAERy7gEAjEi_ifOhbQbFEFD9YOatjt42Fg", options);
        api.registerBot(bot);
    }
}