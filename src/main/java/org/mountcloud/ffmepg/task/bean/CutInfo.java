package org.mountcloud.ffmepg.task.bean;

import java.io.File;

/**
 * <p>类的详细说明</p>
 *
 * @author lyloou
 * @author 其他作者姓名
 * @version 1.00 2020/10/28 , 星期三 lyloou 创建
 * <p>1.01 YYYY/MM/DD 修改者姓名 修改内容说明</p>
 */
public class CutInfo implements Comparable<CutInfo> {
    // C:/Users/lyloou/Desktop/video/input.mp4
    String inputPathname;
    // 00:01:10
    String startTime;
    // 00:01:25
    String endTime;

    public CutInfo(String inputPathname, String startTime, String endTime) {
        this.inputPathname = inputPathname;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getInputPathname() {
        return inputPathname;
    }

    public void setInputPathname(String inputPathname) {
        this.inputPathname = inputPathname;
    }

    public String getOutputFileName() {
        File inputFile = getInputFile();
        String inputFileName = inputFile.getName();
        int indexSuffix = inputFileName.lastIndexOf(".");
        if (indexSuffix == -1) {
            throw new IllegalArgumentException("无效的inputPathname参数:" + inputPathname);
        }

        String fileName = inputFileName.substring(0, indexSuffix);
        String suffix = inputFileName.substring(indexSuffix);
        String outputName = fileName + "_" + startTime + "_" + endTime + suffix;
        return outputName.replaceAll(":", "");
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public File getInputFile() {
        return new File(inputPathname);
    }

    public File getOutputFile() {
        String parent = getInputFile().getParent() + File.separator + getInputFile().getName().replace(".", "_");
        File file = new File(parent);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return new File(parent, getOutputFileName());
    }

    public String getOutputPathname() {
        return getOutputFile().getAbsolutePath();
    }

    @Override
    public int compareTo(CutInfo o) {
        if (o == null) {
            return -1;
        }
        return getOutputFileName().compareTo(o.getOutputFileName());
    }

    @Override
    public String toString() {
        return "CutInfo{" +
                "inputPathname='" + inputPathname + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
