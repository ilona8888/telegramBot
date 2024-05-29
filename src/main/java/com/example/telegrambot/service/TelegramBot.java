package com.example.telegrambot.service;

import com.example.telegrambot.config.BotConfig;
import com.example.telegrambot.repository.ChatRepository;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

@EnableScheduling
@Component
public class TelegramBot extends TelegramLongPollingBot {

    ChatRepository chatRepository = new ChatRepository();
    final BotConfig botConfig;



    public TelegramBot(BotConfig config) {
        this.botConfig = config;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "get a welcome massage"));
        commands.add(new BotCommand("/love", "get love sentence"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
        //scheduleMessage();

    }

    //Список приятных слов
    private String randomWish(){
        String[] array = {
                "Ты всегда делаешь мой день светлее.",
                "Твоя улыбка — мое счастье.",
                "С тобой я чувствую себя настоящим.",
                "Твоя поддержка для меня бесценна.",
                "Спасибо, что ты есть рядом.",
                "Твоя любовь согревает меня в холодные дни.",
                "Ты делаешь мою жизнь полной.",
                "С тобой мир кажется лучше.",
                "Я горжусь тем, кто ты есть.",
                "Твоя простота и искренность заставляют меня влюбляться в тебя снова и снова.",
                "Ты — мой опора и вдохновение.",
                "Ты делаешь мои самые смелые мечты реальностью.",
                "Твои слова всегда поддерживают и мотивируют меня.",
                "Ты — мое сокровище.",
                "Твоя забота нежнее самого нежного прикосновения.",
                "Ты умеешь делать обычные моменты особенными.",
                "Твоя любовь — мое самое ценное достояние.",
                "С тобой я чувствую себя дома в любом уголке мира.",
                "Твоя уверенность во мне дарует мне крылья.",
                "Ты умеешь найти свет в самых темных временах.",
                "Я обожаю, как ты смотришь на мир.",
                "Твоя забота — мое лучшее лекарство.",
                "Твоя легкость и радость жизни заразительны.",
                "С тобой я чувствую, что могу преодолеть все препятствия.",
                "Ты — мой мир и моя вселенная.",
                "Твоя нежность наполняет мое сердце теплом.",
                "Ты — моя счастливая звезда на небе.",
        };

        //Рандом индекса массива
        int min = 0;
        int max = array.length - 1;

        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        return array[randomNum];
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){

            //записываем строку, присланную пользователем
            String massageText = update.getMessage().getText();

            //записываем ID чата
            long chatId = update.getMessage().getChatId();

            //Записываем имя пользователя
            String userName = update.getMessage().getChat().getFirstName();

            //В зависимости от сообщения выполнять некоторые действия
            switch (massageText){
                case "/start" :
                    startCommand(chatId, userName);
                    break;
                case "/love" :
                    sendLoveCommand(chatId,userName);
                    break;
                default:
                    //Отправляем это сообщение, если пользователь ввел не существующую команду
                    sendMassage(chatId, "Sorry, i don't know this command. Please try again");
                    break;
            }
        }
    }

    private void startCommand(long chatId, String userName){
        //Сохраняем ID чата
        chatRepository.save(chatId);

        //Приветсвуем пользователя
        String answer = EmojiParser.parseToUnicode( "Hi, " + userName + "." + " Nice to meet you" + " :blush:");

        //Отпраляем сообщение
        sendMassage(chatId, answer);
    }

    private void sendLoveCommand(long chatId,String userName){
        //Отправляем сообщение с именем пользователя и рандомной "приятной фразой"
        String answer = EmojiParser.parseToUnicode(userName + "! " + randomWish() + ":sparkling_heart:" + ":sparkling_heart:" + ":sparkling_heart:");
        sendMassage(chatId, answer);
    }
    private void sendMassage(long chatId, String textToSend){
        SendMessage massage = new SendMessage();
        massage.setChatId(String.valueOf(chatId)); //задаем ID пользователя
        massage.setText(textToSend); // хадаем текст для отправки

        try {
            execute(massage); // выполняем отправку
        }
        catch (TelegramApiException e){
            System.out.println(e);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    //Примеры:
//    These are valid formats for cron expressions:
//
//            0 0 * * * * = the top of every hour of every day.
//
//            */10 * * * * * = every ten seconds.
//
//            0 0 8-10 * * * = 8, 9 and 10 o'clock of every day.
//
//            0 0 6,19 * * * = 6:00 AM and 7:00 PM every day.
//
//            0 0/30 8-10 * * * = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
//
//            0 0 9-17 * * MON-FRI = on the hour nine-to-five weekdays
//
//            0 0 0 25 12 ? = every Christmas Day at midnight



    //Функция для отправки сообщения по таймеру

    //cron = "0 * * * * *" – переодичность 1 минута
    //cron = "0 0 9 * * *" – каждый день в 9:00
    @Scheduled(cron = "0 * * * * *")
    private void sendMassageLove(){

        //Создаем рандомную строку
        String message = EmojiParser.parseToUnicode(randomWish() + ":kissing_heart:");

        // Получаем список всех пользователей
        List<Long> activeChatIds = chatRepository.getChatIdList();

        // Отправляем сообщение каждому пользователю
        for (Long chatId : activeChatIds) {
            sendMassage(chatId,message);
        }
    }


//    //TODO НЕ работает код по отправке сообщения в определенное время суток
//    private void scheduleMessage() {
//        Timer timer = new Timer();
//        // Задаем время отправки сообщения (в данном случае, каждый день в 4:05)
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                sendScheduledMessage();
//            }
//        }, getDelayToNextTime(4, 05), 24 * 60 * 60 * 1000); // 24 часа * 60 минут * 60 секунд * 1000 миллисекунд
//    }
//
//    private long getDelayToNextTime(int hour, int minute) {
//        long currentTime = System.currentTimeMillis();
//        long nextTime = (currentTime / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000); // До начала текущего дня
//        nextTime += hour * 60 * 60 * 1000 + minute * 60 * 1000; // Прибавляем время 4:05 утра
//        if (nextTime <= currentTime) {
//            nextTime += 24 * 60 * 60 * 1000; // Если указанное время уже прошло, добавляем еще сутки
//        }
//        return nextTime - currentTime;
//    }
//
//    private void sendScheduledMessage() {
//        // Определяем текст сообщения
//        String message = "Это сообщение отправлено в определенное время каждый день!";
//        // Получаем список всех пользователей
//
//        List<Long> activeChatIds = chatRepository.getChatIdList();
//
//        // Отправляем сообщение каждому пользователю
//        for (Long chatId : activeChatIds) {
//            sendMassage(chatId,message);
//        }
//    }
}
