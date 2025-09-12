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
        boolean flag = true;
        java.util.Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements() && flag) {
            String name = (String) names.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null) {
                for (int i = 0; i < values.length && flag; i++) {
                    String value = values[i];
                    if (value.equals("SampleValue")) {
                        param = name;
                        flag = false;
                    }
                }
            }
        }

        String a52901 = param;
        StringBuilder b52901 = new StringBuilder(a52901);
        b52901.append(" SafeStuff");
        b52901.replace(
                b52901.length() - "Chars".length(),
                b52901.length(),
                "Chars");
        java.util.HashMap<String, Object> map52901 = new java.util.HashMap<String, Object>();
        map52901.put("key52901", b52901.toString());
        String c52901 = (String) map52901.get("key52901");
        String d52901 = c52901.substring(0, c52901.length() - 1);
        String e52901 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d52901.getBytes())));
        String f52901 = e52901.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g52901 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g52901);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {bar, "b"};
        response.getWriter().printf("Formatted like: %1$s and %2$s.", obj);
    }
}