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
        java.util.Enumeration<String> headers = request.getHeaders("Referer");

        if (headers != null && headers.hasMoreElements()) {
            param = headers.nextElement();
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String a54571 = param;
        StringBuilder b54571 = new StringBuilder(a54571);
        b54571.append(" SafeStuff");
        b54571.replace(
                b54571.length() - "Chars".length(),
                b54571.length(),
                "Chars");
        java.util.HashMap<String, Object> map54571 = new java.util.HashMap<String, Object>();
        map54571.put("key54571", b54571.toString());
        String c54571 = (String) map54571.get("key54571");
        String d54571 = c54571.substring(0, c54571.length() - 1);
        String e54571 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d54571.getBytes())));
        String f54571 = e54571.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g54571 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g54571);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().print(bar);
    }
}