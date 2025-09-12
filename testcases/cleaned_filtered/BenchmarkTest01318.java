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

        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] iv = random.generateSeed(8);

        try {
            javax.crypto.Cipher c =
                    javax.crypto.Cipher.getInstance(
                            "DES/CBC/PKCS5PADDING", java.security.Security.getProvider("SunJCE"));

            javax.crypto.SecretKey key = javax.crypto.KeyGenerator.getInstance("DES").generateKey();
            java.security.spec.AlgorithmParameterSpec paramSpec =
                    new javax.crypto.spec.IvParameterSpec(iv);
            c.init(javax.crypto.Cipher.ENCRYPT_MODE, key, paramSpec);

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

        } catch (java.security.NoSuchAlgorithmException e) {
            response.getWriter()
                    .println(
                            "Problem executing sample - javax.crypto.Cipher.getInstance(java.lang.String,java.security.Provider) Test Case");
            e.printStackTrace(response.getWriter());
            throw new ServletException(e);
        } catch (javax.crypto.NoSuchPaddingException e) {
            response.getWriter()
                    .println(
                            "Problem executing sample - javax.crypto.Cipher.getInstance(java.lang.String,java.security.Provider) Test Case");
            e.printStackTrace(response.getWriter());
            throw new ServletException(e);
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
        } catch (java.security.InvalidKeyException e) {
            response.getWriter()
                    .println(
                            "Problem executing sample - javax.crypto.Cipher.getInstance(java.lang.String,java.security.Provider) Test Case");
            e.printStackTrace(response.getWriter());
            throw new ServletException(e);
        } catch (java.security.InvalidAlgorithmParameterException e) {
            response.getWriter()
                    .println(
                            "Problem executing sample - javax.crypto.Cipher.getInstance(java.lang.String,java.security.Provider) Test Case");
            e.printStackTrace(response.getWriter());
            throw new ServletException(e);
        }
        response.getWriter()
                .println(
                        "Crypto Test javax.crypto.Cipher.getInstance(java.lang.String,java.security.Provider) executed");
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a66881 = param;
            StringBuilder b66881 = new StringBuilder(a66881);
            b66881.append(" SafeStuff");
            b66881.replace(
                    b66881.length() - "Chars".length(),
                    b66881.length(),
                    "Chars");
            java.util.HashMap<String, Object> map66881 = new java.util.HashMap<String, Object>();
            map66881.put("key66881", b66881.toString());
            String c66881 = (String) map66881.get("key66881");
            String d66881 = c66881.substring(0, c66881.length() - 1);
            String e66881 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d66881.getBytes())));
            String f66881 = e66881.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g66881 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g66881);

            return bar;
        }
    }
}