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

        org.test.testlib.helpers.SeparateClassRequest scr =
                new org.test.testlib.helpers.SeparateClassRequest(request);
        String param = scr.getTheParameter("SampleValue");
        if (param == null) param = "";

        String a87833 = param;
        StringBuilder b87833 = new StringBuilder(a87833);
        b87833.append(" SafeStuff");
        b87833.replace(
                b87833.length() - "Chars".length(),
                b87833.length(),
                "Chars");
        java.util.HashMap<String, Object> map87833 = new java.util.HashMap<String, Object>();
        map87833.put("key87833", b87833.toString());
        String c87833 = (String) map87833.get("key87833");
        String d87833 = c87833.substring(0, c87833.length() - 1);
        String e87833 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d87833.getBytes())));
        String f87833 = e87833.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g87833 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g87833);

        try {
            float rand = java.security.SecureRandom.getInstance("SHA1PRNG").nextFloat();
            String rememberMeKey =
                    Float.toString(rand).substring(2);

            String user = "SafeFloyd";
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
                        if (cookie.getValue()
                                .equals(request.getSession().getAttribute(cookieName))) {
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
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("Problem executing SecureRandom.nextFloat() - TestCase");
            throw new ServletException(e);
        }
        response.getWriter()
                .println("sample Test java.security.SecureRandom.nextFloat() executed");
    }
}