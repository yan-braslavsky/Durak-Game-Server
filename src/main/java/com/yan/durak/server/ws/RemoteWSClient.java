package com.yan.durak.server.ws;

import com.yan.durak.gamelogic.communication.connection.ISocketClient;
import org.java_websocket.WebSocket;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Yan-Home on 5/30/2015.
 */
public class RemoteWSClient implements ISocketClient {

    private final WebSocket mWs;
    private final Deque<String> mMessageQueue;

    RemoteWSClient(WebSocket ws) {
        this.mWs = ws;
        mMessageQueue = new LinkedBlockingDeque<>();
    }

    void onSocketMessage(final String msg) {
        mMessageQueue.add(msg);
    }

    @Override
    public void sendMessage(final String msg) {
        mWs.send(msg);
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
        mWs.close();
    }
}
