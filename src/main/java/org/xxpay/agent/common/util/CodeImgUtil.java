//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.xxpay.core.common.util.MyLog;

public class CodeImgUtil {
    private static final MyLog _log = MyLog.getLog(CodeImgUtil.class);
    private static List<Integer> sizeList = new ArrayList();
    private static final int IMAGE_WIDTH = 25;
    private static final int IMAGE_HEIGHT = 25;
    private static final int IMAGE_HALF_WIDTH = 12;
    private static final int FRAME_WIDTH = 2;
    private static MultiFormatWriter mutiWriter;

    public CodeImgUtil() {
    }

    public static List<Integer> getEwmSizeList() {
        return sizeList;
    }

    public static void encode(String content, int width, int height, String srcImagePath, String destImagePath, String fileName) {
        try {
            File dir = new File(destImagePath);
            _log.error("==================" + destImagePath, new Object[0]);
            _log.error("==================" + srcImagePath, new Object[0]);
            if (!dir.exists()) {
                _log.error("==================notExist", new Object[0]);
                boolean result = dir.mkdirs();
                _log.error("==================midirsResult" + result, new Object[0]);
            }

            ImageIO.write(genBarcode(content, width, height, srcImagePath), "jpg", new File(destImagePath + fileName));
        } catch (Exception var8) {
            _log.error("生成二维码出错", new Object[]{var8});
        }

    }

    private static BufferedImage genBarcode(String content, int width, int height, String srcImagePath) throws WriterException, IOException {
        BufferedImage scaleImage = scale(srcImagePath, 25, 25, false);
        int[][] srcPixels = new int[25][25];

        for(int i = 0; i < scaleImage.getWidth(); ++i) {
            for(int j = 0; j < scaleImage.getHeight(); ++j) {
                srcPixels[i][j] = scaleImage.getRGB(i, j);
            }
        }

        Hashtable hint = new Hashtable();
        hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hint.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = mutiWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hint);
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int[] pixels = new int[width * height];

        for(int y = 0; y < matrix.getHeight(); ++y) {
            for(int x = 0; x < matrix.getWidth(); ++x) {
                if (x > halfW - 12 && x < halfW + 12 && y > halfH - 12 && y < halfH + 12) {
                    pixels[y * width + x] = srcPixels[x - halfW + 12][y - halfH + 12];
                } else if ((x <= halfW - 12 - 2 || x >= halfW - 12 + 2 || y <= halfH - 12 - 2 || y >= halfH + 12 + 2) && (x <= halfW + 12 - 2 || x >= halfW + 12 + 2 || y <= halfH - 12 - 2 || y >= halfH + 12 + 2) && (x <= halfW - 12 - 2 || x >= halfW + 12 + 2 || y <= halfH - 12 - 2 || y >= halfH - 12 + 2) && (x <= halfW - 12 - 2 || x >= halfW + 12 + 2 || y <= halfH + 12 - 2 || y >= halfH + 12 + 2)) {
                    pixels[y * width + x] = matrix.get(x, y) ? -16777216 : 268435455;
                } else {
                    pixels[y * width + x] = 268435455;
                }
            }
        }

        BufferedImage image = new BufferedImage(width, height, 1);
        image.getRaster().setDataElements(0, 0, width, height, pixels);
        return image;
    }

    private static BufferedImage scale(String srcImageFile, int height, int width, boolean hasFiller) throws IOException {
        double ratio = 0.0D;
        URL url = new URL(srcImageFile);
        BufferedImage srcImage = ImageIO.read(url);
        Image destImage = srcImage.getScaledInstance(width, height, 4);
        if (srcImage.getHeight() > height || srcImage.getWidth() > width) {
            if (srcImage.getHeight() > srcImage.getWidth()) {
                ratio = (new Integer(height)).doubleValue() / (double)srcImage.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue() / (double)srcImage.getWidth();
            }

            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), (RenderingHints)null);
            destImage = op.filter(srcImage, (BufferedImage)null);
        }

        if (hasFiller) {
            BufferedImage image = new BufferedImage(width, height, 1);
            Graphics2D graphic = image.createGraphics();
            graphic.setColor(Color.white);
            graphic.fillRect(0, 0, width, height);
            if (width == ((Image)destImage).getWidth((ImageObserver)null)) {
                graphic.drawImage((Image)destImage, 0, (height - ((Image)destImage).getHeight((ImageObserver)null)) / 2, ((Image)destImage).getWidth((ImageObserver)null), ((Image)destImage).getHeight((ImageObserver)null), Color.white, (ImageObserver)null);
            } else {
                graphic.drawImage((Image)destImage, (width - ((Image)destImage).getWidth((ImageObserver)null)) / 2, 0, ((Image)destImage).getWidth((ImageObserver)null), ((Image)destImage).getHeight((ImageObserver)null), Color.white, (ImageObserver)null);
            }

            graphic.dispose();
            destImage = image;
        }

        return (BufferedImage)destImage;
    }

    public static String codeImgEncode(String filePath, String fileName, String info, int width, int height) throws WriterException, IOException {
        String format = "png";
        Map<EncodeHintType, Object> hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = (new MultiFormatWriter()).encode(info, BarcodeFormat.QR_CODE, width, height, hints);
        Path path = FileSystems.getDefault().getPath(filePath, fileName);
        File dir = new File(filePath);
        _log.error("==================" + filePath, new Object[0]);
        if (!dir.exists()) {
            _log.error("==================notExist", new Object[0]);
            boolean result = dir.mkdirs();
            _log.error("==================midirsResult" + result, new Object[0]);
        }

        MatrixToImageWriter.writeToPath(bitMatrix, format, path);
        return path.toString();
    }

    public static void writeQrCode(OutputStream stream, String info, int width, int height) throws WriterException, IOException {
        String format = "png";
        Map<EncodeHintType, Object> hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = (new MultiFormatWriter()).encode(info, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
    }

    public static void codeImgDecode() {
        String filePath = "D://zxing.png";

        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = (new MultiFormatReader()).decode(binaryBitmap, hints);
            JSONObject content = JSON.parseObject(result.getText());
            System.out.println("图片中内容：  ");
            System.out.println("author： " + content.getString("author"));
            System.out.println("zxing：  " + content.getString("zxing"));
            System.out.println("图片中格式：  ");
            System.out.println("encode： " + result.getBarcodeFormat());
        } catch (IOException var8) {
            var8.printStackTrace();
        } catch (NotFoundException var9) {
            var9.printStackTrace();
        }

    }

    static {
        sizeList.add(258);
        sizeList.add(344);
        sizeList.add(430);
        sizeList.add(860);
        sizeList.add(1280);
        mutiWriter = new MultiFormatWriter();
    }
}
