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

        String param = request.getParameter("SampleValue");
        if (param == null) param = "";

        String bar = doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().println(bar);
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a76744 = param;
        StringBuilder b76744 = new StringBuilder(a76744);
        b76744.append(" SafeStuff");
        b76744.replace(
                b76744.length() - "Chars".length(),
                b76744.length(),
                "Chars");
        java.util.HashMap<String, Object> map76744 = new java.util.HashMap<String, Object>();
        map76744.put("key76744", b76744.toString());
        String c76744 = (String) map76744.get("key76744");
        String d76744 = c76744.substring(0, c76744.length() - 1);
        String e76744 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d76744.getBytes())));
        String f76744 = e76744.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String bar = thing.doSomething(f76744);

        return bar;
    }
}