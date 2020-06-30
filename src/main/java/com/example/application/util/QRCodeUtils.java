package com.example.application.util;

import com.example.application.exceptionn.BasicException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
/**
 * QRCode生成工具类
 */
@Slf4j
public class QRCodeUtils {

    private static String note = "获取二维码出错，请刷新后重试或联系管理员.";

    /**
     * 二维码BufferedImage对象生成方法
     * @param contents 二维码内容
     * @param width 二维码图片宽度
     * @param height 二维码图片高度
     * @param margin 二维码边框(0,2,4,8)
     * @throws Exception
     * @return: BufferedImage
     */
    public static BufferedImage createQRCode(String contents, int width, int height,int margin) throws BasicException {
        if (contents == null || contents.equals("")) {
            log.error("生成二维码出错,生成的内容不存在");
            throw new BasicException(note+"错误码[1001]");
        }
        // 二维码基本参数设置
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8);// 设置编码字符集utf-8
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);// 设置纠错等级L/M/Q/H,当二维码被损毁一部分时，纠错等级越高，越可能读取成功；同样的，纠错等级越高，单位面积内点阵的点越多，机器扫描时，识别所需时间越长，当前设置等级为最高等级H
        hints.put(EncodeHintType.MARGIN, margin);// 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
        // 生成图片类型为QRCode
        BarcodeFormat format = BarcodeFormat.QR_CODE;
        // 创建位矩阵对象
        BitMatrix matrix = null;
        try {
            // 生成二维码对应的位矩阵对象
            matrix = new MultiFormatWriter().encode(contents, format, width, height, hints);
        } catch (WriterException e) {
            log.error(e.getMessage(),e);
            log.error("生成二维码出错.[1002]");
            throw new BasicException(note+"错误码[1002]");

        }
        // 设置位矩阵转图片的参数
        MatrixToImageConfig config = new MatrixToImageConfig(Color.black.getRGB(), Color.white.getRGB());
        // 位矩阵对象转BufferedImage对象
        BufferedImage qrCode = MatrixToImageWriter.toBufferedImage(matrix, config);
        return qrCode;
    }

    /**
     * 二维码添加LOGO
     * @param qrCode
     * @param width 二维码图片宽度
     * @param height 二维码图片高度
     * @param logoPath 图标LOGO路径
     * @param logoSizeMultiple 二维码与LOGO的大小比例
     * @throws Exception
     * @return: BufferedImage
     */
    public static BufferedImage createQRCodeWithLogo(BufferedImage qrCode,int width, int height, String logoPath, int logoSizeMultiple) throws BasicException {
        File logoFile = new File(logoPath);
        if (!logoFile.exists() && !logoFile.isFile()) {
            log.error("生成二维码时出错,指定的LOGO图片路径不存在！[1003]");
            throw new BasicException(note+"错误码[1003]");
        }
        try {
            // 读取LOGO
            BufferedImage logo = ImageIO.read(logoFile);
            // 设置LOGO宽高
            int logoHeight = qrCode.getHeight()/logoSizeMultiple;
            int logWidth = qrCode.getWidth()/logoSizeMultiple;
            // 设置放置LOGO的二维码图片起始位置
            int x = (qrCode.getWidth() - logWidth)/2;
            int y = (qrCode.getHeight() - logoHeight)/2;
            // 新建空画板
            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 新建画笔
            Graphics2D g = (Graphics2D) combined.getGraphics();
            // 将二维码绘制到画板
            g.drawImage(qrCode, 0, 0, null);
            // 设置不透明度，完全不透明1f,可设置范围0.0f-1.0f
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            // 绘制LOGO
            g.drawImage(logo, x, y, logWidth, logoHeight, null);
            return combined;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.error("生成二维码出错[1004]");
            throw new BasicException(note+"错误码[1004]");
        }
    }

    /**
     * 导出到指定路径
     * @param bufferedImage
     * @param filePath 图片保存路径
     * @param fileName 图片文件名
     * @param formatName 图片格式
     * @return: boolean
     */
    public static boolean generateQRCodeToPath(BufferedImage bufferedImage,String filePath, String fileName, String formatName) throws BasicException {
        // 判断路径是否存在，不存在则创建
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        // 路径后补充斜杠
        if (filePath.lastIndexOf("\\") != filePath.length() - 1) {
            filePath = filePath + "\\";
        }
        // 组合为图片生成的全路径
        String fileFullPath = filePath + fileName + "." + formatName;
        boolean result = false;
        try {
            // 输出图片文件到指定位置
            result = ImageIO.write(bufferedImage, formatName, new File(fileFullPath));
        } catch (IOException e) {
            log.error("导出二位码出错[1005]");
            throw new BasicException(note+"[1005]");
        }
        return result;
    }



    /**
     * BufferedImage 编码转换为 base64
     * @param bufferedImage
     * @return
     */
    public static String BufferedImageToBase64(BufferedImage bufferedImage) throws BasicException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();//io流
        try {
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);//写入流中
        } catch (IOException e) {
            log.error("二维码转换出错失败[1005]");
            log.error(e.getMessage(),e);
            throw new BasicException(note+"[1005]");
        }
        // 转换成字节
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedText = encoder.encodeToString(bytes);
        encodedText = encodedText.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        return "data:image/jpg;base64," + encodedText;
    }

    /**
     * base64 编码转换为 BufferedImage
     * @param base64
     * @return
     */
    public static BufferedImage base64ToBufferedImage(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            byte[] bytes1 = decoder.decode(base64);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);
            return ImageIO.read(bais);
        } catch (IOException e) {
            log.error("二维码转换出错[1006]");
            log.error(e.getMessage(),e);
        }
        return null;
    }
}