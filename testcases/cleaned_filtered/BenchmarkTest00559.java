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

        String a39502 = param;
        StringBuilder b39502 = new StringBuilder(a39502);
        b39502.append(" SafeStuff");
        b39502.replace(
                b39502.length() - "Chars".length(),
                b39502.length(),
                "Chars");
        java.util.HashMap<String, Object> map39502 = new java.util.HashMap<String, Object>();
        map39502.put("key39502", b39502.toString());
        String c39502 = (String) map39502.get("key39502");
        String d39502 = c39502.substring(0, c39502.length() - 1);
        String e39502 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d39502.getBytes())));
        String f39502 = e39502.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g39502 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g39502);

        java.util.List<String> argList = new java.util.ArrayList<String>();

        String osName = System.getProperty("os.name");
        if (osName.indexOf("Windows") != -1) {
            argList.add("cmd.exe");
            argList.add("/c");
        } else {
            argList.add("sh");
            argList.add("-c");
        }
        argList.add("echo " + bar);

        ProcessBuilder pb = new ProcessBuilder(argList);

        try {
            Process p = pb.start();
            org.test.testlib.helpers.Utils.printOSCommandResults(p, response);
        } catch (IOException e) {
            System.out.println(
                    "Problem executing sample - java.lang.ProcessBuilder(java.util.List) Test Case");
            throw new ServletException(e);
        }
    }
}