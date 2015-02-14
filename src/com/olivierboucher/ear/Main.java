package com.olivierboucher.ear;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.olivierboucher.crawler.CrawlerJobResult;
import com.olivierboucher.crawler.EpicerieCrawler;
import com.olivierboucher.crawler.supermarches.SMCrawler;
import com.olivierboucher.model.EpicerieProduct;

public class Main {

	static MySQLHelper helper = new MySQLHelper();
	static DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	static List<EpicerieCrawler> crawlerList = new ArrayList<EpicerieCrawler>();

	public static void main(String[] args) {
		// Add crawlers here
		crawlerList.add(new SMCrawler());
		// This could be multithreaded as well
		for(EpicerieCrawler crawler : crawlerList) {
			Date start = new Date();
			System.out.print("Starting job: " + df.format(start) + "\n");
			CrawlerJobResult<EpicerieProduct> result = crawler.StartJob();
			Date finish = new Date();
			System.out.print("Finishing job: " + df.format(new Date()) + "\n");

			switch(result.getResult()){
				case Complete:
					System.out.print("Job result: Complete (" + getDateDiff(start, finish, TimeUnit.SECONDS) + " seconds)\n");
					//AddToDatabase(result, start, finish);
					break;
				case Incomplete:
					break;
				case UpToDate:
					break;
				case NetworkError:
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
	private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
}
