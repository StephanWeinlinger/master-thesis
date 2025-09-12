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

        String bar = doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().print(bar);
    }
    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a47309 = param;
        StringBuilder b47309 = new StringBuilder(a47309);
        b47309.append(" SafeStuff");
        b47309.replace(
                b47309.length() - "Chars".length(),
                b47309.length(),
                "Chars");
        java.util.HashMap<String, Object> map47309 = new java.util.HashMap<String, Object>();
        map47309.put("key47309", b47309.toString());
        String c47309 = (String) map47309.get("key47309");
        String d47309 = c47309.substring(0, c47309.length() - 1);
        String e47309 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d47309.getBytes())));
        String f47309 = e47309.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g47309 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g47309);

        return bar;
    }
}