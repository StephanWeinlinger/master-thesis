package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-05/SampleValue")
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

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", "b"};
        response.getWriter().printf(bar, obj);
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a28523 = param;
        StringBuilder b28523 = new StringBuilder(a28523);
        b28523.append(" SafeStuff");
        b28523.replace(
                b28523.length() - "Chars".length(),
                b28523.length(),
                "Chars");
        java.util.HashMap<String, Object> map28523 = new java.util.HashMap<String, Object>();
        map28523.put("key28523", b28523.toString());
        String c28523 = (String) map28523.get("key28523");
        String d28523 = c28523.substring(0, c28523.length() - 1);
        String e28523 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d28523.getBytes())));
        String f28523 = e28523.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g28523 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g28523);

        return bar;
    }
}