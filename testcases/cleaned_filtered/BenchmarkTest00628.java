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

        org.test.testlib.helpers.SeparateClassRequest scr =
                new org.test.testlib.helpers.SeparateClassRequest(request);
        String param = scr.getTheParameter("SampleValue");
        if (param == null) param = "";

        String a24315 = param;
        StringBuilder b24315 = new StringBuilder(a24315);
        b24315.append(" SafeStuff");
        b24315.replace(
                b24315.length() - "Chars".length(),
                b24315.length(),
                "Chars");
        java.util.HashMap<String, Object> map24315 = new java.util.HashMap<String, Object>();
        map24315.put("key24315", b24315.toString());
        String c24315 = (String) map24315.get("key24315");
        String d24315 = c24315.substring(0, c24315.length() - 1);
        String e24315 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d24315.getBytes())));
        String f24315 = e24315.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g24315 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g24315);

        String fileName = null;
        java.io.FileOutputStream fos = null;

        try {
            fileName = org.test.testlib.helpers.Utils.TESTFILES_DIR + bar;

            fos = new java.io.FileOutputStream(fileName);
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
}