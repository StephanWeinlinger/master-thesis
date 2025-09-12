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

        org.test.testlib.helpers.SeparateClassRequest scr =
                new org.test.testlib.helpers.SeparateClassRequest(request);
        String param = scr.getTheParameter("SampleValue");
        if (param == null) param = "";

        String a17321 = param;
        StringBuilder b17321 = new StringBuilder(a17321);
        b17321.append(" SafeStuff");
        b17321.replace(
                b17321.length() - "Chars".length(),
                b17321.length(),
                "Chars");
        java.util.HashMap<String, Object> map17321 = new java.util.HashMap<String, Object>();
        map17321.put("key17321", b17321.toString());
        String c17321 = (String) map17321.get("key17321");
        String d17321 = c17321.substring(0, c17321.length() - 1);
        String e17321 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d17321.getBytes())));
        String f17321 = e17321.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g17321 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g17321);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write(bar.toCharArray());
    }
}