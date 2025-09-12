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

        String a1504 = param;
        StringBuilder b1504 = new StringBuilder(a1504);
        b1504.append(" SafeStuff");
        b1504.replace(
                b1504.length() - "Chars".length(),
                b1504.length(),
                "Chars");
        java.util.HashMap<String, Object> map1504 = new java.util.HashMap<String, Object>();
        map1504.put("key1504", b1504.toString());
        String c1504 = (String) map1504.get("key1504");
        String d1504 = c1504.substring(0, c1504.length() - 1);
        String e1504 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d1504.getBytes())));
        String f1504 = e1504.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g1504 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g1504);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().print(bar);
    }
}