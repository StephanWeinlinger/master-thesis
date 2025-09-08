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

        String a49441 = param;
        StringBuilder b49441 = new StringBuilder(a49441);
        b49441.append(" SafeStuff");
        b49441.replace(
                b49441.length() - "Chars".length(),
                b49441.length(),
                "Chars");
        java.util.HashMap<String, Object> map49441 = new java.util.HashMap<String, Object>();
        map49441.put("key49441", b49441.toString());
        String c49441 = (String) map49441.get("key49441");
        String d49441 = c49441.substring(0, c49441.length() - 1);
        String e49441 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d49441.getBytes())));
        String f49441 = e49441.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String bar = thing.doSomething(f49441);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().println(bar);
    }
}