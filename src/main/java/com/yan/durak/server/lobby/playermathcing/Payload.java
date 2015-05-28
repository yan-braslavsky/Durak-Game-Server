package com.yan.durak.server.lobby.playermathcing;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yan-Home on 4/2/2015.
 */
public class Payload {

    @SerializedName("userId")
    String userId;
    @SerializedName("gameType")
    String gameType;

    public String getUserId() {
        return userId;
    }

    public String getGameType() {
        return gameType;
    }

    public boolean isValidPayload() {
        return userId != null && gameType != null;
    }
}
