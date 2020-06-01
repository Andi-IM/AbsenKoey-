package live.andiirham.absenkoey.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import live.andiirham.absenkoey.Model.DataSiswa;
import live.andiirham.absenkoey.Model.LineEventsModel;
import live.andiirham.absenkoey.Service.BotService;
import live.andiirham.absenkoey.Service.BotTemplate;
import live.andiirham.absenkoey.Service.DBService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private DBService dbService;

    private UserProfileResponse sender = null;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date date = new Date();

    // webhook response
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

            System.err.println(eventsPayload);                                                           // menampilkan payload
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

    // push message response
    @RequestMapping(value="/pushmessage/{id}/{message}", method=RequestMethod.GET)
    public ResponseEntity<String> pushmessage(
            @PathVariable("id") String userId,
            @PathVariable("message") String textMsg
    ){
        TextMessage textMessage = new TextMessage(textMsg);
        PushMessage pushMessage = new PushMessage(userId, textMessage);
        botService.push(pushMessage);

        return new ResponseEntity<String>("Push message:"+textMsg+"\nsent to: "+userId, HttpStatus.OK);
    }

    // greeting message
    private void greetingMessage(String replyToken, Source source, String additionalMessage) {          // pesan pembuka
        if(sender == null) {                                                                            // jika sender tidak ada (dalam database)
            String senderId = source.getSenderId();                                                     // dapatkan id sender
            sender          = botService.getProfile(senderId);                                          // masukkan id sender ke database
        }

        String greetingMessage = botTemplate.greetingMessage(source, sender);                           // bentuk template greeting yang ada di service template

        if (additionalMessage != null) {                                                                // jika pesan tambahan masih ada
            List<Message> messages = new ArrayList<>();                                                 // buat List
            messages.add(new TextMessage(additionalMessage));                                           // tambahkan tambahan pesan
         // messages.add(greetingMessage);                                                              // masukkan ke dalam {pesan pembuka}
            botService.reply(replyToken, messages);                                                     // balas ke sender
        } else {                                                                                        // jika pesan tambahan tidak ada
        botService.replyText(replyToken, greetingMessage);                                              // langsung balas ke sender
        }
    }

    // handlling join
    private void handleJointOrFollowEvent(String replyToken, Source source) {                            // jika bot join atau follow user
        greetingMessage(replyToken, source, null);                                        // tampilkan pesan pembuka
    }

    // handling message
    private void handleMessageEvent(MessageEvent event) {                                               // handle bentuk pesan
        String replyToken      = event.getReplyToken();
        MessageContent content = event.getMessage();
        Source source          = event.getSource();
        String senderId        = source.getUserId();
        sender                 = botService.getProfile(senderId);

        if(content instanceof TextMessageContent) {                                                     // jika pesan berupa teks
            handleTextMessage(replyToken, (TextMessageContent) content, source);                      // ladeni dengan teks
        } else {                                                                                        // tidak dikenal?
            greetingMessage(replyToken, source, null);                                    // pesan pembuka
        }
    }
    // handle untuk pesan tipe text
    private void handleTextMessage(String replyToken, TextMessageContent content, Source source)
    {
        if (source instanceof GroupSource) {
            handleGroupChats(replyToken, content.getText(), ((GroupSource) source).getGroupId());
        } else if (source instanceof RoomSource) {
            handleRoomChats(replyToken, content.getText(), ((RoomSource) source).getRoomId());
        } else if(source instanceof UserSource) {
            handleOneOnOneChats(replyToken, content.getText());
        } else {
            botService.replyText(replyToken,"Unknown Message Source!");
        }
    }

    // handling chats
    // handle untuk RoomChat
    private void handleRoomChats(String replyToken, String textMessage, String roomId)
    {
        String msgText = textMessage.toLowerCase();
        if (msgText.contains("bot leave")) {
            if (sender == null) {
                botService.replyText(replyToken, "Hi, tambahkan dulu bot AbsenKoey! sebagai teman!");
            } else {
                botService.leaveRoom(roomId);
            }
        } else if (msgText.contains("id") // tambahkan untuk pesan proses regex tambahan
        ) {
            processText(replyToken, msgText);
        }   // tambahkan pesan perintah disini
        else if(msgText.contains("start bot")){
            gettingStarted();
        }
        else if (msgText.contains("ambil absen")){
            ambilAbsen(replyToken);
        }
        else {
            handleFallbackMessage(replyToken, new RoomSource(roomId, sender.getUserId()));
        }
    }
    // handle untuk GroupChat
    private void handleGroupChats(String replyToken, String textMessage, String groupId)
    {
        String msgText = textMessage.toLowerCase();
        if (msgText.contains("bot leave")) {
            if (sender == null) {
                botService.replyText(replyToken, "Hi, tambahkan dulu bot Dicoding Event sebagai teman!");
            } else {
                botService.leaveGroup(groupId);
            }
        } else if (msgText.contains("id") // tambahkan untuk pesan proses regex tambahan
                    || msgText.contains("daftar")
                    || msgText.contains("list absen")
        ) {
            processText(replyToken, textMessage);
        }   // tambahkan pesan perintah disini
        else if(msgText.contains("start bot")){
            gettingStarted();
        } else if (msgText.contains("ambil absen")){
            ambilAbsen(replyToken);
        }
        else {
            handleFallbackMessage(replyToken, new GroupSource(groupId, sender.getUserId()));
        }
    }
    // peladen One - One chat
    private void handleOneOnOneChats(String replyToken, String textMessage)
    {
        String msgText = textMessage.toLowerCase();
        if (msgText.contains("id")
                || msgText.contains("find")
                || msgText.contains("join")
                || msgText.contains("teman")
        )
        {
            processText(replyToken, msgText);
        }   // tambahkan pesan disini
        else if (msgText.contains("start bot")){
            gettingStarted();
        }
        else if (msgText.contains("ambil absen")){
            handleFallbackAbsen(replyToken);
        }
        else {
            handleFallbackMessage(replyToken, new UserSource(sender.getUserId()));                        // panggil fallback
        }
    }

    private void handleFallbackAbsen(String replyToken) {
        String message = "Kamu tidak dapat mengambil absen disini!";
        botService.replyText(replyToken, message);
    }

    // text proccessing
    private void processText(String replyToken, String messageText) {
        String[] words = messageText.trim().split("\\s+");
        String intent  = words[0];

        if(intent.equalsIgnoreCase("id")) {
            handleRegisteringUser(replyToken, words);
        } else if (intent.equalsIgnoreCase("daftar")) {
            handleRegisterAbsen(replyToken, words);
        } else if (intent.equalsIgnoreCase("list absen")) {
            handleShowAbsen(replyToken, words);
        }
    }

    // help centre
    private void gettingStarted() {
        String message = "Daftar Perintah yang dapat kamu gunakan : " +
                "\n !daftar : untuk memasukkan daftar absen ke database" +
                "\n !absen : untuk memulai absen" +
                "\n !about : tentang";
        pushmessage(sender.getUserId(), message);
    }

    // handling programs
    // mendaftarkan akun line
    private void handleRegisteringUser(String replyToken, String[] words)
    {
        String registerMessage = null;
        String target=words.length > 1 ? words[1] : "";

        if (target.length()<=3){
            registerMessage = "Butuh lebih dari 3 karakter untuk mencari user";
        } else {
            String lineId = target.substring(target.indexOf("@") + 1);
            if(sender != null) {
                if (!sender.getDisplayName().isEmpty() && (lineId.length() > 0)) {
                    if (dbService.regLineID(sender.getUserId(), lineId, sender.getDisplayName()) != 0) {
                        ShowAbsensi(replyToken, "Pendaftaran user berhasil. Tapi kamu belum mendaftarkan absennya:");
                    } else {
                        userNotFoundFallback(replyToken, "Gagal melakukan pendaftaran user! Ikuti petunjuk berikut ini:");
                    }
                } else {
                    userNotFoundFallback(replyToken, "User tidak terdeteksi. Tambahkan dulu bot AbsenKoey sebagai teman!");
                }
            } else {
                userNotFoundFallback(replyToken, "Hi, tambahkan dulu bot AbsenKoey sebagai teman!");
            }
        }
    }
    // mendaftarkan absen
    private void handleRegisterAbsen(String replyToken, String[] words)
    {
        String target = words.length > 2 ? words[2] : "";
        String no_absen = target.substring(target.indexOf("_") + 1);
        String nama = target.substring(target.indexOf("__") + 1);
        String nobp = target.substring(target.indexOf("___") + 1);
        String user_id = sender.getUserId();

        int AbsenStatus = dbService.insertAbsen(no_absen, nama, nobp, user_id);

        if (AbsenStatus == -1) {
            String Message = "Kamu telah terdaftar dalam absen";
            botService.replyText(replyToken, Message);
            return;
        }

        if (AbsenStatus == 1) {
            String Message = "Pendaftaran berhasil! berikut daftar absensi ";
            botService.replyText(replyToken, Message);
            //broadcastNewFriendRegistered(no_absen);
        }
    }
    // input absen
    private void ambilAbsen(String replyToken)
    {
        String user_id = sender.getUserId();
        DataSiswa ds = (DataSiswa) dbService.getAbsenByUserId(user_id);
        String tanggal = dateFormat.format(date);
        String no_absen = ds.getNo_abs();
        String nama = ds.getNama();
        String no_bp = ds.getNo_bp();

        int absensiStatus = dbService.ambilAbsen(user_id, no_absen, nama, no_bp, tanggal);
        if (absensiStatus == -1) {
            String Message = "Kamu telah terdaftar dalam absen";
            botService.replyText(replyToken, Message);
            return;
        }

        if (absensiStatus == 1) {
            String Message = "Pendaftaran berhasil! berikut daftar absensi ";
            botService.replyText(replyToken, Message);
            //broadcastNewFriendRegistered(no_absen);
        }
    }

    // core programs
    private void handleShowAbsen(String replyToken, String[] words)
    {
        String target       = StringUtils.join(words, " ");
        String no_absen     = target.substring(target.indexOf("#") + 1).trim();

        List<DataSiswa> datasiswa = dbService.getAbsen(no_absen);

        if (datasiswa.size() > 0) {
            List<String> listAbsen = datasiswa.stream()
                    .map((siswa) ->
                            String.format(
                            "%s. %s (%s)",
                            siswa.no_abs, siswa.nama, siswa.no_bp
                    ))
                    .collect(Collectors.toList());

            String replyText  = "Database absen :\n\n";
            replyText += StringUtils.join(listAbsen, "\n");
            botService.replyText(replyToken, replyText);
        } else {
            botService.replyText(replyToken, "Absen tidak terdaftar!");
        }
    }
    private void handleJoinAbsen(String replyToken, String[] words)
    {
        /*String target       = words.length > 2 ? words[2] : "";
        String no_absen     = target.substring(target.indexOf("#") + 1);
        String nama         = target.substring(target.indexOf("?") + 1);
        String noBP         = target.substring(target.indexOf("="));
        String senderId     = sender.getUserId();
        String senderName   = sender.getDisplayName();

        int joinStatus = dbService.insertAbsen(no_absen, nama, noBP, senderName);

        if (joinStatus == -1) {
            TemplateMessage buttonsTemplate = botTemplate.createButton(
                    "Nama kamu sudah ada di dalam database!",
                    "Lihat Teman",
                    "teman #" + no_absen
            );
            botService.push(replyToken, buttonsTemplate);
            return;
        }

        if (joinStatus == 1) {
            TemplateMessage buttonsTemplate = botTemplate.createButton(
                    "Database diupdate! lihat daftar",
                    "Lihat Teman",
                    "teman #" + no_absen
            );
            botService.push(replyToken, buttonsTemplate);
            broadcastNewFriendJoined(no_absen, senderId);
            return;
        }

        botService.replyText(replyToken, "yah, kamu gagal bergabung event :(");*/
    }
    private void broadcastNewFriendRegistered(String no_absen)
    {
      /*  List<String> listIds;
        List<DataSiswa> getAbsen = dbService.getAbsen(no_absen);

        listIds = getAbsen.stream()
                .filter(
                        jointEvent   -> !jointEvent.user_id.equals(newFriendId))
                .map(
                        (jointEvent) -> jointEvent.user_id)
                .collect(Collectors.toList());

        Set<String> stringSet = new HashSet<>(listIds);
        String msg = "Hi temanmu terdaftar dengan no_absen " + no_absen;
        TemplateMessage buttonsTemplate = botTemplate.createButton(msg, "Lihat Teman", "teman #" + no_absen);
        botService.multicast(stringSet, buttonsTemplate);*/
    }
    private void ShowAbsensi(String replyToken) {
        ShowAbsensi(replyToken, null);
    }
    private void ShowAbsensi(String replyToken, String additionalInfo)
    {
        String userFound = dbService.findUser(sender.getUserId());

        if (userFound == null)
        {
            userNotFoundFallback(replyToken);
        }
    }

    // Fallback Method
    private void handleFallbackMessage(String replyToken, Source source) {                              // pesan tidak dikenal
        greetingMessage(replyToken, source, "Hi " + sender.getDisplayName() +
                ", aku belum  mengerti maksud kamu. Silahkan ikuti petunjuk ya :)");
    }
    private void userNotFoundFallback(String replyToken)
    {
        userNotFoundFallback(replyToken, null);
    }
    private void userNotFoundFallback(String replyToken, String additionalInfo)
    {
        List<String> messages = new ArrayList<>();

        if(additionalInfo != null) messages.add(additionalInfo);
        messages.add("Nama kamu belum terdaftar di database, daftarkan LINE ID kamu (pake \'id @\' ya)");
        messages.add("Contoh: id @user");

        botService.replyText(replyToken, messages.toArray(new String[messages.size()]));
        return;
    }
}
