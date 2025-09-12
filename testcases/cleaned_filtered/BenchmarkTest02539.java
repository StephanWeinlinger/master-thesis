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

        String[] values = request.getParameterValues("SampleValue");
        String param;
        if (values != null && values.length > 0) param = values[0];
        else param = "";

        String bar = doSomething(request, param);

        String sql = "SELECT  * from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";
        try {
            org.springframework.jdbc.support.rowset.SqlRowSet results =
                    org.test.testlib.helpers.DatabaseHelper.JDBCtemplate.queryForRowSet(sql);
            response.getWriter().println("Your results are: ");

            while (results.next()) {
                response.getWriter()
                        .println(
                                org.test.samplelib.SAMPLEFUNC
                                                .encoder()
                                                .encodeForHTML(results.getString("USERNAME"))
                                        + " ");
            }
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

        String a9290 = param;
        StringBuilder b9290 = new StringBuilder(a9290);
        b9290.append(" SafeStuff");
        b9290.replace(
                b9290.length() - "Chars".length(),
                b9290.length(),
                "Chars");
        java.util.HashMap<String, Object> map9290 = new java.util.HashMap<String, Object>();
        map9290.put("key9290", b9290.toString());
        String c9290 = (String) map9290.get("key9290");
        String d9290 = c9290.substring(0, c9290.length() - 1);
        String e9290 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d9290.getBytes())));
        String f9290 = e9290.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g9290 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g9290);

        return bar;
    }
}