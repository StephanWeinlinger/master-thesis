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

        try {
            String sql = "SELECT * from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";

            org.test.testlib.helpers.DatabaseHelper.JDBCtemplate.batchUpdate(sql);
            response.getWriter()
                    .println(
                            "No results can be displayed for query: "
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(sql)
                                    + "<br>"
                                    + " because the Spring batchUpdate method doesn't return results.");
        } catch (org.springframework.dao.DataAccessException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
            } else throw new ServletException(e);
        }
    }
    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a21456 = param;
        StringBuilder b21456 = new StringBuilder(a21456);
        b21456.append(" SafeStuff");
        b21456.replace(
                b21456.length() - "Chars".length(),
                b21456.length(),
                "Chars");
        java.util.HashMap<String, Object> map21456 = new java.util.HashMap<String, Object>();
        map21456.put("key21456", b21456.toString());
        String c21456 = (String) map21456.get("key21456");
        String d21456 = c21456.substring(0, c21456.length() - 1);
        String e21456 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d21456.getBytes())));
        String f21456 = e21456.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g21456 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g21456);

        return bar;
    }
}