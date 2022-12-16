package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fit.pdfdom.PDFDomTree;
import org.fit.pdfdom.PDFDomTreeConfig;
import org.fit.pdfdom.resource.HtmlResourceHandler;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Converter {

    public static Integer textClassifier(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return FileConfig.UNKNOWN_FILE;
        }
        String suffix = fileName.substring(fileName.indexOf(".") + 1);
        if (Objects.equals(suffix, "docx")) {
            return FileConfig.DOCX_FILE;
        }
        if (Objects.equals(suffix, "doc")) {
            return FileConfig.DOC_FILE;
        }
        if (Objects.equals(suffix, "txt")) {
            return FileConfig.TXT_FILE;
        }
        if (Objects.equals(suffix, "pdf")) {
            return FileConfig.PDF_FILE;
        }
        return FileConfig.UNKNOWN_FILE;
    }

    public static boolean fileExists(String fileName, Integer kind) {
        String path = FileConfig.pathMap.get(kind);
        File file = new File(path + fileName);
        return file.exists();
    }


    public static String wordToHtml(String fileName) {
        Integer kind = textClassifier(fileName);
        String htmlPath = null;
        if (Objects.equals(kind, FileConfig.UNKNOWN_FILE) || !fileExists(fileName, kind)) {
            return null;
        }
        if (Objects.equals(kind, FileConfig.DOC_FILE)) {
            htmlPath = docToHtml(fileName);
        }
        if (Objects.equals(kind, FileConfig.DOCX_FILE)) {
            htmlPath = docxToHtml(fileName);
        }
        if (Objects.equals(kind, FileConfig.TXT_FILE)) {
            htmlPath = txtToHtml(fileName);
        }
        if (Objects.equals(kind, FileConfig.PDF_FILE)) {
            htmlPath = pdfToHtml(fileName);
        }
        return htmlPath;
    }

    private static String docToHtml(String fileName) {
        String targetPath = null;
        try {
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            wordToHtmlConverter.setPicturesManager((content, pictureType, name, width, height) -> {
                try (FileOutputStream out = new FileOutputStream(FileConfig.IMAGE_PATH + name)) {
                    out.write(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return FileConfig.IMAGE_RELATIVE_PATH + name;
            });
            wordToHtmlConverter.processDocument(new HWPFDocument(new FileInputStream(FileConfig.DOC_PATH + fileName)));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            targetPath = FileConfig.DOC_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
            transformer.transform(new DOMSource(wordToHtmlConverter.getDocument()), new StreamResult(new File(targetPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetPath;
    }

    public static String docxToHtml(String fileName) {
        String targetPath = null;
        // 1) 加载word文档生成 XWPFDocument对象
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(new FileInputStream(FileConfig.DOCX_PATH + fileName));
            XHTMLOptions options = XHTMLOptions.create();
            // options.setExtractor(new FileImageExtractor(new File(FileConfig.IMAGE_PATH)));
            options.URIResolver(new BasicURIResolver(FileConfig.IMAGE_RELATIVE_PATH));
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);
            targetPath = FileConfig.DOCX_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
            OutputStream out = new FileOutputStream(targetPath);
            XHTMLConverter.getInstance().convert(document, out, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetPath;
    }

    public static String txtToHtml(String fileName) {
        String targetPth = null;
        try {
            File file = new File(FileConfig.TXT_PATH + fileName);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(read);
                targetPth = FileConfig.TXT_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
                FileOutputStream fos = new FileOutputStream(new File(targetPth));
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter bw = new BufferedWriter(osw);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    bw.write("&nbsp&nbsp&nbsp" + lineTxt + "</br>");
                }
                bw.close();
                osw.close();
                fos.close();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return targetPth;
    }

    public static String pdfToHtml(String fileName) {
        String targetPth = null;
//        PDFDomTreeConfig config = PDFDomTreeConfig.createDefaultConfig();
//        HtmlResourceHandler resourceHandler = PDFDomTreeConfig.saveToDirectory(new File(FileConfig.PDF_PATH));
//        config.setImageHandler(resourceHandler);
//        config.setFontHandler(resourceHandler);
//        PDDocument pdf = null;
//        try {
//            pdf = PDDocument.load(new FileInputStream(FileConfig.PDF_PATH + fileName));
//            PDFDomTree parser = new PDFDomTree(config);
//            parser.setStartPage(0);
//            parser.setEndPage(pdf.getNumberOfPages());
//            Writer output = new StringWriter();
//            parser.writeText(pdf, output);
//            pdf.close();
//            targetPth = FileConfig.TXT_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
//            FileUtils.write(new File(targetPth), output.toString(), "utf-8");
//        } catch (Exception e) {
//            e.printStackTrace();
            // String outputPath = "C:\\works\\files\\ZSQ保密知识测试题库.html";
                  //try() 写在()里面会自动关闭流
        try {
            PDDocument document = PDDocument.load(new File(FileConfig.PDF_PATH + fileName));
            PDFDomTree pdfDomTree = new PDFDomTree();
            targetPth = FileConfig.PDF_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPth)), StandardCharsets.UTF_8));
            //加载PDF文档
            //PDDocument document = PDDocument.load(bytes);
            pdfDomTree.writeText(document, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetPth;
    }

    public static String htmlToString(String filePath) {
        File file = new File(filePath);
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        byte[] bytes = new byte[1024];
        try {
            for (int n; (n = input.read(bytes)) != -1; ) {
                buffer.append(new String(bytes, 0, n, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
