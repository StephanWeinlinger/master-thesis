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

        String a59129 = param;
        StringBuilder b59129 = new StringBuilder(a59129);
        b59129.append(" SafeStuff");
        b59129.replace(
                b59129.length() - "Chars".length(),
                b59129.length(),
                "Chars");
        java.util.HashMap<String, Object> map59129 = new java.util.HashMap<String, Object>();
        map59129.put("key59129", b59129.toString());
        String c59129 = (String) map59129.get("key59129");
        String d59129 = c59129.substring(0, c59129.length() - 1);
        String e59129 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d59129.getBytes())));
        String f59129 = e59129.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g59129 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g59129);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", bar};
        response.getWriter().printf(java.util.Locale.US, "Formatted like: %1$s and %2$s.", obj);
    }
}