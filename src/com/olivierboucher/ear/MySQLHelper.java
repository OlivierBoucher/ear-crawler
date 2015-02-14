package com.olivierboucher.ear;
import com.olivierboucher.crawler.supermarches.SMCrawler;
import com.olivierboucher.crawler.walmart.WMCrawler;
import com.olivierboucher.model.EpicerieCategory;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.EpicerieStore;
import com.olivierboucher.model.supermarches.SMEpicerieCategory;
import com.olivierboucher.model.supermarches.SMEpicerieStore;
import com.olivierboucher.model.walmart.WMEpicerieCategory;
import com.olivierboucher.model.walmart.WMEpicerieStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLHelper {


	private Connection connection;

	public void Connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://104.131.203.247/test_epicerie", "ear_system", "EarSystem2015");
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public void Disconnect(){
		try {
			connection.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void SendProductList(List<EpicerieProduct> productList) throws SQLException{

		PreparedStatement rqst = connection.prepareStatement("CALL InsertProductAndRebate(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		for(EpicerieProduct p : productList){
			// Parameters
			/*
			 IN iproduct_id INT,
			 IN iproduct_description VARCHAR(100),
			 IN iproduct_note VARCHAR(256),
			 IN iproduct_size VARCHAR(100),
			 IN iproduct_origin VARCHAR(100),
			 IN iproduct_thumbnail VARCHAR(100),
			 IN icategory_id INT,
			 IN istore_id INT,
			 IN iprice_price DOUBLE,
			 IN iprice_rebate DOUBLE,
			 IN iprice_rebate_per INT,
			 IN iprice_quantity INT,
			 IN iprice_start DATE,
			 IN iprice_end DATE
			*/
			rqst.setInt(1,p.getId());
			rqst.setString(2, p.getDescription());
			rqst.setString(3, p.getNote());
			rqst.setString(4, p.getSize());
			rqst.setString(5, p.getOrigin());
			rqst.setString(6, p.getThumbnail());
			rqst.setInt(7,p.getCategory().getProductCategoryId());
			rqst.setInt(8, p.getStore().getId());
			rqst.setDouble(9,p.getRebate().getPrice());
			rqst.setDouble(10,p.getRebate().getRebate());
			rqst.setDouble(11,p.getRebate().getRebate_percent());
			rqst.setInt(12,p.getRebate().getQuantity());
			rqst.setDate(13,new java.sql.Date(p.getRebate().getStart().getTime()));
			rqst.setDate(14,new java.sql.Date(p.getRebate().getEnd().getTime()));

			// Ajouter à la batch
			rqst.addBatch();
		}
		for(int r:rqst.executeBatch()){
			int i = 0;
			if(r == Statement.EXECUTE_FAILED){
				System.out.print("Failure while adding product with id "+ productList.get(i).getId() + "\n");
			}
			i++;
		}
		rqst.close();
	}
	public Date SMGetActualStartDate() throws SQLException{
		//Récupèrer le premier resultat actif
		// TODO : Stored procedure
		Statement rqst = connection.createStatement();
		ResultSet result = rqst.executeQuery("SELECT price_start FROM product_price WHERE price_active=1 LIMIT 1;");
		rqst.closeOnCompletion();
		if(result.next()){
			return result.getDate("price_start");
		}
		else{
			return null;
		}
	}
	public List<EpicerieCategory> GetCategoryList(int websiteId) throws SQLException{
		List<EpicerieCategory> list = new ArrayList<EpicerieCategory>();
		PreparedStatement rqst = connection.prepareStatement("SELECT * FROM website_category WHERE website_id=?");
		rqst.setInt(1,websiteId);
		ResultSet result = rqst.executeQuery();
		rqst.closeOnCompletion();
		switch(websiteId){
			case SMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new SMEpicerieCategory(result.getInt("category_id"), result.getString("category_name"), result.getString("category_slug"), result.getInt("product_category_id")));
				}
				break;
			case WMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new WMEpicerieCategory(result.getInt("category_id"), result.getString("category_name"), result.getString("category_slug"), result.getInt("product_category_id")));
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid website ID");
		}
		return list;
	}
	public List<EpicerieStore> GetStoreList(int websiteId) throws SQLException{
		List<EpicerieStore> list = new ArrayList<EpicerieStore>();
		PreparedStatement rqst = connection.prepareStatement("SELECT * FROM product_store WHERE website_id=?");
		rqst.setInt(1,websiteId);
		ResultSet result = rqst.executeQuery();
		rqst.closeOnCompletion();
		switch(websiteId){
			case SMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new SMEpicerieStore(result.getInt("store_id"), result.getString("store_name"), result.getString("store_slug")));
				}
				break;
			case WMCrawler.WEBSITE_ID:
				while (result.next()){
					list.add(new WMEpicerieStore(result.getInt("store_id"), result.getString("store_name")));
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid website ID");
		}
		return list;
	}
}
