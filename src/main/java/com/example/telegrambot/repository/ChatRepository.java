package com.example.telegrambot.repository;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    //Мой ID = 977764584

    //Колин ID = 1836286216

    //Список ID чатов
    List<Long> chatIdList;

    public ChatRepository() {
        chatIdList = new ArrayList<>();

        //Добавляем ID Колиного и моего чата
        chatIdList.add(977764584L);
        //chatIdList.add(1836286216L);
    }

    public void save(Long chatId){

        //Провека, содержит ли ID, который мы пытаемся заново сохранить
        if(chatIdList.contains(chatId) == false) {
            chatIdList.add(chatId);
        }
    }

    public List<Long> getChatIdList() {
        return chatIdList;
    }
}
