package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-00/SampleValue")
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
        if (request.getHeader("SampleValue") != null) {
            param = request.getHeader("SampleValue");
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String a14330 = param;
        StringBuilder b14330 = new StringBuilder(a14330);
        b14330.append(" SafeStuff");
        b14330.replace(
                b14330.length() - "Chars".length(),
                b14330.length(),
                "Chars");
        java.util.HashMap<String, Object> map14330 = new java.util.HashMap<String, Object>();
        map14330.put("key14330", b14330.toString());
        String c14330 = (String) map14330.get("key14330");
        String d14330 = c14330.substring(0, c14330.length() - 1);
        String e14330 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d14330.getBytes())));
        String f14330 = e14330.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g14330 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g14330);

        request.getSession().setAttribute("userid", bar);

        response.getWriter()
                .println(
                        "Item: 'userid' with value: '"
                                + org.test.testlib.helpers.Utils.encodeForHTML(bar)
                                + "' saved in session.");
    }
}