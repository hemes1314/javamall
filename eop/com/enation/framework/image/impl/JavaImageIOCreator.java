package com.enation.framework.image.impl;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.enation.framework.image.IThumbnailCreator;
import com.enation.framework.util.FileUtil;

/**
 * 使用javax image io生成缩略图
 * @author kingapex
 * 2010-7-10下午11:43:05
 */
public class JavaImageIOCreator implements IThumbnailCreator {
	private String srcFile;
	private String destFile;

	private static Map<String, String> extMap;
	static {
		extMap = new HashMap<String, String>(5);
		extMap.put("jpg", "JPEG");
		extMap.put("jpeg", "JPEG");
		extMap.put("gif", "GIF");
		extMap.put("png", "PNG");
		extMap.put("bmp", "BMP");

	}

	public JavaImageIOCreator(String sourcefile, String targetFile) {
		this.srcFile =sourcefile;
		this.destFile = targetFile;
	}

	public void resize(int w, int h) {
		String ext = FileUtil.getFileExt(srcFile).toLowerCase();
		
		BufferedImage image;
		try {
			Image img = Toolkit.getDefaultToolkit().getImage(srcFile);
			//image = ImageIO.read(new File(srcFile));
			image=FileUtil.toBufferedImage(img);
			ImageIO.write(Lanczos.resizeImage(image, w, h), ext, new File(destFile));
		} catch (IOException e) {
			 throw new RuntimeException("生成缩略图错误",e);
		}
	}

	public static void main(String args[]){
		JavaImageIOCreator creator = new JavaImageIOCreator("d:/1.jpg", "d:/1_j_180.jpg");
		creator.resize(180, 180);
	}
	
}
