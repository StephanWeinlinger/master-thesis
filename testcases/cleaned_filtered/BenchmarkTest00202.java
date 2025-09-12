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
        if (request.getHeader("SampleValue") != null) {
            param = request.getHeader("SampleValue");
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String a15497 = param;
        StringBuilder b15497 = new StringBuilder(a15497);
        b15497.append(" SafeStuff");
        b15497.replace(
                b15497.length() - "Chars".length(),
                b15497.length(),
                "Chars");
        java.util.HashMap<String, Object> map15497 = new java.util.HashMap<String, Object>();
        map15497.put("key15497", b15497.toString());
        String c15497 = (String) map15497.get("key15497");
        String d15497 = c15497.substring(0, c15497.length() - 1);
        String e15497 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d15497.getBytes())));
        String f15497 = e15497.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g15497 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g15497);

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