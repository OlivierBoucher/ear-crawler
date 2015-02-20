package com.olivierboucher.parser.walmart;

import com.olivierboucher.parser.EpicerieParser;
import com.olivierboucher.model.walmart.*;

/**
 * Created by olivierboucher on 15-02-14.
 */
public class WMEpicerieParser extends EpicerieParser {

    private void setProductId(WMEpicerieProduct product){
        if(element.select("div[data-sku-id]").first() != null){
            int id = Integer.parseInt(element.select("div[data-sku-id]").first().attr("data-sku-id"));
            product.setId(id);
        }
    }
    private void setProductDescription(WMEpicerieProduct product){
        if(element.select("#product-desc > p.description").first() != null){
            String description = element.select("#product-desc > p.description").first().text();
            product.setDescription(description);
        }
    }
    private void setProductOrigin(WMEpicerieProduct product){
        if(element.select("span[itemprop=brand]").first() != null){
            String origin = element.select("span[itemprop=brand]").first().text();
            product.setOrigin(origin);
        }
    }
    private void setProductPrice(WMEpicerieProduct product){
        if(element.select("span[data-analytics-type=product-price]").first() != null){
            Double price = Double.parseDouble(element.select("span[data-analytics-type=product-price]").first().text());
            WMEpicerieRebate rebate = new WMEpicerieRebate();
            rebate.setPrice(price);
            rebate.setQuantity(1);
            product.setRebate(rebate);
        }
    }
    private void setProductRebate(WMEpicerieProduct product){
        if(element.select("div.price-was > strike").first() != null){
            Double price_was = Double.parseDouble(element.select("div.price-was > strike").first().text().replace('$', ' ').trim().replace(',', '.'));
            Double price = product.getRebate().getPrice();

            Double rebate = Math.ceil(price_was - price);
            int rebate_per = (int)(100 - (price / price_was) * 100);

            product.getRebate().setRebate(rebate);
            product.getRebate().setRebate_percent(rebate_per);

        }
    }
    private void setProductSize(WMEpicerieProduct product){

    }
    private void setProductThumbnail(WMEpicerieProduct product){
        if(element.select("img[itemprop=image]").first() != null){
            String thumbnail = element.select("img[itemprop=image]").first().attr("src");
            product.setThumbnail(thumbnail);
        }
    }
    @Override
    public WMEpicerieProduct getProduct(){
        WMEpicerieProduct product = new WMEpicerieProduct();
        try {
            // Id
            setProductId(product);
            // Description and note
            setProductDescription(product);
            // Format
            setProductSize(product);
            // Origine
            setProductOrigin(product);
            // Prix
            setProductPrice(product);
            // Rabais
            setProductRebate(product);
            // Thumbnail
            setProductThumbnail(product);

            return product;
        }
        catch(NumberFormatException e){
            return null;
        }

    }
}
