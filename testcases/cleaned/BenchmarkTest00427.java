package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-00/SampleValue")
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

        String a70670 = param;
        StringBuilder b70670 = new StringBuilder(a70670);
        b70670.append(" SafeStuff");
        b70670.replace(
                b70670.length() - "Chars".length(),
                b70670.length(),
                "Chars");
        java.util.HashMap<String, Object> map70670 = new java.util.HashMap<String, Object>();
        map70670.put("key70670", b70670.toString());
        String c70670 = (String) map70670.get("key70670");
        String d70670 = c70670.substring(0, c70670.length() - 1);
        String e70670 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d70670.getBytes())));
        String f70670 = e70670.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String bar = thing.doSomething(f70670);

        request.getSession().setAttribute("userid", bar);

        response.getWriter()
                .println(
                        "Item: 'userid' with value: '"
                                + org.test.testlib.helpers.Utils.encodeForHTML(bar)
                                + "' saved in session.");
    }
}