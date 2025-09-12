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

        String bar = doSomething(request, param);

        java.io.File fileTarget =
                new java.io.File(
                        new java.io.File(org.test.testlib.helpers.Utils.TESTFILES_DIR), bar);
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

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a20919 = param; 
        StringBuilder b20919 = new StringBuilder(a20919); 
        b20919.append(" SafeStuff"); 
        b20919.replace(
                b20919.length() - "Chars".length(),
                b20919.length(),
                "Chars"); 
        java.util.HashMap<String, Object> map20919 = new java.util.HashMap<String, Object>();
        map20919.put("key20919", b20919.toString()); 
        String c20919 = (String) map20919.get("key20919"); 
        String d20919 = c20919.substring(0, c20919.length() - 1); 
        String e20919 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d20919.getBytes()))); 
        String f20919 = e20919.split(" ")[0]; 
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g20919 = "barbarians_at_the_gate"; 
        String bar = thing.doSomething(g20919); 

        return bar;
    }
}