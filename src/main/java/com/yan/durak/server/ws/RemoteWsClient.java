package com.yan.durak.server.ws;

import com.yan.durak.gamelogic.communication.connection.IRemoteClient;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by yan.braslavsky on 6/3/2015.
 */
public class RemoteWsClient extends WebSocketAdapter implements IRemoteClient {
    public static IWsConnectionListener CURRENT_CONNECTION_LISTENER;

    private final Deque<String> mMessageQueue;
    private final IWsConnectionListener mWsConnectionListener;

    public RemoteWsClient() {
        mMessageQueue = new LinkedBlockingDeque<>();
        mWsConnectionListener = CURRENT_CONNECTION_LISTENER;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        System.out.println("Socket Connected: " + session);
        mWsConnectionListener.onRemoteWsClientConnected(this);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        mMessageQueue.add(message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    @Override
    public void sendMessage(String msg) {
        try {
            getRemote().sendString(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readMessage() {
        while (mMessageQueue.isEmpty()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mMessageQueue.poll();
    }

    @Override
    public void disconnect() {
        getSession().close();
    }
}