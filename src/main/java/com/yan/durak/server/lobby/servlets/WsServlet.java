package com.yan.durak.server.lobby.servlets;

import com.yan.durak.server.ws.RemoteWsClient;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by yan.braslavsky on 6/3/2015.
 */
@SuppressWarnings("serial")
public class WsServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(RemoteWsClient.class);
    }
}
