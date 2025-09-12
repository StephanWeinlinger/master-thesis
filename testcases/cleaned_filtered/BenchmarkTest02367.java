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

        String bar = doSomething(request, param);

        String sql = "SELECT * from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";

        try {
            java.sql.Statement statement =
                    org.test.testlib.helpers.DatabaseHelper.getSqlStatement();
            statement.execute(sql, new int[] {1, 2});
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

        String a41891 = param;
        StringBuilder b41891 = new StringBuilder(a41891);
        b41891.append(" SafeStuff");
        b41891.replace(
                b41891.length() - "Chars".length(),
                b41891.length(),
                "Chars");
        java.util.HashMap<String, Object> map41891 = new java.util.HashMap<String, Object>();
        map41891.put("key41891", b41891.toString());
        String c41891 = (String) map41891.get("key41891");
        String d41891 = c441891.substring(0, c41891.length() - 1);
        String e41891 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d41891.getBytes())));
        String f41891 = e41891.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g41891 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g41891);

        return bar;
    }
}