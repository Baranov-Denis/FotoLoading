package Viewer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class MyButton extends JButton {

    private final Color hoverBackgroundColor = MyColors.BUTTON_HOVER;
    private final Color pressedBackgroundColor = MyColors.BUTTON_PRESSED;

    public MyButton(String text) {
        super(text);
        super.setContentAreaFilled(false);
        this.setPreferredSize(new Dimension(320, 41));
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(MyColors.BUTTON_COLOR);
        this.setForeground(MyColors.FONT);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setFocusPainted(false);
        this.setFont(MyFonts.myFont);

    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) {
            g.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g.setColor(hoverBackgroundColor);
        } else {
            g.setColor(getBackground());
        }
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public void setContentAreaFilled(boolean b) {
    }

}
