package com.balievent.telegrambot.service.handler.callback.pagination;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.model.entity.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class FirstPaginationHandler extends AbstractPaginationHandler {

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.FIRST_EVENTS_PAGE;
    }

    @Override
    protected UserData updateUserData(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        return userDataService.setCurrentPage(callbackChatId, 1);
    }

}
