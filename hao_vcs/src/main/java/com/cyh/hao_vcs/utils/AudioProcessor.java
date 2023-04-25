package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.AudioImf;
import com.cyh.hao_vcs.entity.PicImf;
import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3;


public class AudioProcessor {

    /**
     * 裁剪音频
     * @param srcFile 原始音频
     * @param dstFile 保存地址
     * @param startMs 起始时间
     * @param endMs 终止时间
     * @throws Exception
     */
    public static void audioClip(String srcFile, String dstFile, int startMs, int endMs) throws Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(srcFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(dstFile, grabber.getAudioChannels());
        recorder.setSampleRate(grabber.getSampleRate());
        recorder.start();
        Frame frame;
        int startFrame = startMs * grabber.getSampleRate() / 1000;
        int endFrame = endMs * grabber.getSampleRate() / 1000;
        int frameNumber = 0;
        while ((frame = grabber.grabFrame()) != null) {
            if (frame.samples == null) {
                break;
            }
            if (frameNumber >= startFrame && frameNumber <= endFrame) {
                recorder.recordSamples(frame.samples);
            }
            if (frameNumber++ > endFrame) {
                break;
            }
        }
        grabber.stop();
        recorder.stop();
    }


    /**
     * 连接音频
     * @param srcFiles 待连接的音频地址列表，正序连接
     * @param dstFile 连接后的音频保存地址
     * @throws Exception
     */
    public static void audioConcat(List<String> srcFiles, String dstFile) throws Exception {
        List<FFmpegFrameGrabber> grabbers = new ArrayList<>();
        List<Integer> sampleRates = new ArrayList<>();
        for (String srcFile : srcFiles) {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(srcFile);
            grabber.start();
            grabbers.add(grabber);
            //采样帧率
            sampleRates.add(grabber.getSampleRate());
        }
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(dstFile, 2);
        recorder.setSampleRate(sampleRates.get(0));
        recorder.start();
        Frame frame;
        for (int i = 0; i < grabbers.size(); i++) {
            FFmpegFrameGrabber grabber = grabbers.get(i);
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.samples == null) {
                    break;
                }
                recorder.recordSamples(frame.samples);
            }
        }
        for (FFmpegFrameGrabber grabber : grabbers) {
            grabber.stop();
        }
        recorder.stop();
    }

    /**
     * 合成后会将原音频放慢至0.5倍速，时常为最短音频的两倍。输入时应该先将音频做2倍速处理，最好裁剪至相同时长保证合成效果。
     * @param inputFile1 左声道采样
     * @param inputFile2 右声道采样
     * @param outputFile 合成音乐
     * @throws FrameGrabber.Exception
     * @throws FFmpegFrameRecorder.Exception
     */
    public static void audioMix(String inputFile1, String inputFile2, String outputFile) throws Exception{
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(inputFile1);
        grabber1.start();
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(inputFile2);
        grabber2.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 2);
        recorder.setAudioCodec(AV_CODEC_ID_MP3);
        recorder.setAudioBitrate(128000);
        recorder.start();
        Frame frame1, frame2;
        while ((frame1 = grabber1.grabFrame()) != null && (frame2 = grabber2.grabFrame()) != null) {
            Frame mixedFrame = new Frame();
            mixedFrame.samples = new ShortBuffer[] {(ShortBuffer) frame1.samples[0], (ShortBuffer) frame2.samples[0]};
            mixedFrame.sampleRate = frame1.sampleRate;
            mixedFrame.audioChannels = 2;
            mixedFrame.opaque = frame1.opaque;
            mixedFrame.timestamp = Math.max(frame1.timestamp, frame2.timestamp);
            recorder.record(mixedFrame);
        }
        recorder.stop();
        grabber1.stop();
        grabber2.stop();
    }

    /**
     * 变速
     * @param inputFile
     * @param outputFile
     * @param speed 1.0为基准
     * @throws Exception
     */
    public static void audioChangeSpeed(String inputFile, String outputFile, double speed) throws Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getAudioChannels());
        recorder.setAudioCodec(grabber.getAudioCodec());
        recorder.setAudioBitrate(grabber.getAudioBitrate());
        recorder.start();
        String filter = String.format("atempo=%.2f", speed);
        FFmpegFrameFilter frameFilter = new FFmpegFrameFilter(filter, grabber.getAudioChannels());
        frameFilter.start();
        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            frameFilter.push(frame);
            Frame filteredFrame;
            while ((filteredFrame = frameFilter.pull()) != null) {
                recorder.record(filteredFrame);
            }
        }
        frameFilter.stop();
        recorder.stop();
        grabber.stop();
    }

    public static void audioChangeTone(String inputFile, String outputFile) throws Exception{

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getAudioChannels());
        recorder.setAudioCodec(grabber.getAudioCodec());
        recorder.setAudioBitrate(grabber.getAudioBitrate());
        recorder.start();

        String filter = String.format("asetrate=%d,atempo=%.2f", 88200,0.5f);
        FFmpegFrameFilter frameFilter = new FFmpegFrameFilter(filter, grabber.getAudioChannels());
        frameFilter.start();

        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            frameFilter.push(frame);
            Frame filteredFrame;
            while ((filteredFrame = frameFilter.pull()) != null) {
                recorder.record(filteredFrame);
            }
        }
        frameFilter.stop();
        recorder.stop();
        grabber.stop();
    }

    //返回带有地址和时长的音频信息，但是日志列表不在这里读取
    public static AudioImf getAudioImf(String path){
        try {
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(path);
            grabber.start();
            // 计算时长
            double durationInSec = grabber.getFormatContext().duration() / 1000000.0;
            return new AudioImf(path.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH),
                    durationInSec,-1,null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static void main(String[] args) {
        String audio1 = "E:\\project\\test\\audio\\Intro.mp3";
        String audio2 = "E:\\project\\test\\audio\\超跑女神.mp3";
        String audio0 = "E:\\project\\test\\audio\\test.mp3";
        List<String> list = new ArrayList<>();
        list.add(audio1);
        list.add(audio2);
        try {
            //audioConcat(list,audio0);
           // audioClip(audio1,audio0,5,15);
           // audioMix(audio1,audio2,audio0);
            //audioChangeSpeed(audio1,audio0,0.5);
            audioChangeTone(audio2,audio0);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            audioClip("E:\\project\\test\\video\\gotoWc.mp4","E:\\project\\test\\video\\test.mp4",
//                    0,2000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
