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

        String bar = new Test().doSomething(request, param);

        String fileName = null;
        java.io.FileInputStream fis = null;

        try {
            fileName = org.test.testlib.helpers.Utils.TESTFILES_DIR + bar;
            fis = new java.io.FileInputStream(fileName);
            byte[] b = new byte[1000];
            int size = fis.read(b);
            response.getWriter()
                    .println(
                            "The beginning of file: '"
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(fileName)
                                    + "' is:\n\n");
            response.getWriter()
                    .println(org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(new String(b, 0, size)));
        } catch (Exception e) {
            System.out.println("Couldn't open FileInputStream on file: '" + fileName + "'");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (Exception e) {
                }
            }
        }
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String bar;
            String guess = "ABC";
            char switchTarget = guess.charAt(1);

            switch (switchTarget) {
                case 'A':
                    bar = param;
                    break;
                case 'B':
                    bar = "bob";
                    break;
                case 'C':
                case 'D':
                    bar = param;
                    break;
                default:
                    bar = "bob's your uncle";
                    break;
            }

            return bar;
        }
    }
}