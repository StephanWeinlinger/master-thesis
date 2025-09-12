package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-04/SampleValue")
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

        String bar = doSomething(request, param);

        String sql = "SELECT * from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";

        try {
            java.sql.Statement statement =
                    org.test.testlib.helpers.DatabaseHelper.getSqlStatement();
            java.sql.ResultSet rs = statement.executeQuery(sql);
            org.test.testlib.helpers.DatabaseHelper.printResults(rs, sql, response);
        } catch (java.sql.SQLException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
                return;
            } else throw new ServletException(e);
        }
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a16667 = param;
        StringBuilder b16667 = new StringBuilder(a16667);
        b16667.append(" SafeStuff");
        b16667.replace(
                b16667.length() - "Chars".length(),
                b16667.length(),
                "Chars");
        java.util.HashMap<String, Object> map16667 = new java.util.HashMap<String, Object>();
        map16667.put("key16667", b16667.toString());
        String c16667 = (String) map16667.get("key16667");
        String d16667 = c16667.substring(0, c16667.length() - 1);
        String e16667 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d16667.getBytes())));
        String f16667 = e16667.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g16667 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g16667);

        return bar;
    }
}