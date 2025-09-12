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

        String bar = doSomething(request, param);

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

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a90580 = param;
        StringBuilder b90580 = new StringBuilder(a90580);
        b90580.append(" SafeStuff");
        b90580.replace(
                b90580.length() - "Chars".length(),
                b90580.length(),
                "Chars");
        java.util.HashMap<String, Object> map90580 = new java.util.HashMap<String, Object>();
        map90580.put("key90580", b90580.toString());
        String c90580 = (String) map90580.get("key90580");
        String d90580 = c90580.substring(0, c90580.length() - 1);
        String e90580 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d90580.getBytes())));
        String f90580 = e90580.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g90580 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g90580);

        return bar;
    }
}