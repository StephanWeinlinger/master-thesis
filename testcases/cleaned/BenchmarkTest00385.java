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

        String param = request.getParameter("SampleValue");
        if (param == null) param = "";

        String a21475 = param;
        StringBuilder b21475 = new StringBuilder(a21475);
        b21475.append(" SafeStuff");
        b21475.replace(
                b21475.length() - "Chars".length(),
                b21475.length(),
                "Chars");
        java.util.HashMap<String, Object> map21475 = new java.util.HashMap<String, Object>();
        map21475.put("key21475", b21475.toString());
        String c21475 = (String) map21475.get("key21475");
        String d21475 = c21475.substring(0, c21475.length() - 1);
        String e21475 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d21475.getBytes())));
        String f21475 = e21475.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String bar = thing.doSomething(f21475);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {bar, "b"};
        response.getWriter().printf("Formatted like: %1$s and %2$s.", obj);
    }
}