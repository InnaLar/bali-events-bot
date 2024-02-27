package com.example.telegrambot.service;

import com.example.telegrambot.contant.MyConstants;
import com.example.telegrambot.model.entity.Event;
import com.example.telegrambot.repository.EventRepository;
import com.example.telegrambot.util.CommonUtil;
import com.example.telegrambot.util.GetGoogleMapLink;
import com.example.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarProcessService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");
    private final EventRepository eventRepository;

    private static StringBuilder getCalendarMonthChangedText(final Map<LocalDate, List<Event>> eventMap) {
        final Map<LocalDate, List<Event>> reverseSortedMap = new TreeMap<>(eventMap);
        final StringBuilder stringBuilder = new StringBuilder();
        reverseSortedMap.forEach((key, value) ->
            stringBuilder.append("/").append(key.format(DATE_TIME_FORMATTER))
                .append(" : ")
                .append(value.size())
                .append(" events")
                .append("\n"));

        if (stringBuilder.isEmpty()) {
            stringBuilder.append("No events");
        }
        return stringBuilder;
    }

    public SendMessage processShort(final Update update, final LocalDate localDate) {
        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final String eventListToday = findEventListToday(day, month, year);

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(String.format("%s %02d.%02d.%d%n%s", MyConstants.LIST_OF_EVENTS_ON, day, month, year, eventListToday)) // текст сообщения
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear())) // клавиатура
            .build();
    }

    public EditMessageText processShowMore(final Update update, final MessageDataStorage messageDataStorage) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String callbackData = callbackQuery.getData();

        // Получение идентификатора сообщения из колбэк-данных Пример SHOW_MORE:123
        final String chatIdString = callbackQuery.getMessage().getChatId().toString();    // ID пользователя чата
        final Long messageIdFromCallbackData = getMessageIdFromCallbackData(callbackData);
        final Integer messageId = Integer.parseInt(messageDataStorage.getMessageTimestamp(chatIdString, messageIdFromCallbackData)); // ID сообщения
        final LocalDate localDate = messageDataStorage.getLocalDate(chatIdString, getMessageIdFromCallbackData(callbackData)); // Дата сообщения
        final String newCallbackData = MyConstants.SHOW_LESS + MyConstants.SHOW_SEPARATOR + messageIdFromCallbackData;

        final int day = localDate.getDayOfMonth();
        final int month = localDate.getMonthValue();
        final int year = localDate.getYear();

        final String eventListToday = findListToday(day, month, year);
        final Long chatId = callbackQuery.getMessage().getChatId();

        return EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(String.format("%s %02d.%02d.%d%n%s", MyConstants.LIST_OF_EVENTS_ON, day, month, year, eventListToday)) // текст сообщения
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.updateButton(newCallbackData))
            .build();
    }

    public EditMessageText processShowLess(final Update update, final MessageDataStorage messageDataStorage) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();
        final String callbackData = callbackQuery.getData();

        // Получение идентификатора сообщения из колбэк-данных Пример SHOW_MORE:123
        final String chatIdString = callbackQuery.getMessage().getChatId().toString();
        final Long messageIdFromCallbackData = getMessageIdFromCallbackData(callbackData);
        final Integer messageId = Integer.parseInt(messageDataStorage.getMessageTimestamp(chatIdString, messageIdFromCallbackData)); // ID сообщения
        final String newCallbackData = MyConstants.SHOW_MORE + MyConstants.SHOW_SEPARATOR + messageIdFromCallbackData;

        final Long chatId = callbackQuery.getMessage().getChatId();

        return EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(MyConstants.LIST_OF_MORE)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyMarkup(KeyboardUtil.restoreButton(newCallbackData))
            .build();
    }

    public SendMessage processCalendarMonthChanged(final Update update, final LocalDate localDate) {
        final LocalDateTime start = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), 1, 0, 0);
        final LocalDateTime end = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth(), 23, 59);

        final Map<LocalDate, List<Event>> eventMap = eventRepository.findEventsByStartDateBetween(start, end)
            .stream()
            .collect(Collectors.groupingBy(event -> event.getStartDate().toLocalDate()));

        final StringBuilder stringBuilder = getCalendarMonthChangedText(eventMap);
        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(stringBuilder.toString())
            .replyMarkup(KeyboardUtil.setCalendar(localDate.getMonthValue(), localDate.getYear()))
            .build();
    }

    private String findEventListToday(final int day, final int month, final int year) {
        final List<Event> eventList = findEvents(day, month, year);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n");
        }
        return stringBuilder.toString();
    }

    private String findListToday(final int day, final int month, final int year) {
        final List<Event> eventList = findEvents(day, month, year);

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < eventList.size(); i++) {
            final Event event = eventList.get(i);
            stringBuilder.append(i + 1).append(". ")
                .append(CommonUtil.getLink(event.getEventName(), event.getEventUrl()))
                .append("\n")
                .append("Location:")
                .append(getClickableStart())
                .append("\n")
                .append(event.getLocationAddress())
                .append("\n")
                .append("Google Maps:")
                .append(GetGoogleMapLink.getGoogleMap(event.getCoordinates(), event.getCoordinates()))
                .append("\n")
                .append("----------------")
                .append("\n\n");
        }

        return stringBuilder.toString();
    }

    /**
     * Позволяет создать кликабельную ссылку на дату, но только на первое число /10
     *
     * @return - кликабельная ссылка
     */
    public static String getClickableStart() {
        return "<a href=\"command:\">/10.02.2024</a>";
    }

    private List<Event> findEvents(final int day, final int month, final int year) {
        final LocalDateTime from = LocalDateTime.of(year, month, day, 0, 0);
        final LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59);
        return eventRepository.findEventsByStartDateBetween(from, end);
    }

    private Long getMessageIdFromCallbackData(final String callbackData) {
        final String[] parts = callbackData.split(MyConstants.SHOW_SEPARATOR);
        if (parts.length < 2 || parts[1].isEmpty()) {
            throw new NumberFormatException("Invalid callback data format");
        }
        return Long.parseLong(parts[1]);
    }

}
