package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample/sample")
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

        String bar = new Test().doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", "b"};
        response.getWriter().printf(bar, obj);
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a1207 = param;
            StringBuilder b1207 = new StringBuilder(a1207);
            b1207.append(" SafeStuff");
            b1207.replace(
                    b1207.length() - "Chars".length(),
                    b1207.length(),
                    "Chars");
            java.util.HashMap<String, Object> map1207 = new java.util.HashMap<String, Object>();
            map1207.put("key1207", b1207.toString());
            String c1207 = (String) map1207.get("key1207");
            String d1207 = c1207.substring(0, c1207.length() - 1);
            String e1207 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d1207.getBytes())));
            String f1207 = e1207.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g1207 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g1207);

            return bar;
        }
    }
}