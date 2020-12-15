import java.io.*;

/**
 * <p>类的详细说明</p>
 *
 * @author lyloou
 * @author 其他作者姓名
 * @version 1.00 2020/12/15 , 星期二 lyloou 创建
 * <p>1.01 YYYY/MM/DD 修改者姓名 修改内容说明</p>
 */
public class SimpleTest {

    /**
     * // https://javamana.com/2020/11/202011122207326241.html
     * FFmpeg 会报错：
     * No such file or directory:"某视频文件下载URL"。
     * stackoverflow 上有人遇到了类似的问题：
     * FFMPEG “no such file or directory” on Android
     * I am trying to use the ffmpeg binary and call it via a native linux command in android. Most of the commands work fine but the problem is that when i need to pass an http url as an input to the -i option i get "No such file or directory" for the url. The url however is existing and running the SAME command on a mac does the job as expected.
     * <p>
     * 但最终没人给出正确的解决方案。
     * <p>
     * 为什么 terminal 执行正常的同一条命令行语句，Java 调用就挂了呢？看来 Java 并没有将程序员的意图良好地转达给底层。
     * <p>
     * 笔者经过多次测试，终于找到解决办法。既然 terminal 可以成功执行，启动 shell，然后自定义命令行作为参数传递给 shell 解释器。shell 知道如何将程序员的意图转达给底层。使用 sh -c，将自定义 CMD 行作为其参数，最后使用 java.lang.Runtimeexec(String[] cmdarray)：
     * <p>
     * String raw2flvCmd = "/usr/local/ffmpeg/bin/ffmpeg -i \"某视频文件下载URL\" -f flv /usr/userfile/ffmpeg/tempfile/1.flv";
     * Runtime.getRuntime().exec(new String[]{"sh","-c",raw2flvCmd});
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // final Process process = Runtime.getRuntime().exec("ffmpeg -i \"D:/data/video/汪汪队立大功第1季_第1集.mp4\" -ss 00:00:00 -to 00:01:12 \"D:/data/0112.mp4\"");
        final Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ffmpeg -i \"/data/video/汪汪队立大功第1季_第1集.mp4\" -ss 00:00:00 -to 00:01:12 \"/tmp/汪汪队立大功第1季_第1集_mp4/汪汪队立大功第1季_第1集_000000_0001123.mp4\""});
        InputStream inputStream = process.getInputStream();
        InputStream errorInputStream = process.getErrorStream();
        OutputStream outputStream = process.getOutputStream();
        //this.outputStream = process.getOutputStream();
        BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(errorInputStream));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        println(errorBufferedReader);
        println(bufferedReader);

        Thread.sleep(100000);
    }

    private static void println(BufferedReader errorBufferedReader) {
        new Thread(() -> {
            try {
                String str;
                while ((str = errorBufferedReader.readLine()) != null) {
                    System.out.println(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

}
