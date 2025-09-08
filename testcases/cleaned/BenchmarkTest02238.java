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

        java.util.Map<String, String[]> map = request.getParameterMap();
        String param = "";
        if (!map.isEmpty()) {
            String[] values = map.get("SampleValue");
            if (values != null) param = values[0];
        }

        String bar = doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().println(bar);
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a16227 = param;
        StringBuilder b16227 = new StringBuilder(a16227);
        b16227.append(" SafeStuff");
        b16227.replace(
                b16227.length() - "Chars".length(),
                b16227.length(),
                "Chars");
        java.util.HashMap<String, Object> map16227 = new java.util.HashMap<String, Object>();
        map16227.put("key16227", b16227.toString());
        String c16227 = (String) map16227.get("key16227");
        String d16227 = c16227.substring(0, c16227.length() - 1);
        String e16227 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d16227.getBytes())));
        String f16227 = e16227.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g16227 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g16227);

        return bar;
    }
}