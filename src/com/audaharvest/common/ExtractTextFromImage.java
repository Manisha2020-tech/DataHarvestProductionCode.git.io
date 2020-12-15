package com.audaharvest.common;

import java.awt.image.BufferedImage;

import com.audaharvest.utils.ImageRotation;
import com.audaharvest.utils.ImageToTextProjectApplication;

public class ExtractTextFromImage {
	
	
	public  boolean getImageText (String inputUrl) throws Exception {
		
		boolean isSold = false;
		BufferedImage processedImage = null;
		for(int angle =0; angle <= 45 ; angle +=5) {
			if(ImageToTextProjectApplication.testDoOCR_SkewedImage(ImageRotation.rotate(inputUrl,angle)).toLowerCase().contains("sold")) {
				isSold = true;
				break;
			}
		}
		if(!isSold) {
			for(int angle = 300 ; angle < 360 ; angle += 5) {
				if(ImageToTextProjectApplication.testDoOCR_SkewedImage(ImageRotation.rotate(inputUrl,angle)).toLowerCase().contains("sold")) {
					isSold = true;
					break;
				}
			}
		}
		if(!isSold) {
			for(int angle =50; angle < 300 ; angle +=5) {
				if(ImageToTextProjectApplication.testDoOCR_SkewedImage(ImageRotation.rotate(inputUrl,angle)).toLowerCase().contains("sold")) {
					isSold = true;
					break;
				}
			}
		}
		if(isSold) {
			System.out.println("Vehicle is sold !!!");
		}else {
			System.out.println("Vehicle is not sold !!!");
		}
		
		return isSold;
		
	}
	
	
}