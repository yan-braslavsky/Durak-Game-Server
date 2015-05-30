package com.yan.durak.server.ws;

import com.yan.durak.gamelogic.communication.connection.ISocketClient;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yan-Home on 5/30/2015.
 */
public class WSServer extends WebSocketServer {

    private final ConcurrentHashMap<WebSocket, RemoteWSClient> mWsSocketToClientMap;
    private final ConnectionListener mConnectionListener;

    public interface ConnectionListener {
        void onRemoteClientConnected(final ISocketClient client);
        void onRemoteClientDisconnected(final ISocketClient client);
    }

    /**
     * @param hostname only domain name , without protocol prefix
     * @param port     server will listen on port specified
     * @param listener listener that will be notified on connection
     */
    public WSServer(String hostname, int port, ConnectionListener listener) {
        super(new InetSocketAddress(hostname, port));
        mWsSocketToClientMap = new ConcurrentHashMap<>();
        mConnectionListener = listener;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
        RemoteWSClient wsClient = new RemoteWSClient(conn);
        mWsSocketToClientMap.put(conn, wsClient);
        mConnectionListener.onRemoteClientConnected(wsClient);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        mConnectionListener.onRemoteClientDisconnected(mWsSocketToClientMap.get(conn));
        mWsSocketToClientMap.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message from " + conn.getRemoteSocketAddress() + ": " + message);
        mWsSocketToClientMap.get(conn).onSocketMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }

//    public static void main(String[] args) {
//        String host = "localhost";
//        int port = 8887;
//
//        WebSocketServer server = new WSServer(host,port);
//        server.run();
//    }
}
