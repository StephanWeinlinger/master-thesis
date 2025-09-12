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

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write(bar);
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String bar = "safe!";
            java.util.HashMap<String, Object> map58318 = new java.util.HashMap<String, Object>();
            map58318.put("keyA-58318", "a_Value");
            map58318.put("keyB-58318", param);
            map58318.put("keyC", "another_Value");
            bar = (String) map58318.get("keyB-58318");
            bar = (String) map58318.get("keyA-58318");

            return bar;
        }
    }
}