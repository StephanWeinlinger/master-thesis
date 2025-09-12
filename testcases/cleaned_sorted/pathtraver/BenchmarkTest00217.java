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
        java.util.Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();

            if (org.test.testlib.helpers.Utils.commonHeaders.contains(name)) {
                continue;
            }

            java.util.Enumeration<String> values = request.getHeaders(name);
            if (values != null && values.hasMoreElements()) {
                param = name;
                break;
            }
        }

        String a26348 = param;
        StringBuilder b26348 = new StringBuilder(a26348);
        b26348.append(" SafeStuff");
        b26348.replace(
                b26348.length() - "Chars".length(),
                b26348.length(),
                "Chars");
        java.util.HashMap<String, Object> map26348 = new java.util.HashMap<String, Object>();
        map26348.put("key26348", b26348.toString());
        String c26348 = (String) map26348.get("key26348");
        String d26348 = c26348.substring(0, c26348.length() - 1);
        String e26348 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d26348.getBytes())));
        String f26348 = e26348.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g26348 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g26348);

        java.io.File fileTarget = new java.io.File(bar, "/Test.txt");
        response.getWriter()
                .println(
                        "Access to file: '"
                                + org.test
                                        .samplelib
                                        .SAMPLEFUNC
                                        .encoder()
                                        .encodeForHTML(fileTarget.toString())
                                + "' created.");
        if (fileTarget.exists()) {
            response.getWriter().println(" And file already exists.");
        } else {
            response.getWriter().println(" But file doesn't exist yet.");
        }
    }
}