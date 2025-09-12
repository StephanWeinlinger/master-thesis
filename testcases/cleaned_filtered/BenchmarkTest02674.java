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
        String param = scr.getTheValue("SampleValue");

        String bar = doSomething(request, param);

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] input = {(byte) '?'};
            Object inputParam = bar;
            if (inputParam instanceof String) input = ((String) inputParam).getBytes();
            if (inputParam instanceof java.io.InputStream) {
                byte[] strInput = new byte[1000];
                int i = ((java.io.InputStream) inputParam).read(strInput);
                if (i == -1) {
                    response.getWriter()
                            .println(
                                    "This input source requires a POST, not a GET. Incompatible UI for the InputStream source.");
                    return;
                }
                input = java.util.Arrays.copyOf(strInput, i);
            }
            md.update(input);

            byte[] result = md.digest();
            java.io.File fileTarget =
                    new java.io.File(
                            new java.io.File(org.test.testlib.helpers.Utils.TESTFILES_DIR),
                            "passwordFile.txt");
            java.io.FileWriter fw =
                    new java.io.FileWriter(fileTarget, true);
            fw.write(
                    "sample_value="
                            + org.test.samplelib.SAMPLEFUNC.encoder().encodeForBase64(result, true)
                            + "\n");
            fw.close();
            response.getWriter()
                    .println(
                            "Sensitive value '"
                                    + org.test
                                            .samplelib
                                            .SAMPLEFUNC
                                            .encoder()
                                            .encodeForHTML(new String(input))
                                    + "' hashed and stored<br/>");

        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("Problem executing sample - TestCase");
            throw new ServletException(e);
        }

        response.getWriter()
                .println(
                        "Sample Test java.security.MessageDigest.getInstance(java.lang.String) executed");
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a73069 = param;
        StringBuilder b73069 = new StringBuilder(a73069);
        b73069.append(" SafeStuff");
        b73069.replace(
                b73069.length() - "Chars".length(),
                b73069.length(),
                "Chars");
        java.util.HashMap<String, Object> map73069 = new java.util.HashMap<String, Object>();
        map73069.put("key73069", b73069.toString());
        String c73069 = (String) map73069.get("key73069");
        String d73069 = c73069.substring(0, c73069.length() - 1);
        String e73069 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d73069.getBytes())));
        String f73069 = e73069.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g73069 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g73069);

        return bar;
    }
}