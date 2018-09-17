package main;

import IO.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SystemTrayHandler {

    private final static String name = "Sleepy";
    private static TrayIcon trayIcon;
    private static MenuItem codeItem = new MenuItem("Code");

    public void start(){
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported on this platform");
            System.exit(1);
        }

        try {
            SystemTray sysTray = SystemTray.getSystemTray();
            trayIcon = createTrayIconFromFile();
            //trayIcon = createTrayIconFromResource();
            sysTray.add(trayIcon);
            trayIcon.displayMessage("Ready", "Tray icon started and tready", TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            System.out.println("Unable to add icon to the system tray");
            System.exit(1);
        }


    }

    public static void removeCodeOptionFromMenu(){
        trayIcon.getPopupMenu().remove(codeItem);
    }


    private static PopupMenu createTrayMenu() {
        ActionListener exitListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exiting");
                System.exit(0);
            }
        };

        ActionListener executeListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = "Activation Code";
                    String message = "Your activation code is: " + FileHandler.getValueFromXMLForKey("code");
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception error){
                    error.printStackTrace();
                }
            }
        };

        PopupMenu menu = new PopupMenu();

        codeItem.addActionListener(executeListener);
        menu.add(codeItem);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(exitListener);
        menu.add(exitItem);
        return menu;
    }

    private static TrayIcon createTrayIconFromFile() {
        Image image = Toolkit.getDefaultToolkit().getImage("icon.png");
        PopupMenu popup = createTrayMenu();
        TrayIcon ti = new TrayIcon(image, name, popup);
        ti.setImageAutoSize(true);
        return ti;
    }

    /**
     * Loading the image from the classpath
     * if in a folder in a jar, remember to add the folder!
     * ex. /img/realhowto.jpg
     */
    private static TrayIcon createTrayIconFromResource() throws java.io.IOException {
        ClassLoader cldr = SystemTrayHandler.class.getClassLoader();
        java.net.URL imageURL = cldr.getResource("C:\\Users\\Ross\\Desktop\\Computer_Sleep_Program\\icon.jpg");
        Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
        PopupMenu popup = createTrayMenu();
        TrayIcon ti = new TrayIcon(image, name, popup);
        ti.setImageAutoSize(true);
        return ti;
    }

    static Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }


}


