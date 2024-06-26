package com.balievent.telegrambot.service.handler.callback.impl;

import com.balievent.telegrambot.constant.CallbackHandlerType;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.service.handler.callback.ButtonCallbackHandler;
import com.balievent.telegrambot.service.service.EventService;
import com.balievent.telegrambot.service.service.UserDataService;
import com.balievent.telegrambot.util.DateUtil;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Slf4j
public class MonthEventsHandler extends ButtonCallbackHandler {
    private final UserDataService userDataService;
    private final EventService eventService;

    @Override
    public CallbackHandlerType getCallbackHandlerType() {
        return CallbackHandlerType.MONTH_EVENTS_PAGE;
    }

    @Override
    public void handle(final Update update) throws TelegramApiException {
        final Long chatId = update.getCallbackQuery().getMessage().getChatId();
        final UserData userData = userDataService.getUserData(chatId);          // Данные о пользователе
        final LocalDate calendarDate = userData.getSearchEventDate();           // Текущая дата
        final String formattedMonth = DateUtil.getFormattedMonth(calendarDate); // Возвращает строковое представление месяца в заданной календарной дате.
        final String detailedEventsForMonth = eventService.getMessageWithEventsGroupedByDayFull(calendarDate, 1, calendarDate.lengthOfMonth());

        // здесь формируется строки /01_04_2024 : 8 events -> в какую дату сколько сообщений добавляем перевод строки
        final String eventListMessage = TgBotConstants.EVENT_LIST_TEMPLATE.formatted(formattedMonth, detailedEventsForMonth);

        final EditMessageText editMessageText = EditMessageText.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .text(eventListMessage)
            .replyMarkup(KeyboardUtil.createMonthInlineKeyboard(calendarDate))
            .build();

        removeMediaMessage(chatId, userData);
        myTelegramBot.execute(editMessageText);
    }

}
