package com.example.wjk232.rxcontactstatus.stages;

public class RegistrationStruct
{
    final public String server;
    final public String username;
    final public String base64Image;
    final public String keyString;

    public RegistrationStruct(String server,
                              String username,
                              String base64Image,
                              String keyString){
        this.server = server;
        this.username = username;
        this.base64Image = base64Image;
        this.keyString = keyString;
    }
}
