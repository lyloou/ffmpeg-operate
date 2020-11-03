package org.mountcloud.ffmpeg;

import org.junit.Test;
import org.mountcloud.ffmepg.operation.FFOperationBase;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoConcat;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoCut;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoFormatM3u8;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoInfo;
import org.mountcloud.ffmepg.result.defaultResult.FFVideoInfoResult;
import org.mountcloud.ffmepg.task.bean.CutInfo;
import org.mountcloud.ffmepg.task.bean.FFVideoTask;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoConcatTask;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoCutTask;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoFormatM3u8Task;
import org.mountcloud.ffmepg.task.bean.tasks.FFMepgVideoInfoTask;
import org.mountcloud.ffmepg.task.context.FFTaskContext;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;


public class TestTask {
    @Test
    public void testCutAndConcat() throws InterruptedException {
        testCutAndConcat("C:\\Users\\lilou\\Desktop\\video\\制取氧气.mpg", 5);
    }

    @Test
    public void testConcat() {
        List<String> videoPathList = getList();
        FFMpegVideoConcat concat = new FFMpegVideoConcat(videoPathList);
        FFMepgVideoConcatTask task = new FFMepgVideoConcatTask(concat);
        FFTaskContext.getContext().addTask(task);

        monitor(task);
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


    private void testCutAndConcat(String inputPathname, int num) {

        TreeSet<CutInfo> cutInfos = getCutInfos(inputPathname, num);
        System.out.println(cutInfos);

        long t1 = System.currentTimeMillis();
        cut(cutInfos);
        long t2 = System.currentTimeMillis();

        List<String> collect = cutInfos.stream().map(CutInfo::getOutputPathname).collect(Collectors.toList());
        concat(collect);
        long t3 = System.currentTimeMillis();

        System.out.println("------------------------");
        System.out.println("system info: ");
        printSystemInfo();
        System.out.println("输入信息：" + getInputInfo(cutInfos));
        System.out.println("视频数量：" + collect.size());
        System.out.println("视频格式：" + getFormatInfo(cutInfos));
        System.out.println("剪切时间：" + (t2 - t1) + "ms");
        System.out.println("合并时间：" + (t3 - t2) + "ms");
        System.out.println("总时间：" + (t3 - t1) + "ms");
        System.out.println("------------------------");
    }

    private TreeSet<CutInfo> getCutInfos(String inputPathname, int num) {
        Time timeStart = Time.valueOf("00:00:10");
        Time timeEnd = Time.valueOf("00:00:20");
        int step = 20;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        TreeSet<CutInfo> cutInfos = new TreeSet<>();
        for (int i = 0; i < num; i++) {
            LocalTime start = timeStart.toLocalTime().plusSeconds(step * i);
            LocalTime end = timeEnd.toLocalTime().plusSeconds(step * i);
            cutInfos.add(new CutInfo(inputPathname, start.format(formatter), end.format(formatter)));
        }
        return cutInfos;
    }

    private static String getFormatInfo(TreeSet<CutInfo> cutInfos) {
        if (cutInfos.isEmpty()) {
            return null;
        }
        String inputPathname = cutInfos.first().getInputPathname();
        int dotIndex = inputPathname.indexOf(".");
        return inputPathname.substring(dotIndex);
    }

    // https://stackoverflow.com/questions/25552/get-os-level-system-information/61727
    private static void printSystemInfo() {
        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " +
                Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently available to the JVM */
        System.out.println("Total memory available to JVM (bytes): " +
                Runtime.getRuntime().totalMemory());

    }

    private static String getInputInfo(Collection<CutInfo> cutInfos) {
        StringBuilder sb = new StringBuilder();
        for (CutInfo cutInfo : cutInfos) {
            sb
                    .append("\n\t")
                    .append(cutInfo.getInputPathname())
                    .append("\t\t")
                    .append(cutInfo.getStartTime())
                    .append("\t")
                    .append(cutInfo.getEndTime());
        }
        return sb.toString();
    }

    private void concat(List<String> list) {
        System.out.println("【开始合并】" + list);
        FFMpegVideoConcat concat = new FFMpegVideoConcat(list);
        FFMepgVideoConcatTask task = new FFMepgVideoConcatTask(concat);
        FFTaskContext.getContext().addTask(task);
        monitor(task);
    }

    private void cut(Collection<CutInfo> cutInfos) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(cutInfos.size() + 1);

        for (CutInfo cutInfo : cutInfos) {
            FFMpegVideoCut cut = new FFMpegVideoCut();
            cut.setVideoFileName(cutInfo.getInputPathname());
            cut.setStartTime(cutInfo.getStartTime());
            cut.setEndTime(cutInfo.getEndTime());
            cut.setOutputFileName(cutInfo.getOutputPathname());
            System.out.println(cut.toString());

            FFMepgVideoCutTask task = new FFMepgVideoCutTask(cut, cyclicBarrier);
            FFTaskContext.getContext().addTask(task);
            new Thread(() -> {
                monitor(task);
            }).start();
        }

        try {
            System.out.println("【等待剪切】。。。。。。。。。。。。。。。。。。。");
            cyclicBarrier.await();
            System.out.println("【剪切完成】。。。。。。。。。。。。。。。。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
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

        monitor(task);
    }

    private static <T extends FFOperationBase> void monitor(FFVideoTask<T> task) {
        while (task.isRunning()) {
            System.out.println(task.getName() + "-" + task.getTaskId() + ": " + task.getProgress().getProgress());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

        monitor(task);
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
