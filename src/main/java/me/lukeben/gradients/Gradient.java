package me.lukeben.gradients;

import me.lukeben.utils.Methods;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public class Gradient {

    public static String applyGradients(String text, GradientType type, Color colorOne, Color colorTwo) {
        StringBuilder returnedText = new StringBuilder();
        String rawText = ChatColor.stripColor(Methods.color(text));
        char[] rawTextArray = rawText.toCharArray();
        double distance = 100 / rawTextArray.length;
        switch (type) {
            case RAINBOW:
                for(int i = 0; i < rawTextArray.length; i++) {
                    float val = (float) (distance * i) / 100;
                    ChatColor color = ChatColor.of(Color.getHSBColor(val, 1.0f, 1.0f));
                    returnedText.append(color);
                    returnedText.append(rawTextArray[i]);
                }
                break;
            case REVERSE_RAINBOW:
                for(int i = rawTextArray.length - 1; i > -1; i--) {
                    float val = (float) (distance * i) / 100;
                    ChatColor color = ChatColor.of(Color.getHSBColor(val, 1.0f, 1.0f));
                    returnedText.append(color);
                    returnedText.append(rawTextArray[rawTextArray.length - (i + 1)]);
                }
                break;
            case REGULAR:
                float intToFloatConst =  1f/255f;

                for(int i = 0; i < rawTextArray.length; i++) {

                    float fraction = (Float.valueOf(i) * (100 / Float.valueOf(rawTextArray.length))) / 100;

                    fraction = Math.min(fraction, 1f);
                    fraction = Math.max(fraction, 0f);

                    float redOne = colorOne.getRed() * intToFloatConst;
                    float redTwo = colorTwo.getRed() * intToFloatConst;
                    float deltaRed = redTwo - redOne;
                    float red = redOne + (deltaRed * fraction);

                    float blueOne = colorOne.getBlue() * intToFloatConst;
                    float blueTwo = colorTwo.getBlue() * intToFloatConst;
                    float deltaBlue = blueTwo - blueOne;
                    float blue = blueOne + (deltaBlue * fraction);

                    float greenOne = colorOne.getGreen() * intToFloatConst;
                    float greenTwo = colorTwo.getGreen() * intToFloatConst;
                    float deltaGreen = greenTwo - greenOne;
                    float green = greenOne + (deltaGreen * fraction);


                    red = Math.min(red, 1f);
                    red = Math.max(red,0f);

                    green = Math.min(green, 1f);
                    green = Math.max(green,0f);
                    blue = Math.min(blue, 1f);

                    blue = Math.max(blue, 0f);

                    Color newColor = new Color(red, green, blue);
                    returnedText.append(ChatColor.of(newColor));
                    returnedText.append(rawTextArray[i]);
                }
                break;
        }
        return returnedText.toString();
    }

}
