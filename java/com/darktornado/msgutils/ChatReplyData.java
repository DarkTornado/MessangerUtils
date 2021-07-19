package com.darktornado.msgutils;

public class ChatReplyData {
    public String input;
    public String output;
    public int type;
    public int roomType;

    public ChatReplyData(String input, String output, int type, int roomType) {
        this.input = input;
        this.output = output;
        this.type = type;
        this.roomType = roomType;
    }

    public ChatReplyData copy(){
        return new ChatReplyData(input, output, type, roomType);
    }

    public String toJson() {
        return "{\"input\":\"" + toJson(input) + "\",\"output\":\"" + toJson(output) + "\",\"type\":" + type + ",\"roomType\":" + roomType + "}";
    }

    private String toJson(String str){
        return str.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
