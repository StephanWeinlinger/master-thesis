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
        response.getWriter().printf(java.util.Locale.US, bar, obj);
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a34270 = param;
        StringBuilder b34270 = new StringBuilder(a34270);
        b34270.append(" SafeStuff");
        b34270.replace(
                b34270.length() - "Chars".length(),
                b34270.length(),
                "Chars");
        java.util.HashMap<String, Object> map34270 = new java.util.HashMap<String, Object>();
        map34270.put("key34270", b34270.toString());
        String c34270 = (String) map34270.get("key34270");
        String d34270 = c34270.substring(0, c34270.length() - 1);
        String e34270 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d34270.getBytes())));
        String f34270 = e34270.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String bar = thing.doSomething(f34270);

        return bar;
    }
}