package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-01/SampleValue")
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

        String bar = "safe!";
        java.util.HashMap<String, Object> map29173 = new java.util.HashMap<String, Object>();
        map29173.put("keyA-29173", "a_Value");
        map29173.put("keyB-29173", param);
        map29173.put("keyC", "another_Value");
        bar = (String) map29173.get("keyB-29173");
        bar = (String) map29173.get("keyA-29173");

        response.setHeader("X-XSS-Protection", "0");
        response.getWriter().write(bar.toCharArray());
    }
}