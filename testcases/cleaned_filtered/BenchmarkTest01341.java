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

        java.util.Map<String, String[]> map = request.getParameterMap();
        String param = "";
        if (!map.isEmpty()) {
            String[] values = map.get("SampleValue");
            if (values != null) param = values[0];
        }

        String bar = new Test().doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", bar};
        response.getWriter().printf(java.util.Locale.US, "Formatted like: %1$s and %2$s.", obj);
    } 

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a41908 = param; 
            StringBuilder b41908 = new StringBuilder(a41908); 
            b41908.append(" SafeStuff"); 
            b41908.replace(
                    b41908.length() - "Chars".length(),
                    b41908.length(),
                    "Chars"); 
            java.util.HashMap<String, Object> map41908 = new java.util.HashMap<String, Object>();
            map41908.put("key41908", b41908.toString()); 
            String c41908 = (String) map41908.get("key41908"); 
            String d41908 = c41908.substring(0, c41908.length() - 1); 
            String e41908 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d41908.getBytes()))); 
            String f41908 = e41908.split(" ")[0]; 
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g41908 = "barbarians_at_the_gate"; 
            String bar = thing.doSomething(g41908); 

            return bar;
        }
    } 
} 