package Bayer_analysis;
  
import java.io.IOException;

import processing.core.*;

@SuppressWarnings("serial")
public class BayerApp extends PApplet {

	PImage smooth_img, bayer_img, compared_img, demosaiced_img;
	int key_count, smooth_time, bayer_time;
	float smooth_avg, bayer_avg;

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Bayer Comparision" });
	}

	public void setup() {
		size(512, 512);
		// int key_count = 0;
		// create color image, bayer_img, compared_img
		// benchmark smooth_img creation
		for (int x = 0; x < 100; x++) {
			smooth_img = create_smooth_img();
			smooth_avg += smooth_time;
		}
		for (int x = 0; x < 100; x++) {
			create_bayer_img();
			bayer_avg += bayer_time;
		}
		compared_img = create_compared_img(smooth_img, bayer_img);
		smooth_avg = smooth_avg / 100;
		bayer_avg = bayer_avg / 100;
		demosaiced_img = create_bilinear_img(bayer_img);
		noLoop();
	}

	public void draw() {
		background(0);
		println();
		switch (key_count) {
		case 0:// Draw initial image
			println("Image with RGB values for each pixel calculated by \n"
					+ "r = x%256, g = y%256, b = (x+y)%256)");
			image(smooth_img, 0, 0);
			// save("smooth.png");
			println("It averages " + smooth_avg + " ms to create this image");
			println("Press button to view next image");
			break;
		case 1:
			// Draw bayer image
			println("Bayer Pattern Image with R, G, or B values for each pixel calculated by \n"
					+ "r = x%256, g = y%256, b = (x+y)%256)");
			image(bayer_img, 0, 0);
			// save("bayer.jpeg");
			println("It averages " + bayer_avg + " ms to create this image");
			println("Press button to view next image");
			break;
		case 2:
			// Draw comparison image
			println("Greyscale Image created by comparing the smooth image and bayer image. \n"
					+ "Pixel values go from White for completely different to Black for completely the same.");
			image(demosaiced_img, 0, 0);
			// save("compared.png");
			println("Press button to view next image");
			break;
		case 3:
			// Draw comparison image
			println("Bilinear Interpolated Image created from bayer image.");
			compared_img = create_compared_img(bayer_img, demosaiced_img);
			image(compared_img, 0, 0);
			// save("compared.png");
			println("Press button to view first image");
			break;
		}
	}

	public PImage create_smooth_img() {
		smooth_time = millis();
		PImage img = createImage(width, height, RGB);
		img.loadPixels();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				img.pixels[x + (y * width)] = color(x % 256, y % 256,
						(x + y) % 256);
			}
		}
		img.updatePixels();
		smooth_time = millis() - smooth_time;
		return img;
	}

	public void create_bayer_img() {
		bayer_time = millis();
		bayer_img = createImage(width, height, RGB);
		bayer_img.loadPixels();
		int c;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (y % 2 == 0) { // if 0 or even row, blue then green
					c = (x % 2 == 0) ? color(0, 0, (x + y) % 256) : color(0,
							y % 256, 0);// blue if x is 0 or even, green
										// otherwise
				} else { // else red green row
					c = (x % 2 == 0) ? color(0, y % 256, 0) : color(x % 256, 0,
							0);// green if x is 0 or even, red otherwise
				}
				bayer_img.pixels[x + (y * width)] = c;
			}
		}
		bayer_img.updatePixels();
		bayer_time = millis() - bayer_time;
	}

	public PImage create_compared_img(PImage img1, PImage img2) {
		PImage img = createImage(width, height, RGB);
		img.loadPixels();
		img1.loadPixels();
		img2.loadPixels();
		int dif, red_dif, green_dif, blue_dif;
		int color1, color2;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				color1 = img1.pixels[x + (y * width)];
				color2 = img2.pixels[x + (y * width)];
				red_dif = (int) (red(color1) - red(color2));
				green_dif = (int) (green(color1) - green(color2));
				blue_dif = (int) (blue(color1) - blue(color2));
				dif = (int) (red_dif + green_dif + blue_dif)/3;
				img.pixels[x + (y * width)] = color((dif > 255) ? 255
						: dif);
			}
		}
		img.updatePixels();
		return img;
	}
	
	public PImage create_bilinear_img(PImage img_in) {
		  PImage img = createImage(width, height, RGB);
		  img.loadPixels();
		  img_in.loadPixels();
		  for (int x = 1; x < width-1; x++) {
		    for (int y = 1; y < height-1; y++) {
		      img.pixels[x + (y*width)] = createCompositeColor(x, y, img_in);
		    }
		  }
		  img.updatePixels();
		  return img;
		}

		//gets the color from the bayer filter pixels surrounding the center pixel and combines them. 
		//X: the x location of the center pixel
		//Y: the y location of the center pixel
		int createCompositeColor(int x, int y, PImage img) {
		   int base = img.get(x, y);
		   int r = (base >> 16) & 0xFF; 
		   int g = (base >> 8) & 0xFF;  
		   int b = base & 0xFF;        
		   int red_count, green_count, blue_count;
		   
		   boolean x_interface = (x == 0 || x == img.width ) ? true : false;
		   boolean y_interface = (y == 0 || y == img.height) ? true : false;
		   int interfaceNeg = ((x_interface) ? 1 : 0) - ((y_interface) ? 1 : 0);;
		   
		   //decides what color the center pixel is
		   if((y%2 == 0 && x%2 == 1) || (y%2 == 1 && x%2 == 0)){ //if green
			   red_count   = 2 - interfaceNeg;
			   green_count = 0;
			   blue_count  = 2 - interfaceNeg;
		   } else if(y%2 == 0 && x%2 == 0){ //blue
			   red_count   = 4 - interfaceNeg;
			   green_count = 4 - interfaceNeg;
		   	   blue_count  = 0;
		   } else {//red
			   red_count   = 0;
			   green_count = 4 - interfaceNeg;
		   	   blue_count  = 4 - interfaceNeg;
		   }
		   
		   //this grabs surrounding color
		   for(int j = x-1; j < x+2; j++){
			   for(int k = y-1; k < y+2; k++){
				   if(j == x && k == y)
					   continue;
				   int c = img.get(x, y);
				   if(red_count > 0)
					   r += (c >> 16) & 0xFF;
				   if(green_count > 0)
					   g += (c >> 8) & 0xFF;
				   if(blue_count > 0)
					   b += c & 0xFF;
			   }
		   }
		   
		   //averages color out
		   if(red_count > 0)
			   r = (int) (r / red_count);
		   if(green_count > 0)
			   g = (int) (g / green_count);
		   if(blue_count > 0)
			   b = (int) (b / blue_count);
		   
		   return this.color(r,g,b);
		}

	public void keyPressed() {
		key_count = (key_count + 1) % 4;
		redraw();
	}
}