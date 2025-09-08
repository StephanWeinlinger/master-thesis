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
        response.getWriter().write(bar);
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a95930 = param;
        StringBuilder b95930 = new StringBuilder(a95930);
        b95930.append(" SafeStuff");
        b95930.replace(
                b95930.length() - "Chars".length(),
                b95930.length(),
                "Chars");
        java.util.HashMap<String, Object> map95930 = new java.util.HashMap<String, Object>();
        map95930.put("key95930", b95930.toString());
        String c95930 = (String) map95930.get("key95930");
        String d95930 = c95930.substring(0, c95930.length() - 1);
        String e95930 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d95930.getBytes())));
        String f95930 = e95930.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g95930 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g95930);

        return bar;
    }
}