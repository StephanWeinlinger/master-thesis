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

        String param = request.getParameter("SampleValue");
        if (param == null) param = "";

        String bar = "safe!";
        java.util.HashMap<String, Object> map86691 = new java.util.HashMap<String, Object>();
        map86691.put("keyA-86691", "a-Value");
        map86691.put("keyB-86691", param);
        map86691.put("keyC", "another-Value");
        bar = (String) map86691.get("keyB-86691");

        String sql = "SELECT * from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";

        try {
            java.sql.Statement statement =
                    org.test.testlib.helpers.DatabaseHelper.getSqlStatement();
            statement.addBatch(sql);
            int[] counts = statement.executeBatch();
            org.test.testlib.helpers.DatabaseHelper.printResults(sql, counts, response);
        } catch (java.sql.SQLException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
                return;
            } else throw new ServletException(e);
        }
    }
}