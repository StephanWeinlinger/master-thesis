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

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().println(bar.toCharArray());
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a17852 = param;
        StringBuilder b17852 = new StringBuilder(a17852);
        b17852.append(" SafeStuff");
        b17852.replace(
                b17852.length() - "Chars".length(),
                b17852.length(),
                "Chars");
        java.util.HashMap<String, Object> map17852 = new java.util.HashMap<String, Object>();
        map17852.put("key17852", b17852.toString());
        String c17852 = (String) map17852.get("key17852");
        String d17852 = c17852.substring(0, c17852.length() - 1);
        String e17852 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d17852.getBytes())));
        String f17852 = e17852.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g17852 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g17852);

        return bar;
    }
}