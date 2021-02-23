package top.cnzrg.mysafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FileName: StreamUtil
 * Author: ZRG
 * Date: 2019/4/15 12:00
 */
public class StreamUtil {

    /**
     * 流转换成字符串
     *
     * @param inputStream 流对象
     * @return 流转换成字符串，null代表异常
     */
    public static String streamToString(InputStream inputStream) {
        // 1.在读取过程中，将读取的内容存储到缓存中，一次性转换成字符串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 2.读流的操作,读到没有为止
        byte[] buffer = new byte[1024];
        // 3.记录读取内容的临时变量
        int temp = -1;
        try {
            while ((temp = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, temp);
            }
            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
