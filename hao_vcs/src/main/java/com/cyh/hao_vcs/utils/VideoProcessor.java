package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.AudioImf;
import com.cyh.hao_vcs.entity.VideoImf;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import sun.font.FontDesignMetrics;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoProcessor {



    private static void initRecoder(FFmpegFrameRecorder recorder, FFmpegFrameGrabber grabber){
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setVideoCodec(grabber.getVideoCodec());
        recorder.setVideoBitrate(grabber.getVideoBitrate());
        recorder.setSampleRate(grabber.getSampleRate());
        recorder.setAudioCodec(grabber.getAudioCodec());
        recorder.setAudioBitrate(grabber.getAudioBitrate());
    }

    /**
     * 抽取视频图像
     * @param inputFile
     * @param outputFile
     * @throws Exception
     */
    public static void videoGetImage(String inputFile, String outputFile) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight());
//        recorder.setFormat(grabber.getFormat());
//        recorder.setVideoCodec(grabber.getVideoCodec());//27
//        recorder.setVideoBitrate(grabber.getVideoBitrate());//1991613
        initRecoder(recorder,grabber);
        recorder.start();
        Frame frame;
        while ((frame = grabber.grabImage()) != null) {
                recorder.record(frame);
        }
        recorder.close();
        grabber.close();
    }

    /**
     * 抽取视频声音
     * @param inputFile
     * @param outputFile
     * @throws Exception
     */
    public static void videoGetSamples(String inputFile, String outputFile) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getAudioChannels());
//        recorder.setFormat(grabber.getFormat());
        recorder.setAudioBitrate(grabber.getAudioBitrate());//317768
        recorder.setSampleRate(grabber.getSampleRate());//48000
        //initRecoder(recorder,grabber);
        recorder.start();
        Frame frame;
        while ((frame = grabber.grabSamples()) != null) {
            recorder.record(frame);
        }
        recorder.close();
        grabber.close();
    }

    public static void videoAddText(String inputFile, String outputFile, String text) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(),
                grabber.getAudioChannels());
        initRecoder(recorder,grabber);
        recorder.start();
        Java2DFrameConverter converter = new Java2DFrameConverter();
        Frame frame;
        while((frame = grabber.grab()) != null){
            if(frame.image != null){
                BufferedImage bufImg = converter.getBufferedImage(frame);
                Font font = new Font("微软雅黑", Font.BOLD, 32);
                FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
                Graphics2D graphics = bufImg.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                //设置图片背景
                graphics.drawImage(bufImg, 0, 0, bufImg.getWidth(), bufImg.getHeight(), null);
                // 计算文字长度，计算居中的x点坐标
                int textWidth = metrics.stringWidth(text);
                int widthX = bufImg.getWidth() - textWidth;
                graphics.setColor(Color.red);
                graphics.setFont(font);
                graphics.drawString(text, widthX, bufImg.getHeight() - 40);
                graphics.dispose();
                // 视频帧赋值，写入输出流
                frame.image = converter.getFrame(bufImg).image;
                recorder.record(frame);
            }
            if(frame.samples != null){
                recorder.record(frame);
            }
        }
        recorder.close();
        grabber.close();
    }

    public static double videoClip(String inputFile, String outputFile, double startMs, double endMs) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(),
                grabber.getAudioChannels());
        initRecoder(recorder,grabber);
        recorder.start();
        Frame frame;
        int startFrame = (int)(startMs * grabber.getFrameRate());
        int endFrame = (int)(endMs * grabber.getFrameRate());
        System.out.println(startFrame + " - "+ endFrame);
        int frameNumber = 0;
        while((frame = grabber.grab()) != null){
            if(frame.image != null){
                if(frameNumber++ < startFrame){
                    continue;
                }
                recorder.record(frame);
            }
            if(frame.samples != null){
                if(frameNumber < startFrame){
                    continue;
                }
                recorder.record(frame);
            }
            if(frameNumber > endFrame) break;
        }
        recorder.stop();
        grabber.stop();

        return FileUtil.getMediaTime(outputFile);
