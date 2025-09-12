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
        if (request.getHeader("Referer") != null) {
            param = request.getHeader("Referer");
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String bar = new Test().doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write("Parameter value: " + bar);
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a92400 = param;
            StringBuilder b92400 = new StringBuilder(a92400);
            b92400.append(" SafeStuff");
            b92400.replace(
                    b92400.length() - "Chars".length(),
                    b92400.length(),
                    "Chars");
            java.util.HashMap<String, Object> map92400 = new java.util.HashMap<String, Object>();
            map92400.put("key92400", b92400.toString());
            String c92400 = (String) map92400.get("key92400");
            String d92400 = c92400.substring(0, c92400.length() - 1);
            String e92400 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d92400.getBytes())));
            String f92400 = e92400.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String bar = thing.doSomething(f92400);

            return bar;
        }
    }
}