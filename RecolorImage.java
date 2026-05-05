import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class RecolorImage {
    public static void main(String[] args) {
        try {
            File inputFile = new File(args[0]);
            File outputFile = new File(args[1]);
            
            BufferedImage image = ImageIO.read(inputFile);
            int width = image.getWidth();
            int height = image.getHeight();
            
            // Target Blue: #2196F3
            float targetHue = 207f / 360f; // Approx 0.575
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgba = image.getRGB(x, y);
                    int alpha = (rgba >> 24) & 0xFF;
                    int r = (rgba >> 16) & 0xFF;
                    int g = (rgba >> 8) & 0xFF;
                    int b = rgba & 0xFF;
                    
                    if (alpha == 0) continue; // Skip transparent
                    
                    float[] hsb = Color.RGBtoHSB(r, g, b, null);
                    float hue = hsb[0];
                    float sat = hsb[1];
                    float bri = hsb[2];
                    
                    // Pink/Magenta is around hue 280-340 degrees (0.77 - 0.94)
                    // The 'Omis' text in the image is distinctly magenta.
                    // Let's check if hue is in the pink/magenta range and saturation is significant
                    if ((hue > 0.75f || hue < 0.05f) && sat > 0.2f) {
                        // Shift hue to our target blue
                        int newRgb = Color.HSBtoRGB(targetHue, sat, bri);
                        // Preserve original alpha
                        newRgb = (alpha << 24) | (newRgb & 0x00FFFFFF);
                        image.setRGB(x, y, newRgb);
                    }
                }
            }
            
            ImageIO.write(image, "png", outputFile);
            System.out.println("Image recolored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
