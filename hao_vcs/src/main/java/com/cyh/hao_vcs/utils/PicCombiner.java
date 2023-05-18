package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.PicImf;
import com.cyh.hao_vcs.log.PicLog;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.freewayso.image.combiner.enums.ZoomMode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PicCombiner {

    public static Map<String, OutputFormat> picKindMap= new HashMap<>();

    static{
        picKindMap.put("jpg",OutputFormat.JPG);
        picKindMap.put("jpeg",OutputFormat.JPEG);
        picKindMap.put("png",OutputFormat.PNG);
        picKindMap.put("bmp",OutputFormat.BMP);
    }

    public static PicImf getPicImf(String path){
        File picture = new File(path);
        if(!picture.exists()){
            return null;
        }
        try {
            BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
            return new PicImf(path.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH),
                    sourceImg.getWidth(),sourceImg.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static OutputFormat getPicKind(String path){
        OutputFormat res = null;
        String suffix = FileUtil.getSuffix(path);
        if(!Objects.isNull(suffix)){
            res = picKindMap.get(suffix);
        }
        if( Objects.isNull(res)){
            res = OutputFormat.PNG;
        }
        return res;
    }

    public static ImageCombiner combinePic(String basePicPath, int backgroundWidth, int backgroundHeight,
                                    String addPicPath, int addWidth, int addHeight ){
        ImageCombiner combiner = null;
        try {
            combiner = new ImageCombiner(basePicPath, backgroundWidth, backgroundHeight, ZoomMode.Height,getPicKind(basePicPath));
            //加图片元素
            combiner.addImageElement(addPicPath, addWidth, addHeight);
            //执行图片合并
            combiner.combine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return combiner;
    }

    public static void savePic(String savePath, BufferedImage newImg) {
        if (savePath != null && newImg != null) {
            try {
                File outputFile = new File(savePath);
                ImageIO.write(newImg, FileUtil.getSuffix(savePath), outputFile);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static BufferedImage cutPic(BufferedImage image, int x, int y, int width, int height){
        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        Image instance = image.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
        ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
        Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(instance.getSource(), cropFilter));
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
        g.dispose();
        return image;
    }

    public static BufferedImage createImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static BufferedImage changePicSize(BufferedImage image,int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //创建画布
        Graphics2D g2 = bi.createGraphics();
        //画图
        g2.drawImage(image, 0,0,width,height, null);
        //关闭资源
        g2.dispose();
        return bi;
    }

    public static void addWater(BufferedImage image,int width, int height, String content) {
        Graphics2D graphics2D = image.createGraphics();
        // 设置水印字体颜色为红色
        graphics2D.setColor(Color.black);
        int size = Math.min(30, Math.min(image.getWidth(), image.getHeight())/ content.length() / 2);
        // 设置水印字体、字型、字号
        graphics2D.setFont(new Font("微软雅黑", Font.PLAIN, size));
        // 设置水印位置：居中
        graphics2D.drawString(content,width,height);
        // 安排---画板
        graphics2D.dispose();
    }

    //把log中的属性加到picLog上
    public static BufferedImage mapStringToPicLog(String log, String target, BufferedImage image ){
        log = log.substring(1, log.length() - 1).replaceAll(", ", "");
        String[] split = log.split("##&");
        int len = split.length;
        for(int i = 0; i < len; ++i){
            String imf = split[i];
            String[] data;
            if(StatusEnum.LOG_PIC_CUT.equals(imf)){
                imf = split[++i];
                data = imf.split("-");
                image = cutPic(image, Integer.parseInt(data[0]),  Integer.parseInt(data[1]),  Integer.parseInt(data[2]),  Integer.parseInt(data[3]));
            }else if(StatusEnum.LOG_PIC_SIZE.equals(imf)){
                imf = split[++i];
                data = imf.split("-");
                image = changePicSize(image, Integer.parseInt(data[0]), Integer.parseInt(data[1]));
            }else if(StatusEnum.LOG_PIC_WATER.equals(imf)){
                imf = split[++i];
                data = imf.split("-");
                addWater(image, Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2]);
            }
        }
        return image;
    }

    public static void main(String[] args) throws Exception {
        BufferedImage image = createImage("D:\\ADeskTop\\project\\bigWork\\repository\\2-图片测试\\d71e7214-abde-46cc-826d-d8ff9c71763e-v1.0.0.jpg");
        //image = changePicSize(image, 100, 120);
        //image = cutPic(image, 0, 0, 720, 1920);
        //addWater(image, 100,500,"陈宇豪嘎嘎陈宇豪嘎嘎陈宇豪嘎嘎陈宇豪嘎嘎陈宇豪嘎嘎");
        savePic("D:\\ADeskTop\\project\\bigWork\\repository\\2-图片测试\\test.jpg", image);
//        ImageCombiner combiner = new ImageCombiner("file:///D:\\ADeskTop\\project\\bigWork\\repository\\2-图片测试\\d71e7214-abde-46cc-826d-d8ff9c71763e-v1.0.0.jpg",
//                100, 120, ZoomMode.Height,OutputFormat.JPG);
//        combiner.combine();
//        combiner.save("D:\\ADeskTop\\project\\bigWork\\repository\\2-图片测试\\test.jpg");
        //getPicImf("D:\\ADeskTop\\project\\bigWork\\repository\\3- 音视频测试\\ae649190-fc3c-4552-a946-01e228b7d880-v5.0.0.mp3");
//        combinePic("D:\\ADeskTop\\ccip.jpg",864,1920,"D:\\ADeskTop\\xc.jpg",
//                0)
            //getPicImf("D:\\ADeskTop\\project\\bigWork\\image\\a.jpeg");
//        public void demo() throws Exception {
//
//            //图片元素可以是Url（支持file协议），也可以是BufferImage对象
//            String bgImageUrl = "http://xxx.com/image/bg.jpg";                  //背景图（url）
//            String qrCodeUrl = "file:///d:/qrCode.png";                         //二维码（file协议）
//            String productImageUrl = "https://xxx.com/image/product.jpg";       //商品图
//            BufferedImage waterMark = ImageIO.read(new URL("https://xxx.com/image/waterMark.jpg")); //水印图
//            BufferedImage avatar = ImageIO.read(new File("d:/avatar.jpg"));     //头像
//            String title = "# 最爱的家居";                                       //标题文本
//            String content = "苏格拉底说：“如果没有那个桌子，可能就没有那个水壶”";  //内容文本
//
//            //创建合成器（指定背景图和输出格式，整个图片的宽高和相关计算依赖于背景图，所以背景图的大小是个基准）
//            ImageCombiner combiner = new ImageCombiner(bgImageUrl, 1500, 0, ZoomMode.Height, OutputFormat.JPG);  //v1.1.4之后可以指定背景图新宽高了（不指定则默认用图片原宽高）
//
//            //针对背景和整图的设置
//            combiner.setBackgroundBlur(30);     //设置背景高斯模糊（毛玻璃效果）
//            combiner.setCanvasRoundCorner(100); //设置整图圆角（输出格式必须为PNG）
//            combiner.setQuality(.8f);           //设置图片保存质量（0.0~1.0，Java9以下仅jpg格式有效）
//
//            //标题（默认字体为阿里普惠、黑色，也可以自己指定Font对象）
//            combiner.addTextElement(title, 0, 150, 1400)
//                    .setCenter(true)        //居中绘制（会忽略x坐标，改为自动计算）
//                    .setAlpha(.8f)          //透明度（0.0~1.0）
//                    .setRotate(45)          //旋转（0~360）
//                    .setColor(Color.Red)    //颜色
//                    .setDirection(Direction.RightLeft) //绘制方向（从右到左，用于需要右对齐场景）
//                    .setAutoFitWidth(200);  //自适应最大宽度（超出则自动缩小字体）
//
//            //副标题（v2.6.3版本开始支持加载项目内字体文件，可以不用在服务器安装，性能略低）
//            combiner.addTextElement("年度狂欢", "/font/yahei.ttf", 0, 150, 1450)
//
//            //内容（设置文本自动换行，需要指定最大宽度（超出则换行）、最大行数（超出则丢弃）、行高）
//            combiner.addTextElement(content, "微软雅黑", Font.BOLD, 40, 150, 1480)
//                    .setSpace(.5f)                      //字间距
//                    .setStrikeThrough(true)             //删除线
//                    .setAutoBreakLine(837, 2, 60);      //自动换行（还有一个LineAlign参数可以指定对齐方式）
//
//            //商品图（设置坐标、宽高和缩放模式，若按宽度缩放，则高度按比例自动计算）
//            combiner.addImageElement(productImageUrl, 0, 160, 837, 0, ZoomMode.Width)
//                    .setCenter(true)        //居中绘制（会忽略x坐标，改为自动计算）
//                    .setRoundCorner(46)     //设置圆角
//
//            //头像（圆角设置一定的大小，可以把头像变成圆的）
//            combiner.addImageElement(avatar, 200, 1200)
//                    .setRoundCorner(200);   //圆角
//
//            //水印（设置透明度，0.0~1.0）
//            combiner.addImageElement(waterMark, 630, 1200)
//                    .setAlpha(.8f)          //透明度（0.0~1.0）
//                    .setRotate(45)          //旋转（0~360）
//                    .setBlur(20)            //高斯模糊(1~100)
//                    .setRepeat(true, 100, 50);    //平铺绘制（可设置水平、垂直间距）
//
//            //加入圆角矩形元素（版本>=1.2.0），作为二维码的底衬
//            combiner.addRectangleElement(138, 1707, 300, 300)
//                    .setColor(Color.WHITE)
//                    .setRoundCorner(50)     //该值大于等于宽高时，就是圆形，如设为300
//                    .setAlpha(.8f)
//                    .setGradient(Color.yellow,Color.blue,GradientDirection.LeftRight);  //颜色渐变
//            .setBorderSize(5);      //设置border大小就是空心，不设置就是实心
//
//            //二维码（强制按指定宽度、高度缩放）
//            combiner.addImageElement(qrCodeUrl, 138, 1707, 186, 186, ZoomMode.WidthHeight);
//
//            //价格（元素对象也可以直接new，然后手动加入待绘制列表）
//            TextElement textPrice = new TextElement("￥1290", 60, 230, 1300);
//            textPrice.setColor(Color.red);          //红色
//            textPrice.setStrikeThrough(true);       //删除线
//            combiner.addElement(textPrice);         //加入待绘制集合
//
//            //执行图片合并
//            combiner.combine();
//
//            //可以获取流（并上传oss等）
//            InputStream is = combiner.getCombinedImageStream();
//
//            //也可以保存到本地
//            //combiner.save("d://image.jpg");
//        }



    }
}
