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
        response.setContentType("text/html;charset=UTF-8");
        javax.servlet.http.Cookie userCookie =
                new javax.servlet.http.Cookie("SampleValue", "bar");
        userCookie.setMaxAge(60 * 3); 
        userCookie.setSecure(true);
        userCookie.setPath(request.getRequestURI());
        userCookie.setDomain(new java.net.URL(request.getRequestURL().toString()).getHost());
        response.addCookie(userCookie);
        javax.servlet.RequestDispatcher rd =
                request.getRequestDispatcher("/sample.html");
        rd.include(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        javax.servlet.http.Cookie[] theCookies = request.getCookies();

        String param = "noCookieValueSupplied";
        if (theCookies != null) {
            for (javax.servlet.http.Cookie theCookie : theCookies) {
                if (theCookie.getName().equals("SampleValue")) {
                    param = java.net.URLDecoder.decode(theCookie.getValue(), "UTF-8");
                    break;
                }
            }
        }

        String a69840 = param; 
        StringBuilder b69840 = new StringBuilder(a69840); 
        b69840.append(" SafeStuff"); 
        b69840.replace(
                b69840.length() - "Chars".length(),
                b69840.length(),
                "Chars"); 
        java.util.HashMap<String, Object> map69840 = new java.util.HashMap<String, Object>();
        map69840.put("key69840", b69840.toString()); 
        String c69840 = (String) map69840.get("key69840"); 
        String d69840 = c69840.substring(0, c69840.length() - 1); 
        String e69840 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d69840.getBytes()))); 
        String f69840 = e69840.split(" ")[0]; 
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g69840 = "barbarians_at_the_gate"; 
        String bar = thing.doSomething(g69840); 

        String sql = "SELECT * from USERS where USERNAME='foo' and PASSWORD='" + bar + "'";

        try {
            java.sql.Statement statement =
                    org.test.testlib.helpers.DatabaseHelper.getSqlStatement();
            statement.execute(sql, new String[] {"username", "password"});
            org.test.testlib.helpers.DatabaseHelper.printResults(statement, sql, response);
        } catch (java.sql.SQLException e) {
            if (org.test.testlib.helpers.DatabaseHelper.hideSQLErrors) {
                response.getWriter().println("Error processing request.");
                return;
            } else throw new ServletException(e);
        }
    }
}