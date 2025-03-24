package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        SampleBot bot = new SampleBot("7252935986:AAERy7gEAjEi_ifOhbQbFEFD9YOatjt42Fg");
        api.registerBot(bot);
    }
}