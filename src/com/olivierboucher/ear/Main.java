package com.olivierboucher.ear;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import com.olivierboucher.crawler.CrawlerJobResult;
import com.olivierboucher.crawler.EpicerieCrawler;
import com.olivierboucher.crawler.supermarches.SMCrawler;
import com.olivierboucher.crawler.walmart.WMCrawler;
import com.olivierboucher.model.EpicerieProduct;

public class Main {

	static MySQLHelper helper = new MySQLHelper();
	static DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	static List<EpicerieCrawler> crawlerList = new ArrayList<EpicerieCrawler>();

	public static void main(String[] args) {
        Initialize();
		// Add crawlers here TODO : Maybe a factory would be nice instead (load websites from db, then foreach initialise)
		//crawlerList.add(new SMCrawler());
        crawlerList.add(new WMCrawler());
		// This could be multithreaded as well
		for(EpicerieCrawler crawler : crawlerList) {
			Date start = new Date();
			System.out.print("Starting job: " + df.format(start) + "\n");
			CrawlerJobResult<EpicerieProduct> result = crawler.StartJobMultiThreaded();
			Date finish = new Date();
			System.out.print("Finishing job: " + df.format(new Date()) + "\n");

			switch(result.getResult()){
				case Complete:
					System.out.print("Job result: Complete (" + getDateDiff(start, finish, TimeUnit.SECONDS) + " seconds)\n");
					//AddToDatabase(result, start, finish);
					break;
				case UpToDate:
					break;
			}
		}
	}
	private static void AddToDatabase(CrawlerJobResult<EpicerieProduct> result, Date start, Date finish) {
			System.out.print("Job item result: " + result.getItems().size() + "\n");
			Date db_start = new Date();
			System.out.print("Sending to database: " + df.format(db_start) + "\n");
			helper.Connect();
			try {
				helper.SendProductList(result.getItems());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			helper.Disconnect();
			finish = new Date();
			System.out.print("All items sent: " + df.format(finish) + "\n");
			System.out.print("Done sending to database (" + getDateDiff(db_start, finish, TimeUnit.SECONDS) + " seconds)\n");
			System.out.print("Data mining is done (" + getDateDiff(start, finish, TimeUnit.SECONDS) + " seconds)\n");
	}
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
    private static void Initialize(){
        OsCheck.OSType ostype=OsCheck.getOperatingSystemType();

        switch (ostype) {
            case Windows:
                System.setProperty("phantomjs.binary.path", "module/phantomjs-1_9_2/win/phantomjs.exe");
                break;
            case MacOS:
                System.setProperty("phantomjs.binary.path", "module/phantomjs-1_9_2/mac/phantomjs");
                break;
            case Linux:
                //if 32bit
                if(System.getProperty("os.arch").contains("x86")){
                    System.setProperty("phantomjs.binary.path", "module/phantomjs-1_9_2/linux32/phantomjs");
                }
                else{
                    System.setProperty("phantomjs.binary.path", "module/phantomjs-1_9_2/linux64/phantomjs");
                }
                break;
            case Other:
                break;
        }
    }
    /**
     * helper class to check the operating system this Java VM runs in
     *
     * please keep the notes below as a pseudo-license
     *
     * http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
     * compare to http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/common/src/com/tc/util/runtime/Os.java
     * http://www.docjar.com/html/api/org/apache/commons/lang/SystemUtils.java.html
     */
    public static final class OsCheck {
        /**
         * types of Operating Systems
         */
        public enum OSType {
            Windows, MacOS, Linux, Other
        };

        // cached result of OS detection
        protected static OSType detectedOS;

        /**
         * detect the operating system from the os.name System property and cache
         * the result
         *
         * @returns - the operating system detected
         */
        public static OSType getOperatingSystemType() {
            if (detectedOS == null) {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                    detectedOS = OSType.MacOS;
                } else if (OS.indexOf("win") >= 0) {
                    detectedOS = OSType.Windows;
                } else if (OS.indexOf("nux") >= 0) {
                    detectedOS = OSType.Linux;
                } else {
                    detectedOS = OSType.Other;
                }
            }
            return detectedOS;
        }
    }
}
