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
        if (request.getHeader("SampleValue") != null) {
            param = request.getHeader("SampleValue");
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String bar = "safe!";
        java.util.HashMap<String, Object> map50591 = new java.util.HashMap<String, Object>();
        map50591.put("keyA-50591", "a_Value");
        map50591.put("keyB-50591", param);
        map50591.put("keyC", "another_Value");
        bar = (String) map50591.get("keyB-50591");
        bar = (String) map50591.get("keyA-50591");

        String cmd =
                org.test.testlib.helpers.Utils.getInsecureOSCommandString(
                        this.getClass().getClassLoader());

        String[] argsEnv = {bar};
        Runtime r = Runtime.getRuntime();

        try {
            Process p = r.exec(cmd, argsEnv);
            org.test.testlib.helpers.Utils.printOSCommandResults(p, response);
        } catch (IOException e) {
            System.out.println("Problem executing sample - TestCase");
            response.getWriter()
                    .println(org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(e.getMessage()));
            return;
        }
    }
}