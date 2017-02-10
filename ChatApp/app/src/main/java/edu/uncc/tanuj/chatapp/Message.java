package edu.uncc.tanuj.chatapp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinay on 11/18/2016.
 */

public class Message implements Serializable{
    String sender,messageText,receiver,msgKey,msgType,msgSub,imageUri,senderKey;
    Date date;

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    boolean read=false;
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getMsgSub() {
        return msgSub;
    }

    public void setMsgSub(String msgSub) {
        this.msgSub = msgSub;
    }

    public Message() {
    }

    public Message(String sender, String messageText, String receiver, String msgType, String msgKey, Date date) {
        this.sender = sender;
        this.messageText = messageText;
        this.receiver = receiver;
        this.msgType = msgType;
        this.msgKey = msgKey;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("messageText", messageText);
        result.put("msgSub", msgSub);
        result.put("receiver", receiver);
        result.put("msgType", msgType);
        result.put("msgKey", msgKey);
        result.put("date", date);
        result.put("imageUri", imageUri);
        result.put("receiverKey", senderKey);
        result.put("read", read);
        return result;
    }

}
