package com.alon;

import java.io.IOException;

public class MainServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        System.out.println("in doGet");
        String levelAsString = request.getParameter("level");
        if (levelAsString.isEmpty()) {
            return;
        }
        int level = 0;
        try {
            level =Integer.valueOf(levelAsString);
        } catch (Exception ex){
            return;
        }
        if (level < 1 || level > 4){
            System.out.println("illegal level parameter");
            return;
        }
        String result = null;
        switch (level){
            case 1:
                result = "40100";
                break;
            case 2:
                result = "30100";
                break;
            case 3:
                result = "20100";
                break;
            case 4:
                result = "10100";
        }
        System.out.println(result);
        response.getWriter().write(result);

    }
}
