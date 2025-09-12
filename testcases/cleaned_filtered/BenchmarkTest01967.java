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

        String bar = doSomething(request, param);

        String sql =
                "SELECT TOP 1 USERNAME from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";
        try {
            Object results =
                    org.test.testlib.helpers.DatabaseHelper.JDBCtemplate.queryForObject(
                            sql, new Object[] {}, String.class);
            response.getWriter().println("Your results are: ");

            response.getWriter()
                    .println(org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(results.toString()));
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
        String guess = "ABC";
        char switchTarget = guess.charAt(1);

        switch (switchTarget) {
            case 'A':
                bar = param;
                break;
            case 'B':
                bar = "bob";
                break;
            case 'C':
            case 'D':
                bar = param;
                break;
            default:
                bar = "bob's your uncle";
                break;
        }

        return bar;
    }
}