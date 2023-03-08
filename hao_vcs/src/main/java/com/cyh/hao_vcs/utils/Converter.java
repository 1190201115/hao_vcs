package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.config.VersionConfig;
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
                String picPath = newDir + File.separator + name;
                //照片存放地址
                try (FileOutputStream out = new FileOutputStream(picPath)) {
                    out.write(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //html读取照片的地址
                return RELATIVE_PATH + fileName + File.separator + name;
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
        String newDir = IMAGE_PATH + fileName;
        FileUtil.createDir(newDir);
        try {
            document = new XWPFDocument(new FileInputStream(path));
            XHTMLOptions options = XHTMLOptions.getDefault();
            //存放照片地址
            options.setExtractor(new FileImageExtractor(new File(newDir)));
            options.URIResolver(new BasicURIResolver((RELATIVE_PATH + fileName)));
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
                bw.write(lineTxt + "</br>");
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

    public static String nameFromFile2Html(String fileName) {
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
            byte content[] = htmlToString(htmlPath).replaceAll(RELATIVE_PATH, IMAGE_PATH_REGEX)
                    .getBytes(StandardCharsets.UTF_8);
            try {
                POIFSFileSystem poifsFileSystem = new POIFSFileSystem();
                poifsFileSystem.getRoot().createDocument("WordDocument", new ByteArrayInputStream(content));
                FileOutputStream outputStream = new FileOutputStream(TEMP_PATH + fileName);
                poifsFileSystem.writeFilesystem(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Document htmlToDocument(String htmlPath) {
        if (StringUtils.isEmpty(htmlPath)) {
            return null;
        }
        if (new File(htmlPath).exists()) {
            try (Scanner scanner = new Scanner(new File(htmlPath))) {
                String content = scanner.useDelimiter("\\\\A").next();
                scanner.close();
                return Jsoup.parse(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    public static String htmlToTextForTxt(String htmlPath) {
//        Document document = htmlToDocument(htmlPath);
//        if (!Objects.isNull(document)) {
//            document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
//            document.select("br").append("\\n");
//            document.select("p").prepend("\\n");
//            return document.text().replaceAll("&nbsp", "");
//            //Jsoup.clean(temp, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
//        }
//        return null;
//    }
//
//    public static List<String> htmlToList(String htmlPath) {
//        String text = htmlToTextForTxt(htmlPath);
//        List<String> list = null;
//        if (!StringUtils.isEmpty(text)) {
//            list = Arrays.asList(text.split("\\\\n"));
//        }
//        return list;
//    }

    /**
     * 除了返回Elements外也填充了textList
     *
     * @param document
     * @param textList
     * @return
     */
    public static Elements getElementListWithOwnText(Document document, List<String> textList) {
        Elements elements = document.body().getAllElements();
        Elements trimElementList = new Elements();
        String text = null;
        for (Element span : elements) {
            text = span.ownText();
            if (!StringUtils.isEmpty(text)) {
                textList.add(text);
                trimElementList.add(span);
            }
        }
        return trimElementList;
    }

    private static void handleInsertDiff(int targetLinesSize, int targetPosition, Elements newElements, String style) {
        for (int i = 0; i < targetLinesSize; i++) {
            newElements.get(targetPosition + i).attr("style", style);
        }
    }

    private static void handleDeleteDiff(int originLinesSize, int originPosition, Elements originElements, String style) {
        for (int i = 0; i < originLinesSize; i++) {
            originElements.get(originPosition + i).attr("style", style);
        }
    }

    /**
     * 这是用于将删除显示在新文件上的方法，在2.0对比中被废弃
     *
     * @param sourceLinesSize
     * @param sourcePosition
     * @param targetPosition
     * @param newElements
     * @param originElements
     */
    private static void handleDeleteDiff(int sourceLinesSize, int sourcePosition, int targetPosition, Elements newElements, Elements originElements) {
        Element delElement = originElements.get(sourcePosition);
        for (int i = 1; i < sourceLinesSize; i++) {
            delElement.append(originElements.get(sourcePosition + i).html());
        }
        delElement.append("<br/>");
        delElement.attr("style", "background-color: #e86c8c;text-decoration: line-through;");
        if (targetPosition > 0) {
            newElements.get(targetPosition - 1).after(delElement);
        } else {
            newElements.get(targetPosition).before(delElement);
        }
    }

    /**
     * @param htmlPathOrigin 原始html全路径
     * @param htmlPathNew    新html全路径
     * @param diffFileName 差异文件名称（包含后缀）
     * return diffFile's path
     */
    public static String showHtmlDiff(String htmlPathOrigin, String htmlPathNew, String diffFileName) {
        if(new File(DIFF_PATH+ diffFileName).exists()){
            return FileConfig.RELATIVE_DIFF_PATH + diffFileName;
        }
        List<String> originTextList = new ArrayList<>();
        Document originDocument = htmlToDocument(htmlPathOrigin);
        Document newDocument = htmlToDocument(htmlPathNew);
        if(Objects.isNull(originDocument) || Objects.isNull(newDocument)){
            return null;
        }
        Elements originElements = getElementListWithOwnText(originDocument, originTextList);
        List<String> newTextList = new ArrayList<>();
        Elements newElements = getElementListWithOwnText(newDocument, newTextList);
        Patch<String> patch = DiffUtils.diff(originTextList, newTextList);
        List<AbstractDelta<String>> deltas = patch.getDeltas();
        Chunk<String> source = null;
        Chunk<String> target = null;
        List<String> targetLines = null;
        List<String> sourceLines = null;
        for (AbstractDelta<String> delta : deltas) {
            DeltaType type = delta.getType();
            source = delta.getSource();
            target = delta.getTarget();
            targetLines = target.getLines();
            sourceLines = source.getLines();
            if (type.equals(DeltaType.INSERT)) {
                handleInsertDiff(targetLines.size(), target.getPosition(), newElements, VersionConfig.INSERT_STYLE);
            } else if (type.equals(DeltaType.DELETE)) {
                handleDeleteDiff(sourceLines.size(), source.getPosition(), originElements, VersionConfig.DELETE_STYLE);
            } else if (type.equals(DeltaType.CHANGE)) {
                handleInsertDiff(targetLines.size(), target.getPosition(), newElements, VersionConfig.UPDATE_INSERT_STYLE);
                handleDeleteDiff(sourceLines.size(), source.getPosition(), originElements, VersionConfig.UPDATE_DELETE_STYLE);

            }
        }
        StringBuilder stringBuilder = new StringBuilder("<div style=\"float: right;width: 45%\">");
        stringBuilder.append(newDocument.body().html()).append("</div>")
                .append("<div style=\"float: left;width: 45v%\">").append(originDocument.body().html()).append("</div>");
        newDocument.head().append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        newDocument.body().html(stringBuilder.toString());
        FileUtil.saveFile(DIFF_PATH+ diffFileName, newDocument.html());
        return FileConfig.RELATIVE_DIFF_PATH + diffFileName;
    }


}
