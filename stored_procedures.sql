CREATE DEFINER=`olivier`@`%` PROCEDURE `InsertProductAndRebate`(
 IN iproduct_sku INT,
 IN iproduct_description VARCHAR(256),
 IN iproduct_size VARCHAR(100),
 IN iproduct_origin VARCHAR(100),
 IN iproduct_thumbnail VARCHAR(256),

 IN icategory_id INT,
 IN istore_id INT,

 IN iprice_price DOUBLE,
 IN iprice_rebate DOUBLE,
 IN iprice_rebate_per INT,
 IN iprice_quantity INT,
 IN iprice_start DATE,
 IN iprice_note VARCHAR(256),
 IN iprice_end DATE
 )
BEGIN
  IF EXISTS(SELECT product_id FROM product WHERE product_sku=iproduct_sku) THEN
    DECLARE lproduct_id INT;
    SELECT product_id INTO lproduct_id FROM product WHERE product_sku=iproduct_sku;
    INSERT INTO product_price(price_price,price_quantity, price_rebate, price_rebate_percent,price_start, price_end, product_id,price_active, price_note)
    VALUES (iprice_price, iprice_quantity, iprice_rebate, iprice_rebate_per, iprice_start, iprice_end, lproduct_id, 1, iprice_note);
  ELSE
    INSERT INTO product(product_sku,product_description, product_size, product_origin, product_thumbnail, product_introduced, product_category_id, product_store_id)
    VALUES(iproduct_sku, iproduct_description, iproduct_size, iproduct_origin, iproduct_thumbnail, CURDATE(), icategory_id, istore_id);
    DECLARE lproduct_id INT;
    SELECT product_id INTO lproduct_id FROM product WHERE product_sku=iproduct_sku;
    INSERT INTO product_price(price_price,price_quantity, price_rebate, price_rebate_percent,price_start, price_end, product_id,price_active, price_note)
    VALUES (iprice_price, iprice_quantity, iprice_rebate, iprice_rebate_per, iprice_start, iprice_end, lproduct_id, 1, iprice_note);
  END IF;
END;
