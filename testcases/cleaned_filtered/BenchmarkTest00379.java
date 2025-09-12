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

        String a75704 = param;
        StringBuilder b75704 = new StringBuilder(a75704);
        b75704.append(" SafeStuff");
        b75704.replace(
                b75704.length() - "Chars".length(),
                b75704.length(),
                "Chars");
        java.util.HashMap<String, Object> map75704 = new java.util.HashMap<String, Object>();
        map75704.put("key75704", b75704.toString());
        String c75704 = (String) map75704.get("key75704");
        String d75704 = c75704.substring(0, c75704.length() - 1);
        String e75704 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d75704.getBytes())));
        String f75704 = e75704.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g75704 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g75704);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().print(bar.toCharArray());
    }
}