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
		
		if(NeedsUpdate()){
			System.out.print("Update required: " + df.format(new Date()) + "\n");
	        EpicerieCrawler crawler = new EpicerieCrawler();
	        Date start = new Date();
	        System.out.print("Starting job: " + df.format(start)+"\n");
	        CrawlerJobResult<EpicerieProduct> result  = crawler.StartJob();
	        if(result.getResult() == Common.CrawlerResult.Complete){
	        	Date finish = new Date();
	        	System.out.print("Finishing job: " + df.format(new Date())+"\n");
	        	System.out.print("Job result: Complete ("+ getDateDiff(start,finish, TimeUnit.SECONDS)+" seconds)\n");
	        	System.out.print("Job item result: "+result.getItems().size()+"\n");
	        	// Envoi à la base de données
	        	Date db_start = new Date();
	        	System.out.print("Sending to database: "+df.format(db_start)+"\n");
	        	helper.Connect();
	        	try {
					helper.SendProductList(result.getItems());
				} 
	        	catch (SQLException e) {
					e.printStackTrace();
				}
	        	helper.Disconnect();
	        	finish = new Date();
	        	System.out.print("All items sent: "+df.format(finish)+"\n");
	        	System.out.print("Done sending to database ("+ getDateDiff(db_start,finish, TimeUnit.SECONDS)+" seconds)\n");
	        	System.out.print("Data mining is done ("+getDateDiff(start,finish, TimeUnit.SECONDS) +" seconds)\n");
	        }
	        else{
	        	System.out.print("Finishing job: " + df.format(new Date())+"\n");
	        	System.out.print("Job failed\n");
	        }
		}
		else{
			System.out.print("Database is up to date: " + df.format(new Date()) + "\n");
		}
    }
	private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	private static boolean NeedsUpdate(){
		EpicerieCrawler crawler = new EpicerieCrawler();
		Date online_date = crawler.SMGetFirstProductAvailable().getStart();
		Date db_date = null;
		// Get date from database
		helper.Connect();
		try {
			 db_date = helper.GetActualStartDate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		helper.Disconnect();
		// Comparaison
		if(db_date == null || !(db_date.equals(online_date))){
			return true;
		}
		else{
			return false;
		}
	}
}
