package com.yan.durak.server.lobby.playermathcing;

import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yan-Home on 3/11/2015.
 */
public class PlayerMatcher implements IPlayerMatcher, GameThread.GameThreadListener {

    private volatile static int PORT_NUMBER = 5010;
    private ConcurrentHashMap<GameData.GameType, GameData> mGamesMap;
    private String mTableSocketAddress;
    private Gson mGson;
    private Map<GameData.GameType, GameThread> mCreatedGames;

    public PlayerMatcher() {
        try {
            mTableSocketAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        mGamesMap = new ConcurrentHashMap<>();
        mCreatedGames = new ConcurrentHashMap<>();
        mGson = new Gson();
    }

    @Override
    public synchronized String matchPlayer(String payload) {
        Payload payloadData = mGson.fromJson(payload, Payload.class);

        if (!payloadData.isValidPayload()) {
            return "payload contains invalid data";
        }

        GameData.GameType gameType = GameData.GameType.fromPayloadValue(payloadData.getGameType());
        GameThread gt;
        if (mCreatedGames.containsKey(gameType)) {
            gt = mCreatedGames.get(gameType);
        } else {
            gt = new GameThread(mTableSocketAddress, ++PORT_NUMBER, gameType, this);
            gt.start();
            mCreatedGames.put(gameType, gt);
        }

        return mTableSocketAddress + ":" + gt.getPortNumber();
    }

    @Override
    public void onGameStart(GameThread gameThread) {
        mCreatedGames.remove(gameThread.getGameType());
    }
}