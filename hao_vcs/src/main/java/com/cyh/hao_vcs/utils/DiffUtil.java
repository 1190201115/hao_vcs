//package com.cyh.hao_vcs.utils;
//
//import org.outerj.daisy.diff.DaisyDiff;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.sax.SAXTransformerFactory;
//import javax.xml.transform.sax.TransformerHandler;
//import javax.xml.transform.stream.StreamResult;
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.util.Locale;
//
//public class DiffUtil {
//
//    public static void daisyDiffTest(){
//        String html1 = "<html><body>var v2</body></html>";
//        String html2 = "<html><body>Hello world</body></html>";
//
//        try {
//            StringWriter finalResult = new StringWriter();
//            SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
//            TransformerHandler result = tf.newTransformerHandler();
//            result.getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//            result.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
//            result.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
//            result.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            result.setResult(new StreamResult(finalResult));
//
//            ContentHandler postProcess = result;
//            DaisyDiff.diffHTML(new InputSource(new StringReader(html1)),
//                    new InputSource(new StringReader(html2)), postProcess, "test", Locale.ENGLISH);
//            System.out.println(finalResult);
//        } catch (SAXException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        daisyDiffTest();
//    }
//}