//        grabber = FFmpegFrameGrabber.createDefault(outputFile);
//        grabber.start();
//        double durationInSec = grabber.getFormatContext().duration() / 1000000.0;
//        grabber.stop();
//        return durationInSec;
    }

    // 返回视频时长
    public static double videoClipByDel(String inputFile, String outputFile, double startMs, double endMs) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(),
                grabber.getAudioChannels());
        initRecoder(recorder,grabber);
        recorder.start();
        Frame frame;
        int endFrame = (int)(startMs * grabber.getFrameRate());
        int startFrame = (int)(endMs * grabber.getFrameRate());
        int frameNumber = -1;
        while((frame = grabber.grab()) != null){
            if(frame.image != null){
                frameNumber++;
                if(frameNumber < startFrame && frameNumber > endFrame){
                    continue;
                }
                recorder.record(frame);
            }
            if(frame.samples != null){
                if(frameNumber < startFrame && frameNumber > endFrame){
                    continue;
                }
                recorder.record(frame);
            }
        }
        recorder.stop();
        grabber = FFmpegFrameGrabber.createDefault(outputFile);
        grabber.start();
        double durationInSec = grabber.getFormatContext().duration() / 1000000.0;
        grabber.stop();
        return durationInSec;
    }

    /**
     * 融合视频画面与声音
     * @param imageFile
     * @param samplesFile
     * @param outputFile
     * @throws Exception
     */
    public static void videoMixImageAndSamples(String imageFile, String samplesFile,String outputFile) throws Exception{
        FFmpegFrameGrabber imageGrabber = new FFmpegFrameGrabber(new File(imageFile));
        imageGrabber.start();
        FFmpegFrameGrabber samplesGrabber = new FFmpegFrameGrabber(new File(samplesFile));
        samplesGrabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(new File(outputFile), imageGrabber.getImageWidth(),
                imageGrabber.getImageHeight(),samplesGrabber.getAudioChannels());
        recorder.setFormat(imageGrabber.getFormat());
        recorder.setVideoCodec(imageGrabber.getVideoCodec());
        recorder.setVideoBitrate(imageGrabber.getVideoBitrate());
        recorder.setSampleRate(samplesGrabber.getSampleRate());
        recorder.setFrameRate(imageGrabber.getFrameRate());
        recorder.setAudioBitrate(samplesGrabber.getAudioBitrate());
        recorder.start();
        Frame samplesFrame,imageFrame;
        while((imageFrame = imageGrabber.grabImage()) != null){
            recorder.record(imageFrame);
        }
        while((samplesFrame = samplesGrabber.grabSamples()) != null ){
            recorder.record(samplesFrame);
        }
        recorder.close();
        samplesGrabber.close();
        imageGrabber.close();
    }

    //返回带有地址和时长的音频信息，但是日志列表与版本号不在这里读取
    public static VideoImf getVideoImf(String path){
        try {
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(path);
            grabber.start();
            // 计算时长
            double durationInSec = grabber.getFormatContext().duration() / 1000000.0;
            return new VideoImf(path.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH),
                    durationInSec,-1,null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String video1 = "E:\\project\\test\\video\\luming.mp4";
        String video2 = "E:\\project\\test\\video\\luming_s.mp3";
        String video3 = "E:\\project\\test\\video\\luming_i.mp4";
        String video0 = "E:\\project\\test\\video\\luming_t.mp4";
        String video = "E:\\project\\test\\video\\luming_cut.mp4";
        videoAddText(video,video0,"create by 陈宇豪");
        //videoGetSamples(video1,video2);
        //videoGetImage(video1,video3);
        //videoMixImageAndSamples(video3,video2,video0);
        //videoClip(video1,video,5,10);
//
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new File(video1));
//        grabber.start();
//        int width = grabber.getImageWidth();
//        int height =  grabber.getImageHeight();
//        String format = grabber.getFormat();
//        String filterStr = "scale="+width+":"+height+"[out]";
//        FFmpegFrameFilter filter = new FFmpegFrameFilter(filterStr, grabber.getImageWidth(), grabber.getImageHeight());
//        filter.start();
//        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(new File(video0), width, height);
//        recorder.setFormat(format);
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//        recorder.start();
//        int idx = 0;
//        Frame frame0, frame;
//        while ((frame = grabber.grabImage()) != null) {
//            //filter.push(frame0);
//            //frame = filter.pullSamples();
//            //frame = filter.pullImage();
//            if (frame != null) {
//                recorder.record(frame);
//            }
//            if(idx++ > 1000){
//                break;
//            }
//        }
//        recorder.close();
//        filter.close();
//        grabber.close();
    }
}
