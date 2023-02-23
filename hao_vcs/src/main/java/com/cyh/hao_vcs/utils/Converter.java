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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static com.cyh.hao_vcs.config.FileConfig.RELATIVE_PATH;

public class Converter {


//    public static boolean fileExists(String fileName) {
//        return new File(getFilePath(fileName)).exists();
//    }

//    public static String getFilePath(String fileName){
//        return FileConfig.pathMap.get(textClassifier(fileName)) + fileName;
//    }
//
//    public static String wordToHtml(String fileName) {
//        Integer kind = textClassifier(fileName);
//        String htmlPath = null;
//        if (Objects.equals(kind, FileConfig.UNKNOWN_FILE) || !fileExists(fileName)) {
//            return null;
//        }
//        if (Objects.equals(kind, FileConfig.DOC_FILE)) {
//            htmlPath = docToHtml(fileName);
//        }
//        if (Objects.equals(kind, FileConfig.DOCX_FILE)) {
//            htmlPath = docxToHtml(fileName);
//        }
//        if (Objects.equals(kind, FileConfig.TXT_FILE)) {
//            htmlPath = txtToHtml(fileName);
//        }
//        if (Objects.equals(kind, FileConfig.PDF_FILE)) {
//            htmlPath = pdfToHtml(fileName);
//        }
//        return htmlPath;
//    }

//    public static String textToHtml(String path, Integer kind) {
//        String htmlPath = null;
//        if (Objects.equals(kind, FileConfig.UNKNOWN_FILE) || !fileExists(path)) {
//            return null;
//        }
//        if (Objects.equals(kind, FileConfig.DOC_FILE)) {
//            htmlPath = docToHtml(fileName);
//        }
//        if (Objects.equals(kind, FileConfig.DOCX_FILE)) {
//            htmlPath = docxToHtml(fileName);
//        }
//        if (Objects.equals(kind, FileConfig.TXT_FILE)) {
//            htmlPath = txtToHtml(fileName);
//        }
//        if (Objects.equals(kind, FileConfig.PDF_FILE)) {
//            htmlPath = pdfToHtml(fileName);
//        }
//        return htmlPath;
//    }

//    public static String docToHtml(String fileName) {
//        String targetPath = null;
//        try {
//            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
//                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
//            wordToHtmlConverter.setPicturesManager((content, pictureType, name, width, height) -> {
//                try (FileOutputStream out = new FileOutputStream(FileConfig.IMAGE_PATH + name)) {
//                    out.write(content);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return FileConfig.IMAGE_RELATIVE_PATH + name;
//            });
//            wordToHtmlConverter.processDocument(new HWPFDocument(new FileInputStream(FileConfig.DOC_PATH + fileName)));
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty(OutputKeys.METHOD, "html");
//            targetPath = FileConfig.DOC_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
//            transformer.transform(new DOMSource(wordToHtmlConverter.getDocument()), new StreamResult(new File(targetPath)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return targetPath;
//    }

public static String doc2Html(String fileName, String path) {
    String targetPath = null;
    try {
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        String newDir = FileConfig.IMAGE_PATH + fileName;
        FileUtil.createDir(newDir);
        wordToHtmlConverter.setPicturesManager((content, pictureType, name, width, height) -> {
            String picPath = newDir+File.separator+name;
            //照片存放地址
            try (FileOutputStream out = new FileOutputStream(picPath)) {
                out.write(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //html读取照片的地址
            return picPath;
        });
        wordToHtmlConverter.processDocument(new HWPFDocument(new FileInputStream(path)));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        targetPath = FileConfig.DOC_PATH + nameFromFile2Html(fileName);
        transformer.transform(new DOMSource(wordToHtmlConverter.getDocument()), new StreamResult(new File(targetPath)));
    } catch (Exception e) {
        e.printStackTrace();
    }
    return targetPath;
}

    public static String docx2Html(String fileName, String path) {
        String targetPath = null;
        XWPFDocument document = null;
        String newDir = FileConfig.IMAGE_PATH+fileName;
        FileUtil.createDir(newDir);
        try {
            document = new XWPFDocument(new FileInputStream(path));
            XHTMLOptions options = XHTMLOptions.getDefault();
            //存放照片地址
            options.setExtractor(new FileImageExtractor(new File(newDir)));
            options.URIResolver(new BasicURIResolver((RELATIVE_PATH+fileName)));
            //options.URIResolver(new BasicURIResolver(newDir.replace("\\","/")));
//            options.setIgnoreStylesIfUnused(false);
//            options.setFragment(true);
            targetPath = FileConfig.DOCX_PATH + nameFromFile2Html(fileName);
            OutputStream out = new FileOutputStream(targetPath);
            XHTMLConverter.getInstance().convert(document, out, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetPath;
    }

    public static String txt2Html(String fileName, String path) {
        String targetPath = null;
        try {
                InputStreamReader read = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(read);
                targetPath = FileConfig.TXT_PATH + nameFromFile2Html(fileName);
                FileOutputStream fos = new FileOutputStream(targetPath);
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
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return targetPath;
    }

    public static String pdf2Html(String fileName, String path) {
        String targetPth = null;
        try {
            PDDocument document = PDDocument.load(new File(path));
            PDFDomTree pdfDomTree = new PDFDomTree();
            targetPth = FileConfig.PDF_PATH + nameFromFile2Html(fileName);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPth), StandardCharsets.UTF_8));
            //加载PDF文档
            //PDDocument document = PDDocument.load(bytes);
            pdfDomTree.writeText(document, out);
            out.flush();
            out.close();
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetPth;
    }

    public static String nameFromFile2Html(String fileName){
        return fileName.substring(0, fileName.lastIndexOf(".")) + ".html";
    }

//    public static String docxToHtml(String fileName) {
//        String targetPath = null;
//        // 1) 加载word文档生成 XWPFDocument对象
//        XWPFDocument document = null;
//        try {
//            document = new XWPFDocument(new FileInputStream(FileConfig.DOCX_PATH + fileName));
//            XHTMLOptions options = XHTMLOptions.create();
//            // options.setExtractor(new FileImageExtractor(new File(FileConfig.IMAGE_PATH)));
//            options.URIResolver(new BasicURIResolver(FileConfig.IMAGE_RELATIVE_PATH));
//            options.setIgnoreStylesIfUnused(false);
//            options.setFragment(true);
//            targetPath = FileConfig.DOCX_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
//            OutputStream out = new FileOutputStream(targetPath);
//            XHTMLConverter.getInstance().convert(document, out, options);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return targetPath;
//    }

//    public static String txtToHtml(String fileName) {
//        String targetPth = null;
//        try {
//            File file = new File(FileConfig.TXT_PATH + fileName);
//            if (file.isFile() && file.exists()) {
//                InputStreamReader read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
//                BufferedReader bufferedReader = new BufferedReader(read);
//                targetPth = FileConfig.TXT_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
//                FileOutputStream fos = new FileOutputStream(new File(targetPth));
//                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
//                BufferedWriter bw = new BufferedWriter(osw);
//                String lineTxt = null;
//                while ((lineTxt = bufferedReader.readLine()) != null) {
//                    bw.write("&nbsp&nbsp&nbsp" + lineTxt + "</br>");
//                }
//                bw.close();
//                osw.close();
//                fos.close();
//                read.close();
//            } else {
//                System.out.println("找不到指定的文件");
//            }
//        } catch (Exception e) {
//            System.out.println("读取文件内容出错");
//            e.printStackTrace();
//        }
//        return targetPth;
//    }

//    public static String pdfToHtml(String fileName) {
//        String targetPth = null;
//        try {
//            PDDocument document = PDDocument.load(new File(FileConfig.PDF_PATH + fileName));
//            PDFDomTree pdfDomTree = new PDFDomTree();
//            targetPth = FileConfig.PDF_PATH + fileName.substring(0, fileName.indexOf(".")) + ".html";
//            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(targetPth)), StandardCharsets.UTF_8));
//            //加载PDF文档
//            //PDDocument document = PDDocument.load(bytes);
//            pdfDomTree.writeText(document, out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return targetPth;
//    }

    public static String htmlToString(String filePath) {
        String data = null;
        try {
            data = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        System.out.println(htmlToString("D:\\ADeskTop\\project\\bigWork\\html\\docx\\test.html"));
    }

}
