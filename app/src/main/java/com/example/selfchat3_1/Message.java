package com.example.selfchat3_1;

public class Message {
    public String Id, Timestamp, Content;

    @Override
    public String toString()
    {
        return this.Timestamp + "#" + Content;
    }

}
