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

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write(bar.toCharArray());
    }
    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a67594 = param;
        StringBuilder b67594 = new StringBuilder(a67594);
        b67594.append(" SafeStuff");
        b67594.replace(
                b67594.length() - "Chars".length(),
                b67594.length(),
                "Chars");
        java.util.HashMap<String, Object> map67594 = new java.util.HashMap<String, Object>();
        map67594.put("key67594", b67594.toString());
        String c67594 = (String) map67594.get("key67594");
        String d67594 = c67594.substring(0, c67594.length() - 1);
        String e67594 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d67594.getBytes())));
        String f67594 = e67594.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g67594 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g67594);

        return bar;
    }
}