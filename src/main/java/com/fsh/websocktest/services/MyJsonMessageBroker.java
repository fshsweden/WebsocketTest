package com.fsh.websocktest.services;

import com.fsh.websocktest.messages.BagOfKeyValues;
import com.fsh.websocktest.messages.IMessage;
import com.fsh.websocktest.messages.IMessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class DummyInstrument {
    @NonNull
    private String name;
    @NonNull
    private String longname;

    public static List<DummyInstrument> getDummyList() {
        List<DummyInstrument> list = new ArrayList<DummyInstrument>();

        list.add(new DummyInstrument("AAPL", "Apple Inc"));
        list.add(new DummyInstrument("IBM", "IBM Inc"));
        list.add(new DummyInstrument("NFLX", "Netflix"));
        list.add(new DummyInstrument("AMZN", "Amazon Inc"));

        return list;
    }
}

@Service
public class MyJsonMessageBroker {

    private Gson gson = new Gson();
    Map<String, WebSocketSession> sessions = new HashMap<String, WebSocketSession>();
    Map<String, List<String>> subscriptions = new HashMap<String, List<String>>();

    public void handleIncomingMessage(WebSocketSession session, BagOfKeyValues bokv) {
        /* do something with the message! */
        String msgType = bokv.getKeyValue("msgType");
        if (msgType.equals("InstrumentRequest")) {

            DummyInstrument.getDummyList().forEach((d) -> {
                BagOfKeyValues b = new BagOfKeyValues();
                b.setKeyValue("msgType", "InstrumentReply");
                b.setKeyValue("name", d.getName());
                b.setKeyValue("longname", d.getLongname());
                b.setKeyValue("last", "0");
                sendMessageToSession(session, b);
            });

            // Horrible Hack

            BagOfKeyValues b = new BagOfKeyValues();
            b.setKeyValue("msgType", "InstrumentReply");
            b.setKeyValue("name", "nothing");
            b.setKeyValue("longname", "nothing");
            b.setKeyValue("last", "1");

            sendMessageToSession(session, b);
        }
        else {

            // A General Error Message
            BagOfKeyValues b = new BagOfKeyValues();
            b.setKeyValue("msgType", "GeneralError");
            b.setKeyValue("msg", "Unknown message received");
            b.setKeyValue("msgIn", msgType);

            sendMessageToSession(session, b);
        }
    }

    private String convertOutgoingMessageToJson(BagOfKeyValues bokv) {
        String json = gson.toJson(bokv);
        // HACK!
        // {"keyValues":{"longname":"nothing","msgType":"InstrumentReply","last":"1","name":"nothing"}}
        json = json.substring(13, json.length()-1);
        System.out.println("Sending " + json);
        return json;
    }

    public boolean registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
        return true;
    }
    public boolean unregisterSession(WebSocketSession session) {
        // bug: lookup session
        sessions.remove(session.getId());
        return true;
    }

    public void broadcast(BagOfKeyValues bokv) {
        String json = convertOutgoingMessageToJson(bokv);
        TextMessage tm = new TextMessage(json);
        sessions.forEach((k,v) -> {
            try {
                v.sendMessage(tm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendMessageToSession(WebSocketSession session,  BagOfKeyValues bokv) {
        sendMessageToSession(session.getId(), bokv);
    }

    public void sendMessageToSession(String id, BagOfKeyValues bokv) {
        WebSocketSession session = sessions.get(id);
        if (session != null) {
            try {
                String json = convertOutgoingMessageToJson(bokv);
                System.out.println("Outgoing message:" + json);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Can't send to session! Session <" + id + "> not found!");
        }
    }
}
