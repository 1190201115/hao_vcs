package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
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
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import javax.print.Doc;
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
import java.util.stream.Collectors;

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
    public static String htmlToTextForWord(String htmlPath) {
        Document document = htmlToDocument(htmlPath);
        if(!Objects.isNull(document)){
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
            document.select("span").prepend("\\n");
            document.select("span").append("\\n");
            document.select("title").remove();
            return document.text();
        }
        return null;
    }
    public static String htmlToTextForTxt(String htmlPath) {
        Document document = htmlToDocument(htmlPath);
        if(!Objects.isNull(document)){
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
            document.select("br").append("\\n");
            document.select("p").prepend("\\n");
            return document.text().replaceAll("&nbsp","");
             //Jsoup.clean(temp, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        }
        return null;
    }

    public static List<String> htmlToList(String htmlPath){
        String text = htmlToTextForTxt(htmlPath);
        List<String> list = null;
        if(!StringUtils.isEmpty(text)){
            list = Arrays.asList(text.split("\\\\n"));
        }
        return list;
    }

    public static void getWordDiff(String htmlPathOrigin, String htmlPathNew){
        String text1 = htmlToTextForWord(htmlPathOrigin);
        String text2 = htmlToTextForWord(htmlPathNew);
        if(StringUtils.isEmpty(text1) || StringUtils.isEmpty(text2)){
            return;
        }
        List list1 = trimContentOfWord(text1);
        List list2 = trimContentOfWord(text2);
        Patch<String> patch = DiffUtils.diff(list1, list2);
        replaceTextWithDiff(htmlPathOrigin,htmlPathNew, patch);
//        List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff("test1.txt", "test2.txt", list1, patch, 0);
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            System.out.println(delta.toString());
        }
    }

    private static List<String> trimContentOfWord(String text){
        return Arrays.stream(text.split("\\\\n")).filter(str-> !"".equals(str) && !" ".equals(str)).map(str ->{
            return str.trim().replaceAll(",","，");//英文逗号用于区分修改区域
        }).collect(Collectors.toList());
    }


    /**
     * 对单词按照deltaType进行替换
     */
    public static void replaceWordWithDiff(AbstractDelta<String> delta){
        DeltaType type = delta.getType();
        if(type.equals(DeltaType.INSERT)){

        }
    }
    private static Elements trimSpans(Elements spans){
        int i = 0;
        Stack<Integer> stack = new Stack<>();
        for(Element span:spans){
            System.out.println(i+span.text());
            if("".equals(span.text())){
                stack.add(i);
            }
            i++;
        }
        while(!stack.isEmpty()){
            spans.remove(spans.get(stack.pop()));
        }
        return spans;
    }
    public static void replaceTextWithDiff(String originHtmlPath, String htmlPath, Patch<String> patch){
        Document document = htmlToDocument(htmlPath);
        Document originDocument = htmlToDocument(originHtmlPath);
        Elements spans = trimSpans(document.getElementsByTag("span"));
        Elements originSpans = trimSpans(originDocument.getElementsByTag("span"));
        List<AbstractDelta<String>> deltas =  patch.getDeltas();
        Chunk<String> source = null;
        Chunk<String> target = null;
        List<String> targetLines = null;
        List<String> sourceLines = null;
        int sourcePosition = 0;
        int targetPosition =  0;
        int targetLinesSize = 0;
        int sourceLinesSize = 0;
        for (AbstractDelta<String> delta : deltas) {
            DeltaType type = delta.getType();
            source = delta.getSource();
            target = delta.getTarget();
            targetLines = target.getLines();
            sourceLines = source.getLines();
            if(type.equals(DeltaType.INSERT)){
                targetLinesSize = targetLines.size();
                targetPosition = target.getPosition();
                for(int i = 0; i < targetLinesSize; i++){
                    spans.get(targetPosition + i).attr("style","background-color: #43b443;");
                }
            }else if(type.equals(DeltaType.DELETE)){
                sourceLinesSize = sourceLines.size();
                targetPosition = target.getPosition() - 1;
                sourcePosition = source.getPosition();
                for(int i = 0; i < sourceLinesSize; i++) {
                    Element delHtml = originSpans.get(sourcePosition + i).wrap("<del></del>").attr("style", "background-color: #e86c8c;");
                    spans.get(targetPosition + i).parent().after(delHtml.parentNode());
                }
            }else if(type.equals(DeltaType.CHANGE)){
                sourcePosition = source.getPosition();
                sourceLinesSize = sourceLines.size();
                targetLinesSize = targetLines.size();
                targetPosition = target.getPosition();
                for(int i = 0; i < targetLinesSize || i < sourceLinesSize; i++){
                    if(i < sourceLinesSize){
                        Element delHtml = originSpans.get(sourcePosition + i).wrap("<del></del>").attr("style","background-color: #e86c8c;");
                        spans.get(targetPosition + i).before(delHtml.parentNode());
                        spans.get(targetPosition + i).attr("style","background-color: yellow;");
                    }
                    else {
                        spans.get(targetPosition + i).attr("style", "background-color: #43b443;");
                    }
                }
            }
        }

        FileUtil.saveFile(FileConfig.DIFF_PATH+"wordTest.html",document.html());
//        for (Element span : spans) {
//            System.out.println(span.toString());
//            String link_href = span.attr("href");
//
//            String link_text = span.text();

    }

    public static void main(String[] args) {
//       getTextDiff("D:\\ADeskTop\\project\\bigWork\\html\\txt\\6dd77e3f-9edf-4cbb-9af4-adadaadd7679-v1.0.1.html",
//                "D:\\ADeskTop\\project\\bigWork\\html\\txt\\6dd77e3f-9edf-4cbb-9af4-adadaadd7679-v6.0.0.html");
//        getTextDiff("D:\\ADeskTop\\project\\bigWork\\html\\doc\\c5e10406-0ed1-48f8-b78e-71543241a1ca-v1.0.0.html",
//                "D:\\ADeskTop\\project\\bigWork\\html\\doc\\c5e10406-0ed1-48f8-b78e-71543241a1ca-v1.0.1.html");
//        getTextDiff("D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.1.html",
//                "D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.4.html");
//        replaceTextWithDiff("D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.1.html");
        getWordDiff("D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.1.html",
                "D:\\ADeskTop\\project\\bigWork\\html\\docx\\8df70494-411f-4083-a35c-1d4607d7a336-v1.0.4.html");
    }


}
