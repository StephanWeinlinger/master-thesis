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
        Object[] obj = {"a", "b"};
        response.getWriter().format(bar, obj);
    }
    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a53479 = param;
        StringBuilder b53479 = new StringBuilder(a53479);
        b53479.append(" SafeStuff");
        b53479.replace(
                b53479.length() - "Chars".length(),
                b53479.length(),
                "Chars");
        java.util.HashMap<String, Object> map53479 = new java.util.HashMap<String, Object>();
        map53479.put("key53479", b53479.toString());
        String c53479 = (String) map53479.get("key53479");
        String d53479 = c53479.substring(0, c53479.length() - 1);
        String e53479 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d53479.getBytes())));
        String f53479 = e53479.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String bar = thing.doSomething(f53479);

        return bar;
    }
}