package live.andiirham.absenkoey.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import live.andiirham.absenkoey.Model.DaftarAbsen;
import live.andiirham.absenkoey.Model.LineEventsModel;
import live.andiirham.absenkoey.Service.BotService;
import live.andiirham.absenkoey.Service.BotTemplate;
import live.andiirham.absenkoey.Service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class BotController {

    @Autowired
    @Qualifier("lineSignatureValidator")
    private LineSignatureValidator lineSignatureValidator;

    @Autowired
    private BotService botService;

    @Autowired
    private BotTemplate botTemplate;

    private UserProfileResponse sender = null;

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> webhook
            (@RequestHeader("X-Line-Signature") String xLineSignature,
             @RequestBody String eventsPayload)
    {
        try{
            // validasi line signature. matikan validasi ini jika masih dalam pengembangan
            //if (!lineSignatureValidator.validateSignature(eventsPayload.getBytes(), xLineSignature)) {
            //    throw new RuntimeException("Invalid Signature Validation");
            //}

            System.out.println(eventsPayload);                                                           // menampilkan payload
            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();                       // inisiasi object Mapper
            LineEventsModel eventsModel = objectMapper.readValue(eventsPayload, LineEventsModel.class);  // inisiasi eventModel

            eventsModel.getEvents().forEach((event)->{                                                   // Mendapatkan event
                // kode reply message disini

                if (event instanceof JoinEvent || event instanceof FollowEvent) {                        // jika event didapat join
                    String replyToken = ((ReplyEvent) event).getReplyToken();                            // dapatkan token reply
                    handleJointOrFollowEvent(replyToken, event.getSource());                             // fungsikan ke join atau follow
                } else if (event instanceof MessageEvent) {                                              // jika event didapatkan message
                    handleMessageEvent((MessageEvent) event);                                            // fungsikan ke messageEvent
                }
            });

            return new ResponseEntity<>(HttpStatus.OK);                                                  // kembalikan ke status http
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void handleJointOrFollowEvent(String replyToken, Source source) {                            // jika bot join atau follow user
        greetingMessage(replyToken, source, null);                                        // tampilkan pesan pembuka
    }

    private void greetingMessage(String replyToken, Source source, String additionalMessage) {          // pesan pembuka
        if(sender == null) {                                                                            // jika sender tidak ada (dalam database)
            String senderId = source.getSenderId();                                                     // dapatkan id sender
            sender          = botService.getProfile(senderId);                                          // masukkan id sender ke database
        }

        TemplateMessage greetingMessage = botTemplate.greetingMessage(source, sender);                  // bentuk template greeting yang ada di service template

        if (additionalMessage != null) {                                                                // jika pesan tambahan masih ada
            List<Message> messages = new ArrayList<>();                                                 // buat List
            messages.add(new TextMessage(additionalMessage));                                           // tambahkan tambahan pesan
            messages.add(greetingMessage);                                                              // masukkan ke dalam {pesan pembuka}
            botService.reply(replyToken, messages);                                                     // balas ke sender
        } else {                                                                                        // jika pesan tambahan tidak ada
            botService.reply(replyToken, greetingMessage);                                              // langsung balas ke sender
        }
    }

    private void handleMessageEvent(MessageEvent event) {                                               // handle bentuk pesan
        String replyToken      = event.getReplyToken();
        MessageContent content = event.getMessage();
        Source source          = event.getSource();
        String senderId        = source.getUserId();
        sender                 = botService.getProfile(senderId);

        if(content instanceof TextMessageContent) {                                                     // jika pesan berupa teks
            //handleTextMessage(replyToken, (TextMessageContent) content, source);                      // ladeni dengan teks
            greetingMessage(replyToken, source, null);
        } else {                                                                                        // tidak dikenal?
            greetingMessage(replyToken, source, null);                                    // pesan pembuka
        }
    }

    private void handleOneOnOneChats(String replyToken, String textMessage) {                           // peladen One - One chat
        String msgText = textMessage.toLowerCase();
        if (msgText.contains("id"))                                                                      // jika pesan mengandung keyword id
        {
            processText(replyToken, msgText);
        }   // tambahkan pesan disini
        else {
            handleFallbackMessage(replyToken, new UserSource(sender.getUserId()));                      // panggil fallback
        }
    }

    private void handleFallbackMessage(String replyToken, Source source) {                              // pesan tidak dikenal
        greetingMessage(replyToken, source, "Hi " + sender.getDisplayName() +
                ", aku belum  mengerti maksud kamu. Silahkan ikuti petunjuk ya :)");
    }

    private void processText(String replyToken, String messageText) {
        String[] words = messageText.trim().split("\\s+");
        String intent  = words[0];

        if(intent.equalsIgnoreCase("id")) {
            handleRegisteringUser(replyToken, words);
        }
    }

    private void handleRegisteringUser(String replyToken, String[] words)
    {

    }

    private void broadcastNewFriendJoined(String eventId, String newFriendId) {
        List<String> listIds;
        List<DaftarAbsen> jointEvents = DBService;

        listIds = jointEvents.stream()
                .filter(jointEvent -> !jointEvent.user_id.equals(newFriendId))
                .map((jointEvent) -> jointEvent.user_id)
                .collect(Collectors.toList());

        Set<String> stringSet = new HashSet<>(listIds);
        String msg = "Hi, ada teman baru telah bergabung di event " + eventId;
        TemplateMessage buttonsTemplate = botTemplate.createButton(msg, "Lihat Teman", "teman #" + eventId);
        botService.multicast(stringSet, buttonsTemplate);
    }
}
