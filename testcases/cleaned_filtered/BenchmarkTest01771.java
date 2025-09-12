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
        String param = scr.getTheValue("SampleValue");

        String bar = new Test().doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().println(bar);
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a58724 = param;
            StringBuilder b58724 = new StringBuilder(a58724);
            b58724.append(" SafeStuff");
            b58724.replace(
                    b58724.length() - "Chars".length(),
                    b58724.length(),
                    "Chars");
            java.util.HashMap<String, Object> map58724 = new java.util.HashMap<String, Object>();
            map58724.put("key58724", b58724.toString());
            String c58724 = (String) map58724.get("key58724");
            String d58724 = c58724.substring(0, c58724.length() - 1);
            String e58724 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d58724.getBytes())));
            String f58724 = e58724.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String bar = thing.doSomething(f58724);

            return bar;
        }
    }
}