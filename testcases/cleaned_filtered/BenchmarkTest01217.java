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

        String bar = new Test().doSomething(request, param);

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

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a89502 = param;
            StringBuilder b89502 = new StringBuilder(a89502);
            b89502.append(" SafeStuff");
            b89502.replace(
                    b89502.length() - "Chars".length(),
                    b89502.length(),
                    "Chars");
            java.util.HashMap<String, Object> map89502 = new java.util.HashMap<String, Object>();
            map89502.put("key89502", b89502.toString());
            String c89502 = (String) map89502.get("key89502");
            String d89502 = c89502.substring(0, c89502.length() - 1);
            String e89502 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d89502.getBytes())));
            String f89502 = e89502.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g89502 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g89502);

            return bar;
        }
    }
}