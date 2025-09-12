package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-02/SampleValue")
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

        String bar = doSomething(request, param);

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

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String bar;
        String guess = "ABC";
        char switchTarget = guess.charAt(2);

        switch (switchTarget) {
            case 'A':
                bar = param;
                break;
            case 'B':
                bar = "bobs_your_uncle";
                break;
            case 'C':
            case 'D':
                bar = param;
                break;
            default:
                bar = "bobs_your_uncle";
                break;
        }

        return bar;
    }
}