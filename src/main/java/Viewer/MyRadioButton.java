package Viewer;

import javax.swing.*;
import java.awt.*;

public class MyRadioButton extends JRadioButton {

    public MyRadioButton(String text){
        super(text);
        this.setPreferredSize(new Dimension(156,41));
        this.setBackground(MyColors.BUTTON_COLOR);
        this.setForeground(MyColors.FONT);
        this.setFont(MyFonts.myFont);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
