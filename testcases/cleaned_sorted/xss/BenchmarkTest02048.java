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
        java.util.Enumeration<String> headers = request.getHeaders("Referer");

        if (headers != null && headers.hasMoreElements()) {
            param = headers.nextElement();
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String bar = doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", bar};
        response.getWriter().printf(java.util.Locale.US, "Formatted like: %1$s and %2$s.", obj);
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a96053 = param;
        StringBuilder b96053 = new StringBuilder(a96053);
        b96053.append(" SafeStuff");
        b96053.replace(
                b96053.length() - "Chars".length(),
                b96053.length(),
                "Chars");
        java.util.HashMap<String, Object> map96053 = new java.util.HashMap<String, Object>();
        map96053.put("key96053", b96053.toString());
        String c96053 = (String) map96053.get("key96053");
        String d96053 = c96053.substring(0, c96053.length() - 1);
        String e96053 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d96053.getBytes())));
        String f96053 = e96053.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g96053 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g96053);

        return bar;
    }
}