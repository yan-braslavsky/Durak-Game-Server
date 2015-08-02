package com.yan.durak.server.lobby.playermathcing;

import com.yan.durak.gamelogic.GameStarter;
import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import com.yan.durak.gamelogic.game.IGameRules;
import com.yan.durak.gamelogic.utils.LogUtils;
import com.yan.durak.server.ws.IWsConnectionListener;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yan-Home on 3/11/2015.
 * Awaits for all remote players to join via socket connection
 * on provided port
 */
public class GameThread extends Thread implements IWsConnectionListener{

    interface GameThreadListener {
        void onGameStart(GameThread gameThread);
    }

    private static int counter = 0;

    private final GameData.GameType mGameType;
    private volatile int mNumOfConnectedPlayers;
    private String mTableSocketAddress;
    private ConcurrentHashMap<Integer, IRemoteClient> mConnectedClients;
    private GameThreadListener mGameThreadListener;

    public GameThread(String tableSocketAddress, GameData.GameType gameType, GameThreadListener listener) {
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

        //TODO : Connect players using websocket
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

    @Override
    public void onRemoteWsClientConnected(IRemoteClient remoteClient) {
        mConnectedClients.put(mNumOfConnectedPlayers, remoteClient);
        mNumOfConnectedPlayers++;
    }


    private void startGameWithConnectedClients() {
        LogUtils.log(getName() + " Starting a game ");
        final IRemoteClient[] clients = new IRemoteClient[]{
                mConnectedClients.get(0), mConnectedClients.get(1), mConnectedClients.get(3)};
        (new GameStarter(new IGameRules() {
            @Override
            public int getTotalPlayersInGameAmount() {
                return clients.length;
            }
        }, clients)).start();
    }


//    private void connectClientsViaPlainSocket() throws IOException {
//
//        System.out.println(getName() + " Waiting for " + mGameType.getNumberOfHumanPlayers() + " remote players to connect");
//        while (mNumOfConnectedPlayers != mGameType.getNumberOfHumanPlayers()) {
//            mConnectedClients.put(mNumOfConnectedPlayers, waitForClientToConnect());
//            LogUtils.log(getName() + " Remote player " + mNumOfConnectedPlayers + " connected !");
//            mNumOfConnectedPlayers++;
//        }
//
//        //notify listener of game start
//        if (mGameThreadListener != null)
//            mGameThreadListener.onGameStart(this);
//        try {
//            startGameWithConnectedClients();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private IRemoteClient waitForClientToConnect() throws IOException {
//        IRemoteClient remoteClient = null;
//        ServerSocket listener = new ServerSocket(mPortNumber);
//        try {
//            //listen to communication.connection (Blocking)
//            Socket socket = listener.accept();
//            //communication.connection received , create a remote client
//            remoteClient = new RemoteSocketClient(socket);
//        } finally {
//            listener.close();
//        }
//        return remoteClient;
//    }

    public GameData.GameType getGameType() {
        return mGameType;
    }

}