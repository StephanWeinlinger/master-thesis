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
        String param = scr.getTheValue("SampleValue");

        String bar = new Test().doSomething(request, param);

        java.io.File fileTarget =
                new java.io.File(org.test.testlib.helpers.Utils.TESTFILES_DIR, bar);
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

            String a17402 = param;
            StringBuilder b17402 = new StringBuilder(a17402);
            b17402.append(" SafeStuff");
            b17402.replace(
                    b17402.length() - "Chars".length(),
                    b17402.length(),
                    "Chars");
            java.util.HashMap<String, Object> map17402 = new java.util.HashMap<String, Object>();
            map17402.put("key17402", b17402.toString());
            String c17402 = (String) map17402.get("key17402");
            String d17402 = c17402.substring(0, c17402.length() - 1);
            String e17402 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d17402.getBytes())));
            String f17402 = e17402.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g17402 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g17402);

            return bar;
        }
    }
}