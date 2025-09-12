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

        String param = "";
        boolean flag = true;
        java.util.Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements() && flag) {
            String name = (String) names.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null) {
                for (int i = 0; i < values.length && flag; i++) {
                    String value = values[i];
                    if (value.equals("SampleValue")) {
                        param = name;
                        flag = false;
                    }
                }
            }
        }

        String a36502 = param;
        StringBuilder b36502 = new StringBuilder(a36502);
        b36502.append(" SafeStuff");
        b36502.replace(
                b36502.length() - "Chars".length(),
                b36502.length(),
                "Chars");
        java.util.HashMap<String, Object> map36502 = new java.util.HashMap<String, Object>();
        map36502.put("key36502", b36502.toString());
        String c36502 = (String) map36502.get("key36502");
        String d36502 = c36502.substring(0, c36502.length() - 1);
        String e36502 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d36502.getBytes())));
        String f36502 = e36502.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g36502 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g36502);

        String sql = "SELECT * from USERS where USERNAME=? and PASSWORD='" + bar + "'";

        try {
            java.sql.Connection connection =
                    org.test.testlib.helpers.DatabaseHelper.getSqlConnection();
            java.sql.PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            java.sql.ResultSet.TYPE_FORWARD_ONLY,
                            java.sql.ResultSet.CONCUR_READ_ONLY,
                            java.sql.ResultSet.CLOSE_CURSORS_AT_COMMIT);
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
}