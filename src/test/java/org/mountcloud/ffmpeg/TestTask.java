package org.mountcloud.ffmpeg;

import org.junit.Test;
import org.mountcloud.ffmepg.operation.ffmpeg.vidoe.FFMpegVideoCut;
import org.mountcloud.ffmepg.operation.ffmpeg.vidoe.FFMpegVideoFormatM3u8;
import org.mountcloud.ffmepg.operation.ffmpeg.vidoe.FFMpegVideoInfo;
import org.mountcloud.ffmepg.result.defaultResult.FFVideoInfoResult;
import org.mountcloud.ffmepg.task.bean.FFTaskStateEnum;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoCutTask;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoFormatM3u8Task;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoInfoTask;
import org.mountcloud.ffmepg.task.context.FFTaskContext;

public class TestTask {
    @Test
    public void testCut() {
        FFMpegVideoCut cut = new FFMpegVideoCut();
        cut.setVideoFileName("C:\\Users\\lilou\\Desktop\\video\\out.mp4");
        cut.setStartTime("00:00:10");
        cut.setEndTime("00:00:22");
        cut.setOutputFileName("C:\\Users\\lilou\\Desktop\\video\\out1.mp4");

        System.out.println(cut.toString());

        FFMepgVideoCutTask task = new FFMepgVideoCutTask(cut);

        FFTaskContext.getContext().addTask(task);

        while (isRunning(task)) {
            System.out.println(task.getProgress().getProgress());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isRunning(FFMepgVideoCutTask task) {
        switch (task.getProgress().getState()) {
            case FAILED:
            case COMPLETE:
                return false;
        }
        return true;
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

        while (!task.getProgress().getState().equals(FFTaskStateEnum.COMPLETE)){
            System.out.println(task.getProgress().getProgress());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
