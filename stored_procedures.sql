DELIMITER $$
CREATE DEFINER=`olivier`@`%` PROCEDURE `InsertProductAndRebate`(
 IN iproduct_id INT,
 IN iproduct_description VARCHAR(256),
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
 )
BEGIN
  IF (SELECT EXISTS(SELECT product_id FROM product WHERE product_id = iproduct_id )) THEN
    UPDATE product_price 
        SET price_active = 0 
        WHERE product_id = iproduct_id;
  ELSE
    INSERT INTO product(product_id,product_description, product_note, product_size, product_origin, product_thumbnail, product_introduced, product_category_id, product_store_id) 
        VALUES(iproduct_id, iproduct_description, iproduct_note, iproduct_size, iproduct_origin, iproduct_thumbnail, CURDATE(), icategory_id, istore_id);
    END IF;
    
  INSERT INTO product_price(price_price,price_quantity, price_rebate, price_rebate_percent,price_start, price_end, product_id,price_active) 
    VALUES (iprice_price, iprice_quantity, iprice_rebate, iprice_rebate_per, iprice_start, iprice_end, iproduct_id, 1);
END$$
DELIMITER ;
