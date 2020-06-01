package live.andiirham.absenkoey.Service;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class BotTemplate
{
    public TemplateMessage createButton(String message, String actionTitle, String actionText) {
        ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                null,
                null,
                message,
                Collections.singletonList(new MessageAction(actionTitle, actionText))
        );

        return new TemplateMessage(actionTitle, buttonsTemplate);
    }

    public String greetingMessage(Source source, UserProfileResponse sender)
    {
        String message = "Hi %s! terimakasih telah mengundang bot AbsenKoey! \n ketik \"Start\" untuk memulai.";

        if (source instanceof UserSource){
            message = String.format(message, sender.getDisplayName());
        } else {
            message = "Unknown Message Source!";
        }
        return message;
    }
}
