package com.yan.durak.server.lobby;

import com.yan.durak.server.lobby.servlets.LobbyServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by Yan-Home on 5/27/2015.
 */
public class Main {

    public static final String DEBUG_PORT = "80";

    //test change
    public static void main(String[] args) throws Exception {
        String port = System.getenv("PORT");
        if(port == null)
            port = DEBUG_PORT;

        Server server = new Server(Integer.valueOf(port));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new LobbyServlet()), "/*");
        server.start();
        server.join();
    }
}