package com.yan.durak.server.lobby.playermathcing;

import com.yan.durak.gamelogic.GameStarter;
import com.yan.durak.gamelogic.communication.connection.RemoteClient;
import com.yan.durak.gamelogic.utils.LogUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yan-Home on 3/11/2015.
 * Awaits for all remote players to join via socket connection
 * on provided port
 */
public class GameThread extends Thread {

    interface GameThreadListener{
        void onGameStart(GameThread gameThread);
    }

    private static int counter = 0;

    private final GameData.GameType mGameType;
    private int mNumOfConnectedPlayers;
    private int mPortNumber;
    private String mTableSocketAddress;
    private ConcurrentHashMap<Integer, RemoteClient> mConnectedClients;
    private GameThreadListener mGameThreadListener;

    public GameThread(String tableSocketAddress, int portNumber, GameData.GameType gameType,GameThreadListener listener) {
        mPortNumber = portNumber;
        mTableSocketAddress = tableSocketAddress;
        mConnectedClients = new ConcurrentHashMap<>(3);
        mGameType = gameType;
        mGameThreadListener = listener;

        counter++;
        setName("GameThread-" + counter);
    }

    @Override
    public void run() {
        try {
            connectClients();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectClients() throws IOException {

        LogUtils.log(getName() + " Waiting for " + mGameType.getNumberOfHumanPlayers() + " remote players to connect");
        while (mNumOfConnectedPlayers != mGameType.getNumberOfHumanPlayers()) {
            mConnectedClients.put(mNumOfConnectedPlayers, waitForClientToConnect());
            LogUtils.log(getName() + " Remote player " + mNumOfConnectedPlayers + " connected !");
            mNumOfConnectedPlayers++;
        }

        //notify listener of game start
        if(mGameThreadListener != null)
            mGameThreadListener.onGameStart(this);
        try{
            startGameWithConnectedClients();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startGameWithConnectedClients() {
        LogUtils.log(getName() + " Starting a game ");
        (new GameStarter(mConnectedClients.get(0), mConnectedClients.get(1), mConnectedClients.get(3))).start();
    }

    private RemoteClient waitForClientToConnect() throws IOException {
        RemoteClient remoteClient = null;
        ServerSocket listener = new ServerSocket(mPortNumber);
        try {
            //listen to communication.connection (Blocking)
            Socket socket = listener.accept();
            //communication.connection received , create a remote client
            remoteClient = new RemoteClient(socket);
        } finally {
            listener.close();
        }
        return remoteClient;
    }

    public GameData.GameType getGameType() {
        return mGameType;
    }

    public int getPortNumber() {
        return mPortNumber;
    }
}