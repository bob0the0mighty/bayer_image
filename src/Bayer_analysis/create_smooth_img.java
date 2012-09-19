void create_smooth_img() {
  smooth_time = millis();
  smooth_img = createImage(width, height, RGB);
  smooth_img.loadPixels();
  for (int x = 0; x < width; x++) {
    for (int y = 0; y < height; y++) {
      smooth_img.pixels[x + (y*width)] = color(x%256, y%256, (x+y)%256);
    }
  }
  smooth_img.updatePixels();
  smooth_time = millis() - smooth_time;
}
