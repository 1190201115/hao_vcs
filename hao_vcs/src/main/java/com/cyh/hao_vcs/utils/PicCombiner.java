package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.PicImf;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PicCombiner {

    public static PicImf getPicImf(String path){
        File picture = new File(path);
        try {
            BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
            return new PicImf(path.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH),
                    sourceImg.getWidth(),sourceImg.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
            getPicImf("D:\\ADeskTop\\project\\bigWork\\image\\a.jpeg");
//        ImageCombiner combiner = null;
//        try {
//            combiner = new ImageCombiner("https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", OutputFormat.JPEG);
//            //加图片元素
//            //combiner.addImageElement("https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg", 0, 300);
//
//            //加文本元素
//            combiner.addTextElement("周末大放送", 100, 10, 100);
//
//            //执行图片合并
//            combiner.combine();
//
//            //可以获取流（并上传oss等）
//            InputStream is = combiner.getCombinedImageStream();
//
//            //也可以保存到本地
//            combiner.save("D:\\ADeskTop\\project\\bigWork\\image\\a.jpeg");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }



    }
}
