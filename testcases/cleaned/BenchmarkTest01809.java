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

        String bar = new Test().doSomething(request, param);

        String sql = "SELECT * from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";
        try {
            java.util.List<java.util.Map<String, Object>> list =
                    org.test.testlib.helpers.DatabaseHelper.JDBCtemplate.queryForList(sql);
            response.getWriter().println("Your results are: <br>");


            for (Object o : list) {
                response.getWriter()
                        .println(
                                org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(o.toString())
                                        + "<br>");

            }
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            response.getWriter()
                    .println(
                            "No results returned for query: "
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(sql));
        } catch (org.springframework.dao.DataAccessException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
                return;
            } else throw new ServletException(e);
        }
    }
    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a64594 = param;
            StringBuilder b64594 = new StringBuilder(a64594);
            b64594.append(" SafeStuff");
            b64594.replace(
                    b64594.length() - "Chars".length(),
                    b64594.length(),
                    "Chars");
            java.util.HashMap<String, Object> map64594 = new java.util.HashMap<String, Object>();
            map64594.put("key64594", b64594.toString());
            String c64594 = (String) map64594.get("key64594");
            String d64594 = c64594.substring(0, c64594.length() - 1);
            String e64594 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d64594.getBytes())));
            String f64594 = e64594.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g64594 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g64594);

            return bar;
        }
    }
}