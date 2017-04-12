import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class start
{
	public static void main(String arg[])
	{
		try
		{
			/*URLConnection connection = null;
			connection = new URL("http://amizone-arpitjindal.rhcloud.com/Update").openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			String url_to_verify_file = scanner.next();*/
			
			//download the verify file
			(new File("verify.class")).delete();
			saveUrl(new FileOutputStream("verify.class"),new URL("https://github.com/arpitjindal97/aircel_git_bin/blob/master/verify.class?raw=true"));
			
			ProcessBuilder processBuilder = new ProcessBuilder("java","verify");
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
	        
			
		} catch (Exception ex)
		{

		}
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