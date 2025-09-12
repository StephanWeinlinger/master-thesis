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

        String bar = new Test().doSomething(request, param);

        String sql =
                "SELECT TOP 1 USERNAME from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";
        try {
            Object results =
                    org.test.testlib.helpers.DatabaseHelper.JDBCtemplate.queryForObject(
                            sql, new Object[] {}, String.class);
            response.getWriter().println("Your results are: ");

            response.getWriter()
                    .println(org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(results.toString()));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            response.getWriter()
                    .println(
                            "No results returned for query: "
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(sql));
        } catch (org.springframework.dao.DataAccessException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
            } else throw new ServletException(e);
        }
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a9334 = param;
            StringBuilder b9334 = new StringBuilder(a9334);
            b9334.append(" SafeStuff");
            b9334.replace(
                    b9334.length() - "Chars".length(),
                    b9334.length(),
                    "Chars");
            java.util.HashMap<String, Object> map9334 = new java.util.HashMap<String, Object>();
            map9334.put("key9334", b9334.toString());
            String c9334 = (String) map9334.get("key9334");
            String d9334 = c9334.substring(0, c9334.length() - 1);
            String e9334 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d9334.getBytes())));
            String f9334 = e9334.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g9334 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g9334);

            return bar;
        }
    }
}