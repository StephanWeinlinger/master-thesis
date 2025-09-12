package org.test.testlib.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/sample-00/SampleValue")
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

        String[] values = request.getParameterValues("SampleValue");
        String param;
        if (values != null && values.length > 0) param = values[0];
        else param = "";

        String bar = new Test().doSomething(request, param);

        try {
            java.io.FileInputStream file =
                    new java.io.FileInputStream(
                            org.test.testlib.helpers.Utils.getFileFromClasspath(
                                    "employees.xml", this.getClass().getClassLoader()));
            javax.xml.parsers.DocumentBuilderFactory builderFactory =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            javax.xml.parsers.DocumentBuilder builder = builderFactory.newDocumentBuilder();
            org.w3c.dom.Document xmlDocument = builder.parse(file);
            javax.xml.xpath.XPathFactory xpf = javax.xml.xpath.XPathFactory.newInstance();
            javax.xml.xpath.XPath xp = xpf.newXPath();

            String expression = "/Employees/Employee[@emplid='" + bar + "']";
            String result = xp.evaluate(expression, xmlDocument);

            response.getWriter().println("Your query results are: " + result + "<br/>");

        } catch (javax.xml.xpath.XPathExpressionException
                | javax.xml.parsers.ParserConfigurationException
                | org.xml.sax.SAXException e) {
            response.getWriter()
                    .println(
                            "Error parsing XPath input: '"
                                    + org.test.samplelib.SAMPLEFUNC.encoder().encodeForHTML(bar)
                                    + "'");
            throw new ServletException(e);
        }
    }

    private class Test {

        public String doSomething(HttpServletRequest request, String param)
                throws ServletException, IOException {

            String a31144 = param;
            StringBuilder b31144 = new StringBuilder(a31144);
            b31144.append(" SafeStuff");
            b31144.replace(
                    b31144.length() - "Chars".length(),
                    b31144.length(),
                    "Chars");
            java.util.HashMap<String, Object> map31144 = new java.util.HashMap<String, Object>();
            map31144.put("key31144", b31144.toString());
            String c31144 = (String) map31144.get("key31144");
            String d31144 = c31144.substring(0, c31144.length() - 1);
            String e31144 =
                    new String(
                            org.apache.commons.codec.binary.Base64.decodeBase64(
                                    org.apache.commons.codec.binary.Base64.encodeBase64(
                                            d31144.getBytes())));
            String f31144 = e31144.split(" ")[0];
            org.test.testlib.helpers.ThingInterface thing =
                    org.test.testlib.helpers.ThingFactory.createThing();
            String g31144 = "barbarians_at_the_gate";
            String bar = thing.doSomething(g31144);

            return bar;
        }
    }
}