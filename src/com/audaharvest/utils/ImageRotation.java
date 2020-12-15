package com.audaharvest.utils;


import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
 
public class ImageRotation {
	public static BufferedImage rotate (String inputUrl ,int angleOfRotation) throws IOException
	{
		
		
		URL imageURL = new URL(inputUrl);
	    RenderedImage img = ImageIO.read(imageURL);
//		System.out.println("Rotating the original Image By: "+angleOfRotation+" degrees");
		BufferedImage processedImage=rotateMyImage(img, angleOfRotation);
//		System.out.println("...Done");

//		System.out.println("...Done");
		return processedImage;
	}
	
	public static BufferedImage rotate (RenderedImage img ,int angleOfRotation) throws IOException
	{
		BufferedImage processedImage=rotateMyImage(img, angleOfRotation);
		return processedImage;
	}

	public static BufferedImage rotateMyImage(RenderedImage img, double angle) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg =new BufferedImage(w, h, ((BufferedImage) img).getType());
		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.rotate(Math.toRadians(angle), w/2, h/2);

		g.drawImage((BufferedImage) img, null, 0, 0);
		return dimg;
	}

	/**
	 * This method reads an image from the file
	 * @param fileLocation -- > eg. "C:/testImage.jpg"
	 * @return BufferedImage of the file read
	 */
	public static BufferedImage readImage(String fileLocation) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	/**
	 * This method writes a buffered image to a file
	 * @param img -- > BufferedImage
	 * @param fileLocation --> e.g. "C:/testImage.jpg"
	 * @param extension --> e.g. "jpg","gif","png"
	 */
	public static void writeImage(BufferedImage img, String fileLocation,
			String extension) {
		try {
			BufferedImage bi = img;
			File outputfile = new File(fileLocation);
			ImageIO.write(bi, extension, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
