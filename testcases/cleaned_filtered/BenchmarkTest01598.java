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

        String bar = new Test().doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write(bar);
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a75770 = param;
            StringBuilder b75770 = new StringBuilder(a75770);
            b75770.append(" SafeStuff");
            b75770.replace(
                    b75770.length() - "Chars".length(),
                    b75770.length(),
                    "Chars");
            java.util.HashMap<String, Object> map75770 = new java.util.HashMap<String, Object>();
            map75770.put("key75770", b75770.toString());
            String c75770 = (String) map75770.get("key75770");
            String d75770 = c75770.substring(0, c75770.length() - 1);
            String e75770 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d75770.getBytes())));
            String f75770 = e75770.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String bar = thing.doSomething(f75770);

            return bar;
        }
    }
}