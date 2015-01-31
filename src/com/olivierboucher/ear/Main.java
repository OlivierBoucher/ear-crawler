package com.olivierboucher.ear;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.olivierboucher.crawler.Common;
import com.olivierboucher.crawler.CrawlerJobResult;
import com.olivierboucher.model.EpicerieProduct;

public class Main {

	static MySQLHelper helper = new MySQLHelper();

	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		EpicerieCrawler crawler = new EpicerieCrawler();
		Date start = new Date();
		System.out.print("Starting job: " + df.format(start)+"\n");
		CrawlerJobResult<EpicerieProduct> result  = crawler.StartJob();
		Date finish = new Date();
		System.out.print("Finishing job: " + df.format(new Date())+"\n");
		// Look through job website results
		boolean errorOccured = false;
		for(String site : result.getResult().keySet()){
			System.out.print("New result: " + site);
			Common.CrawlerResult siteResult = result.getResult().get(site);
			if( siteResult == Common.CrawlerResult.Complete){
				System.out.print(" completed.\n");
			}
			else if (siteResult == Common.CrawlerResult.UpToDate){
				System.out.print(" up to date.\n");
			}
			else{
				// Raise flag, we don't bother finding what it is yet
				System.out.print(" failed.\n");
				errorOccured = true;
			}
		}
		if(!errorOccured){
			System.out.print("Job result: Complete ("+ getDateDiff(start,finish, TimeUnit.SECONDS)+" seconds)\n");
			if(result.getItems().size() > 0) {
				// Envoi à la base de données si mise à jour
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
		}
		else{
			System.out.print("Job failed\n");
		}
	}
	private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
}
