package com.balievent.telegrambot.service.handler.callback.pagination;

import com.balievent.telegrambot.constant.TelegramButton;
import com.balievent.telegrambot.model.entity.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class LastPaginationHandler extends AbstractPaginationHandler {

    @Override
    public TelegramButton getTelegramButton() {
        return TelegramButton.LAST_EVENTS_PAGE;
    }

    @Override
    protected UserData updateUserData(final Update update) {
        final Long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        final int pageCount = userDataService.getUserData(callbackChatId).getPageCount();
        return userDataService.setCurrentPage(callbackChatId, pageCount);
    }

}
