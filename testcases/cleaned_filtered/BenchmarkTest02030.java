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
        java.util.Enumeration<String> headers = request.getHeaders("SampleValue");

        if (headers != null && headers.hasMoreElements()) {
            param = headers.nextElement();
        }

        param = java.net.URLDecoder.decode(param, "UTF-8");

        String bar = doSomething(request, param);

        String startURIslashes = "";
        if (System.getProperty("os.name").indexOf("Windows") != -1)
            if (System.getProperty("os.name").indexOf("Windows") != -1) startURIslashes = "/";
            else startURIslashes = "//";

        try {
            java.net.URI fileURI =
                    new java.net.URI(
                            "file",
                            null,
                            startURIslashes
                                    + org.test.testlib.helpers.Utils.TESTFILES_DIR
                                            .replace('\\', java.io.File.separatorChar)
                                            .replace(' ', '_')
                                    + bar,
                            null,
                            null);
            java.io.File fileTarget = new java.io.File(fileURI);
            response.getWriter()
                    .println(
                            "Access to file: '"
                                    + org.test
                                            .samplelib
                                            .SAMPLEFUNC
                                            .encoder()
                                            .encodeForHTML(fileTarget.toString())
                                    + "' created.");
            if (fileTarget.exists()) {
                response.getWriter().println(" And file already exists.");
            } else {
                response.getWriter().println(" But file doesn't exist yet.");
            }
        } catch (java.net.URISyntaxException e) {
            throw new ServletException(e);
        }
    }

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String a60868 = param;
        StringBuilder b60868 = new StringBuilder(a60868);
        b60868.append(" SafeStuff");
        b60868.replace(
                b60868.length() - "Chars".length(),
                b60868.length(),
                "Chars");
        java.util.HashMap<String, Object> map60868 = new java.util.HashMap<String, Object>();
        map60868.put("key60868", b60868.toString());
        String c60868 = (String) map60868.get("key60868");
        String d60868 = c60868.substring(0, c60868.length() - 1);
        String e60868 =
                new String(
                        org.apache.commons.codec.binary.Base64.decodeBase64(
                                org.apache.commons.codec.binary.Base64.encodeBase64(
                                        d60868.getBytes())));
        String f60868 = e60868.split(" ")[0];
        org.test.testlib.helpers.ThingInterface thing =
                org.test.testlib.helpers.ThingFactory.createThing();
        String g60868 = "barbarians_at_the_gate";
        String bar = thing.doSomething(g60868);

        return bar;
    }
}