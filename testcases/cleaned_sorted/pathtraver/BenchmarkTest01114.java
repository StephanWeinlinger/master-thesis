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
        java.util.Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();

            if (org.test.testlib.helpers.Utils.commonHeaders.contains(name)) {
                continue;
            }

            java.util.Enumeration<String> values = request.getHeaders(name);
            if (values != null && values.hasMoreElements()) {
                param = name;
                break;
            }
        }

        String bar = new Test().doSomething(request, param);

        String fileName = null;
        java.io.FileOutputStream fos = null;

        try {
            fileName = org.test.testlib.helpers.Utils.TESTFILES_DIR + bar;

            fos = new java.io.FileOutputStream(new java.io.File(fileName), false);
            response.getWriter()
                    .println(
                            "Now ready to write to file: "
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(fileName));

        } catch (Exception e) {
            System.out.println("Couldn't open FileOutputStream on file: '" + fileName + "'");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                }
            }
        }
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a97099 = param;
            StringBuilder b97099 = new StringBuilder(a97099);
            b97099.append(" SafeStuff");
            b97099.replace(
                    b97099.length() - "Chars".length(),
                    b97099.length(),
                    "Chars");
            java.util.HashMap<String, Object> map97099 = new java.util.HashMap<String, Object>();
            map97099.put("key97099", b97099.toString());
            String c97099 = (String) map97099.get("key97099");
            String d97099 = c97099.substring(0, c97099.length() - 1);
            String e97099 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d97099.getBytes())));
            String f97099 = e97099.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g97099 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g97099);

            return bar;
        }
    }
}