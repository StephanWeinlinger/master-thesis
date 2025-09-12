package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample/SampleValue")
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

        String sql = "SELECT userid from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";
        try {
            Long results =
                    org.test.testlib.helpers.DatabaseHelper.JDBCtemplate.queryForObject(
                            sql, Long.class);
            response.getWriter().println("Your results are: " + String.valueOf(results));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            response.getWriter()
                    .println(
                            "No results returned for query: "
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(sql));
        } catch (org.springframework.dao.DataAccessException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
            } else throw new ServletException(e);
        }
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String bar;

        int num = 196;
        if ((500 / 42) + num > 200) bar = param;
        else bar = "This should never happen";

        return bar;
    }
}