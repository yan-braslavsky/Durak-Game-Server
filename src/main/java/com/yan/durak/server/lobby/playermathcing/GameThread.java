package com.yan.durak.server.lobby.playermathcing;

import com.yan.durak.gamelogic.GameStarter;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.utils.LogUtils;
import com.yan.durak.server.sockets.RemoteSocketClient;
import com.yan.durak.server.ws.WSServer;

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

    interface GameThreadListener {
        void onGameStart(GameThread gameThread);
    }

    private static int counter = 0;

    private final GameData.GameType mGameType;
    private volatile int mNumOfConnectedPlayers;
    private int mPortNumber;
    private String mTableSocketAddress;
    private ConcurrentHashMap<Integer, IRemoteClient> mConnectedClients;
    private GameThreadListener mGameThreadListener;

    public GameThread(String tableSocketAddress, int portNumber, GameData.GameType gameType, GameThreadListener listener) {
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
            connectClientsViaWebSocket();
            //TODO : uncomment this method to use socket connection
//            connectClientsViaPlainSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectClientsViaWebSocket() throws IOException {
        WSServer server = new WSServer(mTableSocketAddress, mPortNumber, new WSServer.ConnectionListener() {
            @Override
            public void onRemoteClientConnected(IRemoteClient client) {
                mConnectedClients.put(mNumOfConnectedPlayers, client);
                LogUtils.log(getName() + " Remote player " + mNumOfConnectedPlayers + " connected !");
                mNumOfConnectedPlayers++;
            }

            @Override
            public void onRemoteClientDisconnected(IRemoteClient client) {
                //TODO : handle somehow ?
            }
        });

        server.start();
        waitForPlayersToConnect();

        //notify listener of game start
        if (mGameThreadListener != null)
            mGameThreadListener.onGameStart(this);
        try {
            startGameWithConnectedClients();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void waitForPlayersToConnect() {
        while (mNumOfConnectedPlayers != mGameType.getNumberOfHumanPlayers()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void startGameWithConnectedClients() {
        LogUtils.log(getName() + " Starting a game ");
        (new GameStarter(mConnectedClients.get(0), mConnectedClients.get(1), mConnectedClients.get(3))).start();
    }


    private void connectClientsViaPlainSocket() throws IOException {

        System.out.println(getName() + " Waiting for " + mGameType.getNumberOfHumanPlayers() + " remote players to connect");
        while (mNumOfConnectedPlayers != mGameType.getNumberOfHumanPlayers()) {
            mConnectedClients.put(mNumOfConnectedPlayers, waitForClientToConnect());
            LogUtils.log(getName() + " Remote player " + mNumOfConnectedPlayers + " connected !");
            mNumOfConnectedPlayers++;
        }

        //notify listener of game start
        if (mGameThreadListener != null)
            mGameThreadListener.onGameStart(this);
        try {
            startGameWithConnectedClients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IRemoteClient waitForClientToConnect() throws IOException {
        IRemoteClient remoteClient = null;
        ServerSocket listener = new ServerSocket(mPortNumber);
        try {
            //listen to communication.connection (Blocking)
            Socket socket = listener.accept();
            //communication.connection received , create a remote client
            remoteClient = new RemoteSocketClient(socket);
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