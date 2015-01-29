package com.olivierboucher.ear;
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
			connection = DriverManager.getConnection("jdbc:mysql://104.131.203.247/epicerie", "ear_system", "EarSystem2015");
		} 
		catch (ClassNotFoundException | SQLException e) {
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
	public void SendProductList(List<Product> productList) throws SQLException{
		// Mise à jour du statut des éléments précedents
		SetAllInactive();
		//Ajout
		PreparedStatement rqst = connection.prepareStatement("INSERT INTO product (product_id, product_description, product_size, product_origin, product_price, product_qty, product_rebate, product_rebate_per, product_start, product_end, product_store, product_category, product_thumbnail, product_modif, product_active) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE product_price=?, product_qty=?, product_rebate=?, product_rebate_per=?, product_start=?, product_end=?, product_modif=?, product_active=?");
		
		for(Product p : productList){
			// INSERT
			rqst.setInt(1, p.getId());
			rqst.setString(2, p.getDescription());
			rqst.setString(3, p.getSize());
			rqst.setString(4, p.getOrigin());
			rqst.setDouble(5, p.getPrice());
			rqst.setInt(6, p.getQuantity());
			rqst.setDouble(7, p.getRebate());
			rqst.setInt(8,p.getRebate_percent());
			rqst.setDate(9, new java.sql.Date(p.getStart().getTime()));
			rqst.setDate(10, new java.sql.Date(p.getEnd().getTime()));
			rqst.setString(11, p.getVendor().getName());
			rqst.setString(12, p.getCategory().getName());
			rqst.setString(13, p.getThumbnail());
			rqst.setDate(14, new java.sql.Date(new Date().getTime()));
			rqst.setBoolean(15, true);
			// UPDATE
			rqst.setDouble(16, p.getPrice());
			rqst.setInt(17, p.getQuantity());
			rqst.setDouble(18, p.getRebate());
			rqst.setInt(19, p.getRebate_percent());
			rqst.setDate(20, new java.sql.Date(p.getStart().getTime()));
			rqst.setDate(21, new java.sql.Date(p.getEnd().getTime()));
			rqst.setDate(22, new java.sql.Date(new Date().getTime()));
			rqst.setBoolean(23, true);
			// Ajouter à la batch
			rqst.addBatch();
		}
		for(int r:rqst.executeBatch()){
			int i = 0;
			if(r == Statement.EXECUTE_FAILED){
				System.out.print("Failure while adding product with id "+ productList.get(i).getId());
			}
			i++;
		}
		rqst.close();
	}
	public Date GetActualStartDate() throws SQLException{	
		//Récupèrer le premier resultat actif
		Statement rqst = connection.createStatement();
		ResultSet result = rqst.executeQuery("SELECT product_start FROM product WHERE product_active=1 LIMIT 1;");
		rqst.closeOnCompletion();
		if(result.next()){
			return result.getDate("product_start");
		}
		else{
			return null;
		}
	}
	public void SetAllInactive() throws SQLException{
		Statement rqst = connection.createStatement();
		rqst.executeUpdate("UPDATE product SET product_active=0;");
		rqst.close();
	}
// OLD CODE
//			//Trouver le id min et le id max
//			productList.sort(Product.ProductIdComparator);
//			int minId = productList.get(0).getId();
//			int maxId = productList.get(productList.size()-1).getId();
//			//Récupèrer la liste des id dans l'interval de ces 2 numéros
//			PreparedStatement rqst = connection.prepareStatement("SELECT id FROM produit WHERE id >= ? AND id <= ?");
//			rqst.setInt(0, minId);
//			rqst.setInt(1, maxId);
//			ResultSet result = rqst.executeQuery();
//			// On trie pour savoir quels produits ont besoin d'être mis à jour
//			List<Product> productListUpdate = new ArrayList<Product>();
//			while(result.next()){
//				//Comparer chaque id avec chaque id de la liste
//				for(int i =0; i<productList.size(); i++){
//					if(result.getInt("id") == productList.get(i).getId()){
//						productListUpdate.add(productList.get(i));
//						productList.remove(i);
//						break;
//					}
//				}
//			}
//			// Pour chacun des produits dans la liste productListUpdate, on UPDATE
//			
//			// Pour chacun des produits dans la liste productList, on INSERT
}
