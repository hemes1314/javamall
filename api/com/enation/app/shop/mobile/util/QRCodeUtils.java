package com.enation.app.shop.mobile.util;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类.
 * 
 * @author baoxiufeng
 */
public class QRCodeUtils {

	private static final String charset = "UTF-8";
	
	/** 
     * 生成二维码图片.
     * 
     * @param qrCodeData 原文字 
     * @param qrCodeheight 高度 
     * @param qrCodewidth 宽度 
     * @return 二维码图片对象
     */  
    public static BufferedImage createImage(String qrCodeData, int qrCodeheight, int qrCodewidth) {  
        Map<EncodeHintType, Object> hintMap = new HashMap<EncodeHintType, Object>();  
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);	// 容错率  
        hintMap.put(EncodeHintType.MARGIN, 0);	// 外边距  
        BitMatrix matrix = null;  
        try {  
            matrix = new MultiFormatWriter().encode(  
                    new String(qrCodeData.getBytes(charset), charset),  
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);  
        } catch (Exception e) {  
        	e.printStackTrace();
        }  
        return MatrixToImageWriter.toBufferedImage(matrix);  
    } 
}
