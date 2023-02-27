package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fit.pdfdom.PDFDomTree;
import org.fit.pdfdom.PDFDomTreeConfig;
import org.fit.pdfdom.resource.HtmlResourceHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

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
import java.util.*;
import java.util.regex.Matcher;

import static com.cyh.hao_vcs.config.FileConfig.*;

public class Converter {

public static String doc2Html(String fileName, String path) {
    String targetPath = null;
    try {
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        String newDir = IMAGE_PATH + fileName;
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
            return RELATIVE_PATH+fileName+File.separator+name;
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
        String newDir = IMAGE_PATH+fileName;
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


    public static String htmlToString(String filePath) {
        String data = null;
        try {
            data = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String htmlToWord(String htmlPath, String fileName) {
        if (StringUtils.isEmpty(htmlPath)) {
            return null;
        }
        File html = new File(htmlPath);
        if (html.exists()) {
            byte content[] = htmlToString(htmlPath). replaceAll(RELATIVE_PATH, IMAGE_PATH_REGEX)
                    .getBytes(StandardCharsets.UTF_8);
            try {
                POIFSFileSystem poifsFileSystem = new POIFSFileSystem();
                poifsFileSystem.getRoot().createDocument("WordDocument",new ByteArrayInputStream(content));
                FileOutputStream outputStream = new FileOutputStream(TEMP_PATH + fileName);
                poifsFileSystem.writeFilesystem(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Document htmlToDocument(String htmlPath){
        if (StringUtils.isEmpty(htmlPath)) {
            return null;
        }
        if (new File(htmlPath).exists()) {
            try(Scanner scanner=new Scanner( new File(htmlPath))) {
                String content = scanner.useDelimiter("\\\\A").next();
                scanner.close();
                return Jsoup.parse(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param htmlPath html文件路径（含文件名） 若文件不存在返回null
     * @return 提取的纯文本String，以空格为分隔
     */
    public static String htmlToText(String htmlPath) {
        Document document = htmlToDocument(htmlPath);
        if(!Objects.isNull(document)){
            return document.text();
        }
        return null;
    }

    public static void getTextDiff(String htmlPathOrigin, String htmlPathNew){
        String text1 = htmlToText(htmlPathOrigin);
        String text2 = htmlToText(htmlPathNew);
        if(StringUtils.isEmpty(text1) || StringUtils.isEmpty(text2)){
            return;
        }
        List list1 = Arrays.asList(text1.split(" "));
        List list2 = Arrays.asList(text2.split(" "));
        Patch<String> patch = DiffUtils.diff(list1, list2);
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            System.out.println(delta.getType());
            System.out.println(delta);
        }
    }

    public static void replaceTextWithDiff(String htmlPath){
        Document document = htmlToDocument(htmlPath);


        Elements spans = document.getElementsByTag("span");

        for (Element span : spans) {
            System.out.println(span.toString());
//            String link_href = span.attr("href");
//
//            String link_text = span.text();

        }
    }

    public static void main(String[] args) {
//        getTextDiff("D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.0.html",
//                "D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.1.html");
        replaceTextWithDiff("D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.1.html");
    }


}
