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

        String bar = doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write("Parameter value: " + bar);
    }
    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String bar = "safe!";
        java.util.HashMap<String, Object> map52216 = new java.util.HashMap<String, Object>();
        map52216.put("keyA-52216", "a_Value");
        map52216.put("keyB-52216", param);
        map52216.put("keyC", "another_Value");
        bar = (String) map52216.get("keyB-52216");
        bar = (String) map52216.get("keyA-52216");

        return bar;
    }
}