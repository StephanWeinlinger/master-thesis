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

        String bar = new Test().doSomething(request, param);

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

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a87030 = param;
            StringBuilder b87030 = new StringBuilder(a87030);
            b87030.append(" SafeStuff");
            b87030.replace(
                    b87030.length() - "Chars".length(),
                    b87030.length(),
                    "Chars");
            java.util.HashMap<String, Object> map87030 = new java.util.HashMap<String, Object>();
            map87030.put("key87030", b87030.toString());
            String c87030 = (String) map87030.get("key87030");
            String d87030 = c87030.substring(0, c87030.length() - 1);
            String e87030 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d87030.getBytes())));
            String f87030 = e87030.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g87030 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g87030);

            return bar;
        }
    }
}