package com.yan.durak.server.lobby.servlets;

import com.yan.durak.server.lobby.playermathcing.IPlayerMatcher;
import com.yan.durak.server.lobby.playermathcing.PlayerMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Yan-Home on 5/27/2015.
 */
public class LobbyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static IPlayerMatcher mPlayerMatcher = new PlayerMatcher();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String responseString = "Server is running on : " + InetAddress.getLocalHost().getHostAddress();
        resp.getOutputStream().write(responseString.getBytes());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //user requests the server with his user ID
        String payload = req.getParameter("payload");

        if (payload == null) {
            resp.getOutputStream().write("payload is required !".getBytes());
            return;
        }

        String responseString = mPlayerMatcher.matchPlayer(payload);
        resp.getOutputStream().write(responseString.getBytes());
    }
}