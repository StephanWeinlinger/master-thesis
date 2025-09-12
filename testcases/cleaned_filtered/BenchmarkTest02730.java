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

        String bar = doSomething(request, param);

        String sql = "SELECT * from USERS where USERNAME=? and PASSWORD='" + bar + "'";

        try {
            java.sql.Connection connection =
                    org.test.testlib.helpers.DatabaseHelper.getSqlConnection();
            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql, new String[] {"Column1", "Column2"});
            statement.setString(1, "foo");
            statement.execute();
            org.test.testlib.helpers.DatabaseHelper.printResults(statement, sql, response);
        } catch (java.sql.SQLException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
                return;
            } else throw new ServletException(e);
        }
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a81593 = param;
        StringBuilder b81593 = new StringBuilder(a81593);
        b81593.append(" SafeStuff");
        b81593.replace(
                b81593.length() - "Chars".length(),
                b81593.length(),
                "Chars");
        java.util.HashMap<String, Object> map81593 = new java.util.HashMap<String, Object>();
        map81593.put("key81593", b81593.toString());
        String c81593 = (String) map81593.get("key81593");
        String d81593 = c81593.substring(0, c81593.length() - 1);
        String e81593 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d81593.getBytes())));
        String f81593 = e81593.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g81593 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g81593);

        return bar;
    }
}