import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.io.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.text.*;
import java.net.URLConnection;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MyTerminal
{
    JFrame frame;
    JPanel panel;
    JTextArea area;
    int printed=0;String str="";
    public static int signal=0;

    MyTerminal()throws Exception
    {
        frame=new JFrame();
        Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
        area=new JTextArea();
        panel=new JPanel();
        
        class Filter extends DocumentFilter 
        {
            public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)throws BadLocationException 
            {
                if (offset >= printed) 
                {
                    super.insertString(fb, offset, string, attr);
                }
            }
        
            public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException 
            {
                if (offset >= printed) 
                {
                    super.remove(fb, offset, length);
                }
            }
        
            public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs)throws BadLocationException 
            {
                if (offset >= printed) 
                {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        }

        ((AbstractDocument)area.getDocument()).setDocumentFilter(new Filter());

        JScrollPane scroll=new JScrollPane(area);
        DefaultCaret caret = (DefaultCaret)area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        frame.setSize(600,350);
        frame.setLocation(dim.width/2-frame.getWidth()/2,dim.height/2-frame.getHeight()/2);
        frame.add(scroll,BorderLayout.CENTER);
        frame.setVisible(true);
        area.setLineWrap(true);
        area.setBackground(Color.BLACK);
        Font font=new Font("Monospaced",Font.BOLD,12);
        area.setForeground(Color.GREEN);
        area.setFont(font);
        frame.setTitle("Java Terminal made by Arpit");
        area.setCaretColor(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        String msg="Fetching latest file ....\n";
        area.append(msg);
        printed=area.getText().length();

        URLConnection connection = new URL(
                "https://github.com/arpitjindal97/aircel-amt-bin/blob/master/download.txt?raw=true").openConnection();
        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter("\\Z");
        String content = scanner.next();

        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
        ArrayList<String> files_to_download = new ArrayList<>();
        while ((content = br.readLine()) != null) {

            files_to_download.add(content);
            (new File(content)).delete();
            saveUrl(new FileOutputStream(content), new URL("https://github.com/arpitjindal97/aircel-amt-bin/blob/master/"+content+"?raw=true"));
        }

        ProcessBuilder pb;
        pb=new ProcessBuilder("java","-cp",".","verify");
        
        pb.redirectErrorStream(true);
        Process pp=pb.start();


        BufferedWriter cmdout=new BufferedWriter(new OutputStreamWriter(pp.getOutputStream()));
        BufferedReader cmdbr=new BufferedReader(new InputStreamReader(pp.getInputStream()));
        Thread input=new Thread(new Runnable()
        {
            public void run()
            {
                int i=0;
                try
                {
                    while((i=cmdbr.read())!=-1)
                    {
                        area.append((char)i+"");
                        printed=area.getText().length();
                    }
                    //indicating process has been stopped
                    MyTerminal.signal=1;
                    area.append("Process completed press any key to exit.....");
                    printed=area.getText().length();
                    for(int w=0;w<files_to_download.size();w++)
                        (new File(files_to_download.get(w))).delete();
                }
                catch(IOException ioe)
                {
                    //indicating process has been stopped
                    MyTerminal.signal=1;
                }
            }
        });

        input.start();
      
        area.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if(MyTerminal.signal==1)
                    System.exit(0);
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        cmdout.newLine();
                        cmdout.flush();
                    }
                    catch(IOException eee)
                    {
                        System.exit(0);
                    }
                    return;
                }
                else if(e.getKeyCode() == KeyEvent.VK_C && ( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) )
                {
                    pp.destroy();
                    try
                    {
                        cmdout.close();
                    }
                    catch(IOException ioe)
                    {}
                    area.append("Process destroyed press any key to exit.....");
                    printed=area.getText().length();
                    
                    return;
                }
                else
                {
                    try
                    {
                        //System.out.println((int)e.getKeyChar());
                        cmdout.write(e.getKeyChar());
                    }
                    catch(IOException ioe)
                    {
                        System.exit(0);
                    }
                }
            }
        });


        pp.waitFor();
    }
    public static void main(String arg[])throws Exception
    {
        new MyTerminal();
    }
    public static void saveUrl(final FileOutputStream filename, final URL urlString)
            throws MalformedURLException, IOException
    {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try
        {
            in = new BufferedInputStream(urlString.openStream());
            fout = filename;

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1)
            {
                fout.write(data, 0, count);
            }
        } finally
        {
            if (in != null)
            {
                in.close();
            }
            if (fout != null)
            {
                fout.close();
            }
        }
    }
}