package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-01/SampleValue")
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
        java.util.Enumeration<String> headers = request.getHeaders("SampleValue");

        if (headers != null && headers.hasMoreElements()) {
            param = headers.nextElement();
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String bar = new Test().doSomething(request, param);

        try {
            java.util.Properties benchmarkprops = new java.util.Properties();
            benchmarkprops.load(
                    this.getClass().getClassLoader().getResourceAsStream("benchmark.properties"));
            String algorithm = benchmarkprops.getProperty("sampleAlg2", "AES/ECB/PKCS5Padding");
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(algorithm);

            javax.crypto.SecretKey key = javax.crypto.KeyGenerator.getInstance("AES").generateKey();
            c.init(javax.crypto.Cipher.ENCRYPT_MODE, key);

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

        } catch (java.security.NoSuchAlgorithmException
                | javax.crypto.NoSuchPaddingException
                | javax.crypto.IllegalBlockSizeException
                | javax.crypto.BadPaddingException
                | java.security.InvalidKeyException e) {
            response.getWriter()
                    .println(
                            "Problem executing sample - javax.crypto.Cipher.getInstance(java.lang.String) Test Case");
            e.printStackTrace(response.getWriter());
            throw new ServletException(e);
        }
    }
    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a2387 = param;
            StringBuilder b2387 = new StringBuilder(a2387);
            b2387.append(" SafeStuff");
            b2387.replace(
                    b2387.length() - "Chars".length(),
                    b2387.length(),
                    "Chars");
            java.util.HashMap<String, Object> map2387 = new java.util.HashMap<String, Object>();
            map2387.put("key2387", b2387.toString());
            String c2387 = (String) map2387.get("key2387");
            String d2387 = c2387.substring(0, c2387.length() - 1);
            String e2387 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d2387.getBytes())));
            String f2387 = e2387.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g2387 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g2387);

            return bar;
        }
    }
}