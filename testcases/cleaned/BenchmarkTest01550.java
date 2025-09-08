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

        String bar = new Test().doSomething(request, param);

        request.getSession().setAttribute("userid", bar);

        response.getWriter()
                .println(
                        "Item: 'userid' with value: '"
                                + org.test.testlib.helpers.Utils.encodeForHTML(bar)
                                + "' saved in session.");
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a86153 = param;
            StringBuilder b86153 = new StringBuilder(a86153);
            b86153.append(" SafeStuff");
            b86153.replace(
                    b86153.length() - "Chars".length(),
                    b86153.length(),
                    "Chars");
            java.util.HashMap<String, Object> map86153 = new java.util.HashMap<String, Object>();
            map86153.put("key86153", b86153.toString());
            String c86153 = (String) map86153.get("key86153");
            String d86153 = c86153.substring(0, c86153.length() - 1);
            String e86153 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d86153.getBytes())));
            String f86153 = e86153.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String bar = thing.doSomething(f86153);

            return bar;
        }
    }
}