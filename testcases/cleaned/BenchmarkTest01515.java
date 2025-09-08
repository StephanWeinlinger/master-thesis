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

        String bar = new Test().doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write(bar);
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a84124 = param;
            StringBuilder b84124 = new StringBuilder(a84124);
            b84124.append(" SafeStuff");
            b84124.replace(
                    b84124.length() - "Chars".length(),
                    b84124.length(),
                    "Chars");
            java.util.HashMap<String, Object> map84124 = new java.util.HashMap<String, Object>();
            map84124.put("key84124", b84124.toString());
            String c84124 = (String) map84124.get("key84124");
            String d84124 = c84124.substring(0, c84124.length() - 1);
            String e84124 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d84124.getBytes())));
            String f84124 = e84124.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g84124 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g84124);

            return bar;
        }
    }
}