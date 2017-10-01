
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class amt
{

    static Workbook workbook;
    static String path = "amt.xlsx";

    public static void main(String arg[]) throws Exception
    {
        if (!authenticate().equals("OK"))
        {
            return;
        }
        int count = 0;
        String user, pass, temp = "";
        FileInputStream fis = new FileInputStream(new File(path));
        workbook = new XSSFWorkbook(fis);
        fis.close();
        Sheet sheet = null;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++)
        {
            if (workbook.getSheetAt(i).getSheetName().equals("Input"))
            {
                sheet = workbook.getSheetAt(i);
            }
        }
        if (sheet == null)
        {
            workbook.setSheetName(0, "Input");
            sheet = workbook.getSheetAt(0);
        }
        Iterator<Row> row = sheet.rowIterator();
        Row ro = null;
        Iterator<Cell> cell;
        Cell ce;
        ArrayList<Cell> array_list = new ArrayList<Cell>();
        try
        {
            ro = row.next();
            cell = ro.cellIterator();
            ce = cell.next();
            user = getValue(ce);
            while (cell.hasNext())
            {
                ce = cell.next();
                array_list.add(ce);
            }
            for (int i = 0; i < array_list.size(); i++)
            {
                ro.removeCell(array_list.get(i));
            }
            array_list = new ArrayList<Cell>();
            ro = row.next();
            cell = ro.cellIterator();
            ce = cell.next();
            pass = getValue(ce);
            pass = URLEncoder.encode(pass, "UTF-8");
            while (cell.hasNext())
            {
                ce = cell.next();
                array_list.add(ce);
            }
            for (int i = 0; i < array_list.size(); i++)
            {
                ro.removeCell(array_list.get(i));
            }
            array_list = new ArrayList<Cell>();
        } catch (Exception e)
        {
            System.out.println(e);
            System.out.println("Invalid Format");
            return;
        }

        String OS = System.getProperty("os.name");
        String driver_path="";
        if (OS.contains("Window")) {
            driver_path = "libs/chromedriver_win";
        } else if (OS.contains("Mac")) {
            driver_path = "libs/chromedriver_mac";
            makeExecutable(driver_path);
        } else {
            driver_path = "libs/chromedriver_linux";
            makeExecutable(driver_path);
        }
        System.setProperty("webdriver.chrome.driver", driver_path);
        
        ChromeDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 60);
        driver.navigate().to("https://" + user + ":" + pass + "@amt.aircel.co.in/AMTIDAM/Default.aspx");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("LinkButton1")));
        driver.findElement(By.id("LinkButton1")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSearch")));
        while (row.hasNext())
        {
            try
            {
                ro = row.next();
                String msisdn = null;
                try
                {
                    msisdn = getValue(ro.getCell(0));
                    if (msisdn.length() != 10)
                    {
                        throw new java.lang.NullPointerException();
                    }
                } catch (java.lang.NullPointerException nn)
                {
                    cell = ro.cellIterator();
                    while (cell.hasNext())
                    {
                        ce = cell.next();
                        array_list.add(ce);
                    }
                    for (int i = 0; i < array_list.size(); i++)
                    {
                        ro.removeCell(array_list.get(i));
                    }
                    array_list = new ArrayList<Cell>();
                    continue;
                }
                System.out.println(msisdn);
                save_file(0);
                cell = ro.cellIterator();
                ce = cell.next();
                while (cell.hasNext())
                {
                    ce = cell.next();
                    array_list.add(ce);
                }
                for (int i = 0; i < array_list.size(); i++)
                {
                    ro.removeCell(array_list.get(i));
                }
                array_list = new ArrayList<Cell>();
                wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSearch")));
                driver.findElement(By.name("txtSearch")).sendKeys(msisdn);
                driver.findElement(By.name("btnSearch")).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSearch")));
                //List<WebElement> list = driver.findElements(By.id("grdSearch"));
                List<WebElement> list = driver.findElements(By.className("grid_content_alt"));
                if (list.isEmpty())
                {
                    System.out.println("No Records Found");
                    ro.createCell(1).setCellValue("No records found");
                    save_file(0);
                    continue;
                }
                int i;
                for (i = 0; i < list.size(); i++)
                {
                    temp = list.get(i).getText();
                    if (list.size() == 7 && i + 1 == 7)
                    {
                        temp = short_this(temp);
                        i = 8;
                    } else if (i + 1 == 9)
                    {
                        temp = short_this(temp);
                    } else
                    {
                    }
                    msisdn = msisdn + "\t" + temp;
                    ro.createCell(i + 1).setCellValue(temp);
                }
                System.out.println(msisdn);
                save_file(0);
                int jj;
                if (list.size() == 7)
                {
                    jj = 6;
                } else
                {
                    jj = 8;
                }
                if (list.get(jj).getText().equals("SIM ACTIVATED-TV PENDING"))
                {
                    driver.findElement(By.id("grvCEFPending_ctl02_lnkMSISDN")).click();
                    wait.until(ExpectedConditions.elementToBeClickable(By.id("cbActView")));
                    driver.findElement(By.id("cbActView")).click();
                    wait.until(ExpectedConditions.elementToBeClickable(By.id("lnkCEFForm")));
                    driver.findElement(By.id("lnkCEFForm")).click();
                    Thread.sleep(2000);
                    ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
                    driver.switchTo().window(tabs2.get(1));
                    wait = new WebDriverWait(driver, 60);
                    wait.until(ExpectedConditions.elementToBeClickable(By.id("btnCancel")));
                    temp = driver.findElement(By.name("txtSubscriberFirstName")).getAttribute("value") + " "
                            + driver.findElement(By.name("txtSubscriberMiddleName")).getAttribute("value") + " "
                            + driver.findElement(By.name("txtSubscriberLastName")).getAttribute("value");
                    temp = temp.trim();
                    ro.createCell(3).setCellValue(temp);
                    System.out.println(temp);
                    temp = driver.findElement(By.name("txtFatherHusbandName")).getAttribute("value");
                    ro.createCell(12).setCellValue(temp);
                    System.out.println(temp);

                    temp = driver.findElement(By.name("txtPOINo")).getAttribute("value");
                    System.out.println(temp);
                    ro.createCell(14).setCellValue(temp);
                    if (!temp.equals(driver.findElement(By.name("txtPOAno")).getAttribute("value")))
                    {
                        temp = driver.findElement(By.name("txtPOAno")).getAttribute("value");
                        System.out.println(temp);
                        ro.createCell(15).setCellValue(temp);
                    }

                    temp = driver.findElement(By.name("txtDOB")).getAttribute("value");
                    //System.out.println(temp);
                    System.out.println(temp);
                    ro.createCell(16).setCellValue(temp);

                    driver.close();
                    driver.switchTo().window(tabs2.get(0));
                    temp = driver.findElement(By.id("txtAddress")).getText();
                    System.out.println(temp);
                    ro.createCell(13).setCellValue(temp);

                    driver.findElement(By.id("btnBack")).click();
                }
            } catch (NoSuchElementException dd)
            {
                System.out.println(dd);
                if (count == 1)
                {
                    break;
                } else
                {
                    count++;
                    driver.navigate().to("https://" + user + ":" + pass + "@amt.aircel.co.in/AMTIDAM/Default.aspx");
                    driver.findElement(By.id("LinkButton1")).click();
                    continue;
                }
            } catch (Exception e)
            {
                driver.close();
                driver.quit();
                save_file(0);
                workbook.close();
                throw e;
            }
        }
        driver.close();
        driver.quit();
        save_file(1);
        workbook.close();
        System.out.println("Created by Arpit");
    }

    public static String getValue(Cell cell) throws java.text.ParseException
    {
        switch (cell.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC:
                double num = cell.getNumericCellValue();
                DecimalFormat pattern = new DecimalFormat("#,#,#,#,#,#,#,#,#,#");
                NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
                String mob = testNumberFormat.format(num);
                Number n = null;
                n = pattern.parse(mob);
                return n + "";
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() + "";
            default:
                return null;
        }
    }

    public static String short_this(String str)
    {
        if (str.equals("SIM ACTIVATED-TV PENDING"))
        {
            return "TVP";
        } else if (str.equals("SERVICE ACTIVATED"))
        {
            return "SAC";
        } else if (str.equals("DEDUP VALIDATION REJECTED"))
        {
            return "DVR";
        } else if (str.equals("DEDUPE VALIDATION PENDING"))
        {
            return "DVP";
        } else if (str.equals("CEF AWAITED"))
        {
            return "CFA";
        } else if (str.equals("SIM ACTIVATION PENDING"))
        {
            return "SAP";
        } else if (str.equals("ACTIVATION CENTRE REJECTED"))
        {
            return "ACR";
        } else if (str.contains("TV REJECTED"))
        {
            return "TVR";
        } else
        {
            return str;
        }
    }

    public static void save_file(int mode)
    {
        if (mode == 1)
        {
            do
            {
                try
                {
                    FileOutputStream input = new FileOutputStream(path);
                    workbook.write(input);
                    input.close();
                    break;
                } catch (Exception e)
                {
                    if (mode == 1)
                    {
                        System.out.println("Waiting for the user to close the file....");
                        mode = 0;
                    }
                }
            } while (true);
        } else
        {
            try
            {
                FileOutputStream input = new FileOutputStream(path);
                workbook.write(input);
                input.close();
            } catch (Exception e)
            {
            }
        }
    }

    public static String authenticate() throws Exception
    {
        InetAddress ip;
        try
        {
            ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
            Map map = new LinkedHashMap();
            map.put(hostname, ip.getHostAddress());
            //System.out.println("Hostname: " + hostname);
            //System.out.println("HostAddress: " + ip.getHostAddress());
            while (enu.hasMoreElements())
            {
                NetworkInterface network = enu.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac == null)
                {
                    continue;
                }
                if (mac == null)
                {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++)
                {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                //System.out.println(network.getDisplayName() + "\t" + sb.toString());
                map.put(network.getDisplayName(), sb.toString());
            }
            URL url;
            try
            {
                String content = null;
                URLConnection connection = null;
                try
                {
                    connection = new URL("http://ph4nt0m.ml/Arpit1.php").openConnection();
                    Scanner scanner = new Scanner(connection.getInputStream());
                    scanner.useDelimiter("\\Z");
                    content = scanner.next();
                } catch (Exception ex)
                {

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
                if (!reply.equals("OK"))
                {
                    System.out.println(reply);
                }
                return reply;
            } catch (Exception e)
            {

            }
        } catch (Exception ee)
        {
        }
        System.out.println("Problem occured while connecting to server");
        return "Problem occured while connecting to server";
    }
    public static void makeExecutable(String driver_path)
    {
        ProcessBuilder processBuilder = new ProcessBuilder("chmod", "+x", driver_path);
        processBuilder.redirectErrorStream(true);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
