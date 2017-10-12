
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.net.HttpURLConnection;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
/*
Aircel AMT
*/

public class verify {

    public static void main(String[] args) throws Exception {
        String OS = System.getProperty("os.name");
        //if (!authenticate().equals("OK")) {
        //    return;
        //}

        Map<String, String> server_md5 = new LinkedHashMap();
        try {
            System.out.println("Checking for updates...");
            URLConnection connection = new URL(
                    "https://github.com/arpitjindal97/aircel_git_bin/blob/master/md5.md?raw=true").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            String content = scanner.next();

            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
            while ((content = br.readLine()) != null) {
                StringBuilder builder = new StringBuilder(content);

                String sum = builder.substring(0, builder.indexOf("  "));
                builder.delete(0, builder.indexOf(" ") + 2);

                server_md5.put(builder.toString(), sum);
                //System.out.println(file);
            }

        } catch (Exception ex) {

        }

        String classpath="libs/*:libs/";
        if (OS.contains("Window")) {
            server_md5.remove("libs/chromedriver_mac");
            server_md5.remove("libs/chromedriver_linux");
            classpath="libs/*;libs/";
        } else if (OS.contains("Mac")) {
            server_md5.remove("libs/chromedriver_win");
            server_md5.remove("libs/chromedriver_linux");
        } else {
            server_md5.remove("libs/chromedriver_mac");
            server_md5.remove("libs/chromedriver_win");
        }


        File f = new File("libs/");
        f.mkdir();
        String[] path = f.list();
        if (path != null)
            for (int i = 0; i < path.length; i++) {
                String ff = path[i];
                File tt = new File("libs/" + ff);
                if (tt.isDirectory()) {
                    continue;
                }
                String digest = getMD5Checksum(new FileInputStream(tt));

                // check if file exists
                if (server_md5.containsKey("libs/" + ff)) {
                    // file corrupt
                    if (!server_md5.get("libs/" + ff).equals(digest)) {
                        try {
                            System.out.println("Downloading libs/" + ff);
                            saveUrl(new FileOutputStream("libs/" + ff), new URL(
                                    "https://github.com/arpitjindal97/aircel_git_bin/raw/master/libs/" + ff + "?raw=true"));
                        } catch (Exception e) {
                            System.out.println("Problem with your Network !!!");

                        }
                        // repeat once more
                        i--;
                    }
                    // file is in good condition
                    else {
                        server_md5.remove("libs/" + ff);
                    }
                } else {
                    // file does not belong to us
                    System.out.println("Removed libs/" + ff);
                    (new File("libs/" + ff)).delete();
                }
            }

        for (Map.Entry<String, String> entry : server_md5.entrySet()) {
            System.out.println("Downloading " + entry.getKey());
            while (true) {
                try {
                    saveUrl(new FileOutputStream("" + entry.getKey()), new URL(
                            "https://github.com/arpitjindal97/aircel_git_bin/raw/master/" + entry.getKey() + "?raw=true"));

                    if (getMD5Checksum(new FileInputStream(entry.getKey())).equals(entry.getValue()))
                        break;
                } catch (Exception e) {
                    System.out.println("Problem with your Network !!!");

                }
            }
        }

        System.out.println("Done Updating files :)");
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-classpath", classpath, "amt");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        InputStream in = process.getInputStream();

        byte[] buffer = new byte[1024];
        while (true) {
            int r = in.read(buffer);
            if (r <= 0) {
                break;
            }
            System.out.write(buffer, 0, r);
        }
    }

    public static String getMD5Checksum(FileInputStream filename) throws NoSuchAlgorithmException, IOException {
        InputStream fis = filename;

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        byte[] b = complete.digest();
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static void saveUrl(final FileOutputStream filename, final URL urlString)
            throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(urlString.openStream());
            fout = filename;

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    public static String authenticate() throws Exception {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
            Map map = new LinkedHashMap();
            map.put(hostname, ip.getHostAddress());
            //System.out.println("Hostname: " + hostname);
            //System.out.println("HostAddress: " + ip.getHostAddress());
            while (enu.hasMoreElements()) {
                NetworkInterface network = enu.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac == null) {
                    continue;
                }
                if (mac == null) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                //System.out.println(network.getDisplayName() + "\t" + sb.toString());
                map.put(network.getDisplayName(), sb.toString());
            }
            URL url;
            try {
                String content = null;
                URLConnection connection = null;
                try {
                    connection = new URL("http://ph4nt0m.ml/Arpit1.php").openConnection();
                    Scanner scanner = new Scanner(connection.getInputStream());
                    scanner.useDelimiter("\\Z");
                    content = scanner.next();
                } catch (Exception ex) {

                }
                //System.out.println(content);
                //content="localhost:8080/amizone-1.0";
                url = new URL("http://" + content + "/Secure");
                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setDoOutput(true); // to be able to write.
                urlCon.setDoInput(true); // to be able to read.
                ObjectOutputStream out = new ObjectOutputStream(urlCon.getOutputStream());
                String software = "Aircel AMT";
                out.writeObject(software);
                out.writeObject(map);
                out.close();
                ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                String reply = (String) ois.readObject();
                ois.close();
                if (!reply.equals("OK")) {
                    System.out.println(reply);
                }
                return reply;
            } catch (Exception e) {

            }
        } catch (Exception ee) {
        }
        System.out.println("Problem occured while connecting to server");
        return "Problem occured while connecting to server";
    }
}
