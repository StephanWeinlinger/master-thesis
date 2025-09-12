package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample")
public class SampleValue extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        org.test.testlib.helpers.SeparateClassRequest scr =
                new org.test.testlib.helpers.SeparateClassRequest(request);
        String param = scr.getTheValue("SampleValue");

        String bar;
        String guess = "ABC";
        char switchTarget = guess.charAt(2);

        switch (switchTarget) {
            case 'A':
                bar = param;
                break;
            case 'B':
                bar = "bobs_your_uncle";
                break;
            case 'C':
            case 'D':
                bar = param;
                break;
            default:
                bar = "bobs_your_uncle";
                break;
        }

        String sql = "INSERT INTO users (username, password) VALUES ('foo','" + bar + "')";

        try {
            java.sql.Statement statement =
                    org.test.testlib.helpers.DatabaseHelper.getSqlStatement();
            int count = statement.executeUpdate(sql, new int[] {1, 2});
            org.test.testlib.helpers.DatabaseHelper.outputUpdateComplete(sql, response);
        } catch (java.sql.SQLException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
                return;
            } else throw new ServletException(e);
        }
    }
}