package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-01/sample")
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

        String bar = "safe!";
        java.util.HashMap<String, Object> map61765 = new java.util.HashMap<String, Object>();
        map61765.put("keyA-61765", "a-Value");
        map61765.put("keyB-61765", param);
        map61765.put("keyC", "another-Value");
        bar = (String) map61765.get("keyB-61765");

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write(bar.toCharArray());
    }
}