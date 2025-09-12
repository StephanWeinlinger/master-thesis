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

        String param = "";
        boolean flag = true;
        java.util.Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements() && flag) {
            String name = (String) names.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null) {
                for (int i = 0; i < values.length && flag; i++) {
                    String value = values[i];
                    if (value.equals("SampleValue")) {
                        param = name;
                        flag = false;
                    }
                }
            }
        }

        String a87760 = param;
        StringBuilder b87760 = new StringBuilder(a87760);
        b87760.append(" SafeStuff");
        b87760.replace(
                b87760.length() - "Chars".length(),
                b87760.length(),
                "Chars");
        java.util.HashMap<String, Object> map87760 = new java.util.HashMap<String, Object>();
        map87760.put("key87760", b87760.toString());
        String c87760 = (String) map87760.get("key87760");
        String d87760 = c87760.substring(0, c87760.length() - 1);
        String e87760 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d87760.getBytes())));
        String f87760 = e87760.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g87760 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g87760);

        try {
            javax.crypto.Cipher c = org.test.testlib.helpers.Utils.getCipher();
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
            byte[] result = c.doFinal(input);

            java.io.File fileTarget =
                    new java.io.File(
                            new java.io.File(org.test.testlib.helpers.Utils.TESTFILES_DIR),
                            "passwordFile.txt");
            java.io.FileWriter fw =
                    new java.io.FileWriter(fileTarget, true);
            fw.write(
                    "secret_value="
                            + org.test.samplelib.SAMPLEFUNC.encoder().encodeForBase64(result, true)
                            + "\n");
            fw.close();
            response.getWriter()
                    .println(
                            "Sensitive value: '"
                                    + org.test
                                            .samplelib
                                            .SAMPLEFUNC
                                            .encoder()
                                            .encodeForHTML(new String(input))
                                    + "' encrypted and stored<br/>");

        } catch (javax.crypto.IllegalBlockSizeException e) {
            response.getWriter()
                    .println(
                            "Problem executing sample - javax.crypto.Cipher.getInstance(java.lang.String,java.security.Provider) Test Case");
            e.printStackTrace(response.getWriter());
            throw new ServletException(e);
        } catch (javax.crypto.BadPaddingException e) {
            response.getWriter()
                    .println(
                            "Problem executing sample - javax.crypto.Cipher.getInstance(java.lang.String,java.security.Provider) Test Case");
            e.printStackTrace(response.getWriter());
            throw new ServletException(e);
        }
        response.getWriter()
                .println(
                        "sample Test javax.crypto.Cipher.getInstance(java.lang.String,java.lang.String) executed");
    }
}