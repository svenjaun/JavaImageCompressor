package dev.jaun.JavaImageCompressor;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {

    private Main(String[] args) throws IOException {
        String st = getBase64String();
        if (st == null) {
            System.out.println("create file named image.txt with the base64 string in it");
            System.exit(1);
        }
        String extension = getExtension(st);
        BufferedImage bufferedImage = convertStringToImage(st);
        bufferedImage = (extension.startsWith("png")) ? convertPngToJpg(bufferedImage) : bufferedImage;

        if (bufferedImage.getWidth() > 3000) {
            int scaleX = (int) (bufferedImage.getWidth() / 1.5);
            int scaleY = (int) (bufferedImage.getHeight() / 1.5);
            Image image = bufferedImage.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
            bufferedImage = new BufferedImage(scaleX, scaleY, bufferedImage.getType());
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        }

        // TODO: Is compression needed?

        File output = new File("image.jpg");
        ImageIO.write(bufferedImage, "jpg", output);
    }

    public static void main(String[] args) throws IOException {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("/?")) {
                System.out.println("create file named image.txt with the base64 string in it");
                System.exit(0);
            }
        }
        new Main(args);
    }

    private static String getBase64String() {
        Path currentRelativePath = Paths.get("");
        String currentPath = currentRelativePath.toAbsolutePath().toString();
        File imageString = new File(currentPath + "\\image.txt");

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(imageString));
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private String getExtension(String st) {
        String base64Properties = st.split(",")[0];
        String extension;
        switch (base64Properties) {
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default:// should write cases for more images types
                extension = "jpg";
                break;
        }
        return extension;
    }

    private BufferedImage convertPngToJpg(BufferedImage image) {
        BufferedImage result = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        return result;
    }

    private BufferedImage convertStringToImage(String st) throws IOException {
        String base64String = st.split(",")[1];

        byte[] imageByte;
        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(base64String);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        BufferedImage bufferedImage = ImageIO.read(bis);
        bis.close();
        return bufferedImage;
    }
}
