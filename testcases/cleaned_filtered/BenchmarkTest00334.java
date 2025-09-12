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
        java.util.Enumeration<String> headers = request.getHeaders("SampleValue");

        if (headers != null && headers.hasMoreElements()) {
            param = headers.nextElement();
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String a30608 = param;
        StringBuilder b30608 = new StringBuilder(a30608);
        b30608.append(" SafeStuff");
        b30608.replace(
                b30608.length() - "Chars".length(),
                b30608.length(),
                "Chars");
        java.util.HashMap<String, Object> map30608 = new java.util.HashMap<String, Object>();
        map30608.put("key30608", b30608.toString());
        String c30608 = (String) map30608.get("key30608");
        String d30608 = c30608.substring(0, c30608.length() - 1);
        String e30608 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d30608.getBytes())));
        String f30608 = e30608.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g30608 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g30608);

        String sql = "SELECT * from USERS where USERNAME=? and PASSWORD='" + bar + "'";

        try {
            java.sql.Connection connection =
                    org.test.testlib.helpers.DatabaseHelper.getSqlConnection();
            java.sql.PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            java.sql.ResultSet.TYPE_FORWARD_ONLY,
                            java.sql.ResultSet.CONCUR_READ_ONLY);
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