void create_bayer_img() {
  bayer_time = millis();
  bayer_img = createImage(width, height, RGB);
  bayer_img.loadPixels();
  color c;
  for (int x = 0; x < width; x++) {
    for (int y = 0; y < height; y++) {
      if(y%2 == 0) { //if 0 or even row, blue then green
        c = (x%2 == 0)? color(0, 0, (x+y)%256): color(0, y%256, 0);//blue if x is 0 or even, green otherwise
      } else { //else red green row
        c = (x%2 == 0)? color(0, y%256, 0): color(x%256, 0, 0);//green if x is 0 or even, red otherwise
      }
      bayer_img.pixels[x + (y*width)] = c;
    }
  }
  bayer_img.updatePixels();
  bayer_time = millis() - bayer_time;  
}
