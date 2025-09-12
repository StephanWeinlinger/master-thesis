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
        boolean flag = true;
        java.util.Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements() && flag) {
            String name = (String) names.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null) {
                for (int i = 0; i < values.length && flag; i++) {
                    String value = values[i];
                    if (value.equals("SampleValue")) {
                        param = name;
                        flag = false;
                    }
                }
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

            String a75056 = param;
            StringBuilder b75056 = new StringBuilder(a75056);
            b75056.append(" SafeStuff");
            b75056.replace(
                    b75056.length() - "Chars".length(),
                    b75056.length(),
                    "Chars");
            java.util.HashMap<String, Object> map75056 = new java.util.HashMap<String, Object>();
            map75056.put("key75056", b75056.toString());
            String c75056 = (String) map75056.get("key75056");
            String d75056 = c75056.substring(0, c75056.length() - 1);
            String e75056 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d75056.getBytes())));
            String f75056 = e75056.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g75056 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g75056);

            return bar;
        }
    }
}