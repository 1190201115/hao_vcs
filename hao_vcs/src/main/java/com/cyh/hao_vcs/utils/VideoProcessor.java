package com.cyh.hao_vcs.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3;

public class VideoProcessor {

    /**
     * 抽取视频图像
     * @param inputFile
     * @param outputFile
     * @throws Exception
     */
    public static void videoGetImage(String inputFile, String outputFile) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new File(inputFile));
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(new File(outputFile), grabber.getImageWidth(), grabber.getImageHeight());
        recorder.setFormat(grabber.getFormat());
        recorder.setVideoCodec(grabber.getVideoCodec());//27
        recorder.setVideoBitrate(grabber.getVideoBitrate());//1991613
        recorder.setVideoQuality(25);
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
        recorder.setFormat(grabber.getFormat());

        recorder.setAudioBitrate(grabber.getAudioBitrate());//317768
        recorder.setSampleRate(grabber.getSampleRate());//48000

        recorder.start();
        Frame frame;
        while ((frame = grabber.grabSamples()) != null) {
            recorder.record(frame);
        }
        recorder.close();
        grabber.close();
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

    public static void main(String[] args) throws Exception {
        String video1 = "E:\\project\\test\\video\\luming.mp4";
        String video2 = "E:\\project\\test\\video\\luming_s.mp3";
        String video3 = "E:\\project\\test\\video\\luming_i.mp4";
        String video0 = "E:\\project\\test\\video\\luming_t.mp4";
        videoMixImageAndSamples(video3,video2,video0);
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
