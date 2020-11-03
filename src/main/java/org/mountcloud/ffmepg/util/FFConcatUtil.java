package org.mountcloud.ffmepg.util;

import org.mountcloud.ffmepg.excption.FFMpegOperationExcption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * <p>类的详细说明</p>
 *
 * @author lyloou
 * @author 其他作者姓名
 * @version 1.00 2020/11/3 , 星期二 lyloou 创建
 * <p>1.01 YYYY/MM/DD 修改者姓名 修改内容说明</p>
 */
public class FFConcatUtil {
    public static String generateTextFile(List<String> videoPathList, String outputVideoFilename) {
        String txtPath = outputVideoFilename + "_tmp.txt";
        try {
            File outputFile = new File(outputVideoFilename);
            if (outputFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                outputFile.delete();
            }
            // 将文件列表写入到 txt 文件
            FileOutputStream fos = new FileOutputStream(new File(txtPath));
            for (String path : videoPathList) {
                fos.write(("file '" + path + "'\r\n").getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FFMpegOperationExcption("生成txt文件失败");
        }

        return txtPath;
    }

    /**
     * TODO 改用文件生成策略（策略模式）
     * 通过一组视频文件路径列表，获取一个拼接后的文件路径
     *
     * @param videoPathList 视频文件路径列表
     * @return 拼接的文件路径
     */
    public static String getConcatFilename(List<String> videoPathList) {
        if (videoPathList == null || videoPathList.isEmpty()) {
            throw new FFMpegOperationExcption("视频路径列表无效");
        }
        String firstVideoPath = videoPathList.get(0);
        int indexSuffix = firstVideoPath.lastIndexOf(".");
        if (indexSuffix == -1) {
            throw new FFMpegOperationExcption("无效的视频格式，路径为：" + firstVideoPath);
        }

        String fileName = firstVideoPath.substring(0, indexSuffix);
        String suffix = firstVideoPath.substring(indexSuffix);

        return String.format(fileName + "_ALL_IN_ONE_%02d" + suffix, videoPathList.size());
    }
}
