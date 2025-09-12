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

        String bar = new Test().doSomething(request, param);

        String a1 = "";
        String a2 = "";
        String osName = System.getProperty("os.name");
        if (osName.indexOf("Windows") != -1) {
            a1 = "cmd.exe";
            a2 = "/c";
        } else {
            a1 = "sh";
            a2 = "-c";
        }
        String[] args = {a1, a2, "echo " + bar};

        ProcessBuilder pb = new ProcessBuilder();

        pb.command(args);

        try {
            Process p = pb.start();
            org.test.testlib.helpers.Utils.printOSCommandResults(p, response);
        } catch (IOException e) {
            System.out.println(
                    "Problem executing sample - java.lang.ProcessBuilder(java.util.List) Test Case");
            throw new ServletException(e);
        }
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a84214 = param;
            StringBuilder b84214 = new StringBuilder(a84214);
            b84214.append(" SafeStuff");
            b84214.replace(
                    b84214.length() - "Chars".length(),
                    b84214.length(),
                    "Chars");
            java.util.HashMap<String, Object> map84214 = new java.util.HashMap<String, Object>();
            map84214.put("key84214", b84214.toString());
            String c84214 = (String) map84214.get("key84214");
            String d84214 = c84214.substring(0, c84214.length() - 1);
            String e84214 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d84214.getBytes())));
            String f84214 = e84214.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g84214 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g84214);

            return bar;
        }
    }
}