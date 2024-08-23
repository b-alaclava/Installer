//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Main extends JFrame {
    private JLabel statusLabel = new JLabel("Ready.");
    private JButton checkInstallButton = new JButton("Check Installation");

    public Main() {
        super("Balaclava Installer");
        this.checkInstallButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Main.this.checkInstallButton.getText().equals("Install")) {
                    Main.this.performUpdate();
                } else if (Main.this.checkInstallButton.getText().equals("Open Runelite")) {
                    Main.this.openRunelite();
                } else if (Main.this.checkJavaVersion()) {
                    Main.this.performDirectoryChecks();
                }

            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(this.checkInstallButton);
        panel.add(this.statusLabel);
        this.getContentPane().add(panel, "Center");
        this.setSize(400, 150);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo((Component)null);
    }

    private void performDirectoryChecks() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            protected Void doInBackground() throws Exception {
                this.publish(new String[]{"Performing directory checks..."});
                boolean directoriesExist = Main.this.checkDirectories();
                if (directoriesExist) {
                    this.publish(new String[]{"Directories found. Checking for updates..."});
                    Main.this.checkFiles();
                } else {
                    this.publish(new String[]{"Required directories not found"});
                }

                return null;
            }

            protected void process(List<String> chunks) {
                Iterator var2 = chunks.iterator();

                while(var2.hasNext()) {
                    String message = (String)var2.next();
                    Main.this.statusLabel.setText(message);
                }

            }
        };
        worker.execute();
    }

    private boolean checkDirectories() {
        boolean directoriesExist = true;
        File directory;
        if (System.getProperty("os.name").contains("Mac OS X")) {
            directory = new File("/Applications/RuneLite.app/Contents/Resources");
            if (!directory.exists()) {
                directoriesExist = false;
            }
        } else {
            directory = new File(System.getProperty("user.home") + "/AppData/Local/RuneLite");
            if (!directory.exists()) {
                directoriesExist = false;
            }
        }

        return directoriesExist;
    }

    private void checkFiles() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            protected Void doInBackground() throws Exception {
                this.publish(new String[]{"Checking for updates..."});
                boolean ethanVannInstallerExists = Main.this.checkEthanVannInstaller();
                boolean configNeedsUpdate = Main.this.checkConfigJson();
                if (ethanVannInstallerExists && !configNeedsUpdate) {
                    this.publish(new String[]{"No updates needed."});
                    if (System.getProperty("os.name").contains("Mac OS X")) {
                        Main.this.checkInstallButton.setText("Open Runelite");
                    }
                } else {
                    this.publish(new String[]{"Installation needed"});
                    Main.this.checkInstallButton.setText("Install");
                }

                return null;
            }

            protected void process(List<String> chunks) {
                Iterator var2 = chunks.iterator();

                while(var2.hasNext()) {
                    String message = (String)var2.next();
                    Main.this.statusLabel.setText(message);
                }

            }
        };
        worker.execute();
    }

    private void performUpdate() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            protected Void doInBackground() throws Exception {
                this.publish(new String[]{"Installing files..."});
                Main.this.downloadJar();
                this.publish(new String[]{"Modifying config..."});
                Main.this.modifyConfig();
                this.publish(new String[]{"Installation complete."});
                if (System.getProperty("os.name").contains("Mac OS X")) {
                    Main.this.checkInstallButton.setText("Open Runelite");
                } else {
                    Main.this.checkInstallButton.setText("Check Installation");
                }

                return null;
            }

            protected void process(List<String> chunks) {
                Iterator var2 = chunks.iterator();

                while(var2.hasNext()) {
                    String message = (String)var2.next();
                    Main.this.statusLabel.setText(message);
                }

            }
        };
        worker.execute();
    }

    private boolean checkEthanVannInstaller() {
        String jarFilePath = System.getProperty("os.name").contains("Mac OS X") ? "/Applications/RuneLite.app/Contents/Resources/Balaclava.jar" : System.getProperty("user.home") + "/AppData/Local/RuneLite/Balaclava.jar";
        File jarFile = new File(jarFilePath);
        return jarFile.exists();
    }

    private boolean checkConfigJson() {
        String configFilePath = System.getProperty("os.name").contains("Mac OS X") ? "/Applications/RuneLite.app/Contents/Resources/config.json" : System.getProperty("user.home") + "/AppData/Local/RuneLite/config.json";
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            return true;
        } else {
            try {
                InputStream inputStream = new FileInputStream(configFilePath);

                boolean var6;
                label79: {
                    boolean var15;
                    label78: {
                        try {
                            JSONTokener tokener = new JSONTokener(inputStream);
                            JSONObject object = new JSONObject(tokener);
                            if (!object.has("mainClass") || !"ca.arnah.runelite.LauncherHijack".equals(object.getString("mainClass"))) {
                                var6 = true;
                                break label79;
                            }

                            if (object.has("classPath")) {
                                JSONArray classPathArray = object.getJSONArray("classPath");
                                boolean ethanVannInstallerFound = false;
                                boolean runeliteJarFound = false;

                                for(int i = 0; i < classPathArray.length(); ++i) {
                                    String jarEntry = classPathArray.getString(i);
                                    if ("Balaclava.jar".equals(jarEntry)) {
                                        ethanVannInstallerFound = true;
                                    } else if ("RuneLite.jar".equals(jarEntry)) {
                                        runeliteJarFound = true;
                                    }
                                }

                                var15 = !ethanVannInstallerFound || !runeliteJarFound;
                                break label78;
                            }

                            var6 = true;
                        } catch (Throwable var12) {
                            try {
                                inputStream.close();
                            } catch (Throwable var11) {
                                var12.addSuppressed(var11);
                            }

                            throw var12;
                        }

                        inputStream.close();
                        return var6;
                    }

                    inputStream.close();
                    return var15;
                }

                inputStream.close();
                return var6;
            } catch (IOException var13) {
                var13.printStackTrace();
                return true;
            }
        }
    }

    private void modifyConfig() {
        try {
            this.downloadConfig();
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }

    private boolean checkJavaVersion() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            protected Void doInBackground() throws Exception {
                this.publish(new String[]{"Performing directory checks..."});
                boolean isJava11 = System.getProperty("java.specification.version").equals("11");
                if (isJava11) {
                    this.publish(new String[]{" Java version is correct. Checking files..."});
                } else {
                    this.publish(new String[]{" Java 11 required. Current version: " + System.getProperty("java.specification.version")});
                }

                return null;
            }

            protected void process(List<String> chunks) {
                Iterator var2 = chunks.iterator();

                while(var2.hasNext()) {
                    String message = (String)var2.next();
                    Main.this.statusLabel.setText(message);
                }

            }
        };
        worker.execute();
        String javaVersion = System.getProperty("java.specification.version");
        return javaVersion.equals("11");
    }

    private void downloadJar() throws IOException {
        URL url = new URL("https://github.com/b-alaclava/Hijack/releases/download/Host/Balaclava.jar");
        ReadableByteChannel channel = Channels.newChannel(url.openStream());
        String jarFilePath = System.getProperty("os.name").contains("Mac OS X") ? "/Applications/RuneLite.app/Contents/Resources/Balaclava.jar" : System.getProperty("user.home") + "/AppData/Local/RuneLite/Balaclava.jar";

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(jarFilePath);

            try {
                fileOutputStream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
            } catch (Throwable var12) {
                try {
                    fileOutputStream.close();
                } catch (Throwable var11) {
                    var12.addSuppressed(var11);
                }

                throw var12;
            }

            fileOutputStream.close();
        } finally {
            channel.close();
        }

    }

    private void downloadConfig() throws IOException {

        URL url = new URL("https://github.com/b-alaclava/Hijack/releases/download/Host/config.json");
        ReadableByteChannel channel = Channels.newChannel(url.openStream());
        String jarFilePath = System.getProperty("os.name").contains("Mac OS X") ? "/Applications/RuneLite.app/Contents/Resources/config.json" : System.getProperty("user.home") + "/AppData/Local/RuneLite/config.json";

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(jarFilePath);

            try {
                fileOutputStream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
            } catch (Throwable var12) {
                try {
                    fileOutputStream.close();
                } catch (Throwable var11) {
                    var12.addSuppressed(var11);
                }

                throw var12;
            }

            fileOutputStream.close();
        } finally {
            channel.close();
        }

    }

    private void openRunelite() {
        if (System.getProperty("os.name").contains("Mac OS X")) {
            try {
                String[] command = new String[]{"java", "-cp", "/Applications/RuneLite.app/Contents/Resources/Balaclava.jar:/Applications/RuneLite.app/Contents/Resources/RuneLite.jar", "ca.arnah.runelite.LauncherHijack"};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process var3 = processBuilder.start();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main app = new Main();
                app.setVisible(true);
            }
        });
    }
}
