package com.balievent.telegrambot.service.handler.callback.showlessmore;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.model.entity.UserData;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ShowMoreHandler extends AbstractShowHandler {

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.SHOW_MONTH_FULL;
    }

    @Override
    protected String getText(final Update update) {
        final UserData userData = userDataService.getUserData(update.getCallbackQuery().getMessage().getChatId());
        final LocalDate calendarDate = userData.getCalendarDate();
        final String displayDate = calendarDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " (" + calendarDate.getMonthValue() + "." + calendarDate.getYear() + ")";
        final String detailedEventsForMonth = eventService.getMessageWithEventsGroupedByDayFull(calendarDate, 1, calendarDate.lengthOfMonth());
        return TgBotConstants.EVENT_LIST_TEMPLATE.formatted(displayDate, detailedEventsForMonth);
    }

    @Override
    protected InlineKeyboardMarkup replyMarkup(final Update update) {
        return KeyboardUtil.createInlineKeyboard(TelegramButton.SHOW_MONTH_LESS);
    }

}
