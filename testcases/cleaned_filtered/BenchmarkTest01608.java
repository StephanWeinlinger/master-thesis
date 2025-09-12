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

        String[] values = request.getParameterValues("SampleValue");
        String param;
        if (values != null && values.length > 0) param = values[0];
        else param = "";

        String bar = new Test().doSomething(request, param);

        String cmd =
                org.test.testlib.helpers.Utils.getInsecureOSCommandString(
                        this.getClass().getClassLoader());
        String[] args = {cmd};
        String[] argsEnv = {bar};

        Runtime r = Runtime.getRuntime();

        try {
            Process p = r.exec(args, argsEnv, new java.io.File(System.getProperty("user.dir")));
            org.test.testlib.helpers.Utils.printOSCommandResults(p, response);
        } catch (IOException e) {
            System.out.println("Problem executing sample - TestCase");
            response.getWriter()
                    .println(org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(e.getMessage()));
            return;
        }
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String bar = "safe!";
            java.util.HashMap<String, Object> map55872 = new java.util.HashMap<String, Object>();
            map55872.put("keyA-55872", "a_Value");
            map55872.put("keyB-55872", param);
            map55872.put("keyC", "another_Value");
            bar = (String) map55872.get("keyB-55872");
            bar = (String) map55872.get("keyA-55872");

            return bar;
        }
    }
}