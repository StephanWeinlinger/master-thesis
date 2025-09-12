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

        String bar = doSomething(request, param);

        double value = new java.util.Random().nextDouble();
        String rememberMeKey = Double.toString(value).substring(2); 

        String user = "Donna";
        String fullClassName = this.getClass().getName();
        String testCaseNumber =
                fullClassName.substring(
                        fullClassName.lastIndexOf('.') + 1 + "SampleValue".length());
        user += testCaseNumber;

        String cookieName = "rememberMe" + testCaseNumber;

        boolean foundUser = false;
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; !foundUser && i < cookies.length; i++) {
                javax.servlet.http.Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName())) {
                    if (cookie.getValue().equals(request.getSession().getAttribute(cookieName))) {
                        foundUser = true;
                    }
                }
            }
        }

        if (foundUser) {
            response.getWriter().println("Welcome back: " + user + "<br/>");
        } else {
            javax.servlet.http.Cookie rememberMe =
                    new javax.servlet.http.Cookie(cookieName, rememberMeKey);
            rememberMe.setSecure(true);
            rememberMe.setHttpOnly(true);
            rememberMe.setDomain(new java.net.URL(request.getRequestURL().toString()).getHost());
            rememberMe.setPath(request.getRequestURI()); 
            request.getSession().setAttribute(cookieName, rememberMeKey);
            response.addCookie(rememberMe);
            response.getWriter()
                    .println(
                            user
                                    + " has been remembered with cookie: "
                                    + rememberMe.getName()
                                    + " whose value is: "
                                    + rememberMe.getValue()
                                    + "<br/>");
        }

        response.getWriter().println("Weak Randomness Test java.util.Random.nextDouble() executed");
    } 

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a40533 = param; 
        StringBuilder b40533 = new StringBuilder(a40533); 
        b40533.append(" SafeStuff"); 
        b40533.replace(
                b40533.length() - "Chars".length(),
                b40533.length(),
                "Chars"); 
        java.util.HashMap<String, Object> map40533 = new java.util.HashMap<String, Object>();
        map40533.put("key40533", b40533.toString()); 
        String c40533 = (String) map40533.get("key40533"); 
        String d40533 = c40533.substring(0, c40533.length() - 1); 
        String e40533 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d40533.getBytes()))); 
        String f40533 = e40533.split(" ")[0]; 
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g40533 = "barbarians_at_the_gate"; 
        String bar = thing.doSomething(g40533); 

        return bar;
    }
}