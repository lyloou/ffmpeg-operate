package org.mountcloud.ffmpeg;

import org.junit.Test;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoConcat;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoCut;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoFormatM3u8;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoInfo;
import org.mountcloud.ffmepg.result.defaultResult.FFVideoInfoResult;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoConcatTask;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoCutTask;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoFormatM3u8Task;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoInfoTask;
import org.mountcloud.ffmepg.task.context.FFTaskContext;

import java.util.Arrays;
import java.util.List;

import static org.mountcloud.ffmepg.util.FFTaskUtil.defaultMonitor;


public class TestTask {
    @Test
    public void testConcat() {
        List<String> videoPathList = getList();
        FFMpegVideoConcat concat = new FFMpegVideoConcat(videoPathList);
        FFMepgVideoConcatTask task = new FFMepgVideoConcatTask(concat);
        FFTaskContext.getContext().addTask(task);

        defaultMonitor(task);
    }

    private List<String> getList() {
        return Arrays.asList(
                "C:\\Users\\lilou\\Desktop\\video\\input_mp4\\input_000010_000020.mp4",
                "C:\\Users\\lilou\\Desktop\\video\\input_mp4\\input_000030_000040.mp4",
                "C:\\Users\\lilou\\Desktop\\video\\input_mp4\\input_000050_000100.mp4",
                "C:\\Users\\lilou\\Desktop\\video\\input_mp4\\input_000110_000120.mp4",
                "C:\\Users\\lilou\\Desktop\\video\\input_mp4\\input_000130_000140.mp4"
        );
    }


    @Test
    public void testCut() {
        FFMpegVideoCut cut = new FFMpegVideoCut();
        cut.setVideoFileName("C:\\Users\\lilou\\Desktop\\video\\out.mp4");
        cut.setStartTime("00:00:10");
        cut.setEndTime("00:00:22");
        cut.setOutputFileName("C:\\Users\\lilou\\Desktop\\video\\out1.mp4");
        System.out.println(cut.toString());

        FFMepgVideoCutTask task = new FFMepgVideoCutTask(cut, null);
        task.setName("taskCut");
        FFTaskContext.getContext().addTask(task);

        defaultMonitor(task);
    }

    @Test
    public void testM3u8() {
        FFMpegVideoFormatM3u8 m3u8Operation = new FFMpegVideoFormatM3u8();
        m3u8Operation.setVideoFileName("C:\\Users\\lilou\\Desktop\\video\\out.mp4");
        m3u8Operation.setBitrate("2048k");
        m3u8Operation.setTimes(5);
        m3u8Operation.setM3u8File("D:\\test\\m3u8\\test.m3u8");
        m3u8Operation.setTsFiles("D:\\test\\m3u8\\test%5d.ts");

        System.out.println(m3u8Operation.toString());

        FFMepgVideoFormatM3u8Task task = new FFMepgVideoFormatM3u8Task(m3u8Operation);

        FFTaskContext.getContext().addTask(task);

        defaultMonitor(task);
    }


    @Test
    public void testInfo() {
        FFVideoInfoResult result = new FFVideoInfoResult();

        FFMpegVideoInfo ffMpegVideoInfo = new FFMpegVideoInfo();
        ffMpegVideoInfo.setVideoUrl("C:\\Users\\lilou\\Desktop\\video\\aa.mp4");
        System.out.println(ffMpegVideoInfo);
        FFMepgVideoInfoTask videoInfoTask = new FFMepgVideoInfoTask(result, ffMpegVideoInfo);

        FFTaskContext.getContext().submit(videoInfoTask, null);

        System.out.println(result.getTimeLengthSec());
        System.out.println(result.getTimeLength());
        System.out.println(result.getStartTime());
        System.out.println(result.getBitrate());
        System.out.println(result.getWidth());
        System.out.println(result.getHeight());
        System.out.println(result.getFps());
        System.out.println(result.getTbr());
        System.out.println(result.getTbn());
        System.out.println(result.getTbc());
    }
}
