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

        String a62588 = param;
        StringBuilder b62588 = new StringBuilder(a62588);
        b62588.append(" SafeStuff");
        b62588.replace(
                b62588.length() - "Chars".length(),
                b62588.length(),
                "Chars");
        java.util.HashMap<String, Object> map62588 = new java.util.HashMap<String, Object>();
        map62588.put("key62588", b62588.toString());
        String c62588 = (String) map62588.get("key62588");
        String d62588 = c62588.substring(0, c62588.length() - 1);
        String e62588 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d62588.getBytes())));
        String f62588 = e62588.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g62588 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g62588);

        String fileName = org.test.testlib.helpers.Utils.TESTFILES_DIR + bar;
        java.io.InputStream is = null;

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileName);
            is = java.nio.file.Files.newInputStream(path, java.nio.file.StandardOpenOption.READ);
            byte[] b = new byte[1000];
            int size = is.read(b);
            response.getWriter()
                    .println(
                            "The beginning of file: '"
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(fileName)
                                    + "' is:\n\n");
            response.getWriter()
                    .println(org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(new String(b, 0, size)));
            is.close();
        } catch (Exception e) {
            System.out.println("Couldn't open InputStream on file: '" + fileName + "'");
            response.getWriter()
                    .println(
                            "Problem getting InputStream: "
                                    + org.test
                                            .samplelib
                                            .SAMPLEFUNC
                                            .encoder()
                                            .encodeForHTML(e.getMessage()));
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (Exception e) {
                    
                }
            }
        }
    }
}