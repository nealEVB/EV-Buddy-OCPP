package com.evbuddy.ocpp.server.payload;

public class AuthorizePayloads {
    public static class Req { public String idTag; }
    public static class IdTagInfo { public String status; public IdTagInfo(String s){status=s;} }
    public static class Res { public IdTagInfo idTagInfo; public Res(IdTagInfo i){idTagInfo=i;} }
}
