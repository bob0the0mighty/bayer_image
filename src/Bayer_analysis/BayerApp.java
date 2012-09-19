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
			create_smooth_img();
			smooth_avg += smooth_time;
		}
		for (int x = 0; x < 100; x++) {
			create_bayer_img();
			bayer_avg += bayer_time;
		}
		create_compared_img();
		smooth_avg = smooth_avg / 100;
		bayer_avg = bayer_avg / 100;
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
			image(compared_img, 0, 0);
			// save("compared.png");
			println("Press button to view first image");
			break;
		}
	}

	public void create_smooth_img() {
		smooth_time = millis();
		smooth_img = createImage(width, height, RGB);
		smooth_img.loadPixels();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				smooth_img.pixels[x + (y * width)] = color(x % 256, y % 256,
						(x + y) % 256);
			}
		}
		smooth_img.updatePixels();
		smooth_time = millis() - smooth_time;
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

	public void create_compared_img() {
		compared_img = createImage(width, height, RGB);
		compared_img.loadPixels();
		smooth_img.loadPixels();
		bayer_img.loadPixels();
		int dif, red_dif, green_dif, blue_dif;
		int smooth_color, bayer_color;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				smooth_color = smooth_img.pixels[x + (y * width)];
				bayer_color = bayer_img.pixels[x + (y * width)];
				red_dif = (int) (red(smooth_color) - red(bayer_color));
				green_dif = (int) (green(smooth_color) - green(bayer_color));
				blue_dif = (int) (blue(smooth_color) - blue(bayer_color));
				dif = (int) (red_dif + green_dif + blue_dif)/3;
				compared_img.pixels[x + (y * width)] = color((dif > 255) ? 255
						: dif);
			}
		}
		compared_img.updatePixels();
	}
	
	void create_demosaiced_img() {
		  demosaiced_img = createImage(width, height, RGB);
		  demosaiced_img.loadPixels();
		  bayer_img.loadPixels();
		  for (int x = 1; x < width-1; x++) {
		    for (int y = 1; y < height-1; y++) {
		      demosaiced_img.pixels[x + (y*width)] = createCompositeColor(x, y);
		    }
		  }
		  demosaiced_img.updatePixels();
		}

		//gets the color from the bayer filter pixels surrounding the center pixel and combines them. 
		//X: the x location of the center pixel
		//Y: the y location of the center pixel
		int createCompositeColor(int x, int y) {
		   int base = bayer_img.get(x, y);
		   int color_case;
		   int r = (base >> 16) & 0xFF; 
		   int g = (base >> 8) & 0xFF;  
		   int b = base & 0xFF;        
		   
		   //decide center color
		   if(y%2 == 0){//blue green row
		     if(x%2 == 0) {//blue
		       color_case = 0;
		     } else {//green case 1
		       color_case = 2;
		     }
		   } else {//green red row
		     if(x%2 == 0) {//green case 2
		       color_case = 3;
		     } else {//red
		       color_case = 1;
		     }
		   }
		   
		   int red_count   = (color_case == 1) ? 0 : (color_case != 0) ? 2 : 4;//red doesn't care, green has 2, blue has 4;
		   int green_count = (color_case < 3) ? 4 : 0; //red and blue have 4 surrounding green, green doesn't care
		   int blue_count  = (color_case == 0) ? 0 : (color_case != 1) ? 2 : 4;//blue doesn't care, green has 2, red has 4
		   
		   for(int j = x-1; j < x+2; j++){
			   for(int k = y-1; k < y+2; k++){
				   
			   }
		   }
		   
		}

	public void keyPressed() {
		key_count = (key_count + 1) % 3;
		redraw();
	}
}