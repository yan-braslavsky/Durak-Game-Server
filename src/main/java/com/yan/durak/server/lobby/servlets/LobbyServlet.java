package com.yan.durak.server.lobby.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Yan-Home on 5/27/2015.
 */
public class LobbyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //redeploy
        resp.getWriter().print("Hello from Java built with Gradle!\n");
    }
}