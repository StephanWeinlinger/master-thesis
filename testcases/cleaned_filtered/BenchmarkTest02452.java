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
        String param = scr.getTheParameter("SampleValue");
        if (param == null) param = "";

        String bar = doSomething(request, param);

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
    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a91263 = param;
        StringBuilder b91263 = new StringBuilder(a91263);
        b91263.append(" SafeStuff");
        b91263.replace(
                b91263.length() - "Chars".length(),
                b91263.length(),
                "Chars");
        java.util.HashMap<String, Object> map91263 = new java.util.HashMap<String, Object>();
        map91263.put("key91263", b91263.toString());
        String c91263 = (String) map91263.get("key91263");
        String d91263 = c91263.substring(0, c91263.length() - 1);
        String e91263 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d91263.getBytes())));
        String f91263 = e91263.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g91263 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g91263);

        return bar;
    }
}