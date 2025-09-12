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
        Object[] obj = {bar, "b"};
        response.getWriter().printf("Formatted like: %1$s and %2$s.", obj);
    }
    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a60610 = param;
        StringBuilder b60610 = new StringBuilder(a60610);
        b60610.append(" SafeStuff");
        b60610.replace(
                b60610.length() - "Chars".length(),
                b60610.length(),
                "Chars");
        java.util.HashMap<String, Object> map60610 = new java.util.HashMap<String, Object>();
        map60610.put("key60610", b60610.toString());
        String c60610 = (String) map60610.get("key60610");
        String d60610 = c60610.substring(0, c60610.length() - 1);
        String e60610 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d60610.getBytes())));
        String f60610 = e60610.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g60610 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g60610);

        return bar;
    }
}