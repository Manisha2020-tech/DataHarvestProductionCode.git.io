package com.audaharvest.utils;

import java.awt.image.BufferedImage;


import net.sourceforge.tess4j.Tesseract;

public class ImageToTextProjectApplication {

	static int count = 1;

	public static void getTextFromImage(BufferedImage processesImage) {

		// Set environment variable TESSDATA_PREFIX pointing to root directory of
		// project
		System.out.println("Output is below ::::::::::::::::::::: ");

		try {

			System.out.println(testDoOCR_SkewedImage(processesImage));
		} catch (Exception e) {

		}
	}

	public static String testDoOCR_SkewedImage(BufferedImage processesImage) throws Exception {

		Tesseract instance = new Tesseract();
		instance.setLanguage("eng");
		instance.setTessVariable("tessedit_char_whitelist", "soldSOLD");

		String result = instance.doOCR(processesImage);
		result = result.trim().replaceAll("\\s", "");
//		if (result.length() > 0) {
//			System.out.println(count + "     " + result);
//			count++;
//		}
		return result;
	}

}
