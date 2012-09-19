void create_compared_img() {
  compared_img = createImage(width, height, RGB);
  compared_img.loadPixels();
  smooth_img.loadPixels();
  bayer_img.loadPixels();
  int dif, red_dif, green_dif, blue_dif;
  color smooth_color, bayer_color;
  for (int x = 0; x < width; x++) {
    for (int y = 0; y < height; y++) {
      smooth_color = smooth_img.pixels[x + (y*width)];
      bayer_color = bayer_img.pixels[x + (y*width)];
      red_dif = int(red(smooth_color)  - red(bayer_color));
      green_dif = int(green(smooth_color)  - green(bayer_color));
      blue_dif = int(blue(smooth_color)  - blue(bayer_color));
      dif = red_dif + green_dif + blue_dif;
      compared_img.pixels[x + (y*width)] = color((dif > 255)? 255 : dif);
    }
  }
  compared_img.updatePixels();
}
