package com.balievent.telegrambot.service.handler.callback.showlessmore;

import com.balievent.telegrambot.constant.TgBotConstants;
import com.balievent.telegrambot.service.handler.callback.CallbackHandlerMessageType;
import com.balievent.telegrambot.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class ShowLessHandler extends AbstractShowHandler {

    private static String getShowWord(final String showWord) {
        if (showWord.contains(TgBotConstants.SHOW_LESS)) {
            return TgBotConstants.SHOW_MORE;
        } else {
            return TgBotConstants.SHOW_FULL_MONTH;
        }
    }

    @Override
    public CallbackHandlerMessageType getHandlerType() {
        return CallbackHandlerMessageType.SHOW_LESS;
    }

    @Override
    protected String getText(final Update update) {
        return TgBotConstants.LIST_OF_MORE;
    }

    @Override
    protected InlineKeyboardMarkup replyMarkup(final Update update) {
        final String callbackData = update.getCallbackQuery().getData();
        final Long callbackMessageId = getCallbackMessageId(callbackData);
        final String newCallbackData = getShowWord(callbackData) + TgBotConstants.COLON_MARK + callbackMessageId;

        return KeyboardUtil.setShowMoreButtonKeyboard(TgBotConstants.SHOW_MORE_TEXT, newCallbackData);
    }

}
