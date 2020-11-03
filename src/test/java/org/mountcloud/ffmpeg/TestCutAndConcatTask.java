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


public class TestCutAndConcatTask {
    @Test
    public void testCutAndConcat() throws InterruptedException {
        testCutAndConcat("C:\\Users\\lilou\\Desktop\\video\\制取氧气.mpg", 4);
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
        task.setName("taskCutAndConcat-Concat");
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
            task.setName("taskCutAndConcat-Cut");
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
}
