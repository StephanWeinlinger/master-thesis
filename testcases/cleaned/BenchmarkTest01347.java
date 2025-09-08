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

        String bar = new Test().doSomething(request, param);

        response.setHeader("X-sample-Protection", "0");
        response.getWriter().println(bar);
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a81561 = param;
            StringBuilder b81561 = new StringBuilder(a81561);
            b81561.append(" SafeStuff");
            b81561.replace(
                    b81561.length() - "Chars".length(),
                    b81561.length(),
                    "Chars");
            java.util.HashMap<String, Object> map81561 = new java.util.HashMap<String, Object>();
            map81561.put("key81561", b81561.toString());
            String c81561 = (String) map81561.get("key81561");
            String d81561 = c81561.substring(0, c81561.length() - 1);
            String e81561 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d81561.getBytes())));
            String f81561 = e81561.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String bar = thing.doSomething(f81561);

            return bar;
        }
    }
}