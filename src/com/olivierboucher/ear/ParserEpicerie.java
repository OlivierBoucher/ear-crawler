package com.olivierboucher.ear;
import com.olivierboucher.parser.Parser;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserEpicerie extends Parser<Product> {

    private Element element;
    // Patterns as member so they do not need to compile for each product
    private Pattern priceCH = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?ch");
    private Pattern priceSac = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?sac");
    private Pattern pricePqt = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?pqt");
    private Pattern priceMulti = Pattern.compile("\\d+[\\s]?\\/[\\s]\\d+[.]\\d{2}");
    private Pattern priceCaisse = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?caisse");
    private Pattern pricePoid = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?lb[\\s]?\\d+[.]\\d{2}[\\s]?\\/[\\s]?kg");

    public ParserEpicerie(){
    }

    private void setProductId(Product product){
        if(element.select("a.dslink") != null){
            if(element.select("a.dslink").first().attr("onClick") != null){
                String rawId = element.select("a.dslink").first().attr("onClick");
                int indexOfIdEQ = rawId.indexOf("id=") + 3;
                String sId = "";
                for(int i = indexOfIdEQ; i<rawId.length(); i++){
                    char charAt = rawId.charAt(i);

                    if(charAt >= '0' && charAt <= '9'){
                        sId += charAt;
                    }
                    else{
                        break;
                    }
                }
                product.setId(Integer.parseInt(sId));
            }
        }
    }
    private void setProductDescription(Product product){
        if(element.select("[width=230] > a, [width=228] > a").first() != null){
            String description;
            description = element.select("[width=230] > a, [width=228] > a").first().text();
            Pattern productWithNote = Pattern.compile(
                    "(.+)(\\*\\sVoir.+|\\*\\sJusqu.+|\\*\\sExcepté.+|\\*\\sAchetez-en.+|\\*\\sBonus.+|" +
                            "Économisez.+|Limite.+|Achetez-en.+|Jusqu.+|Voir\\svari.+|Provenant.+|Catégorie.+|" +
                            "|Du\\sQuébec.+|Du\\sCanada.+|Du\\sMexique.+|Des\\sÉtats.+)");

            Matcher m = productWithNote.matcher(description);
            if(m.find()){
                product.setDescription(m.group(1));
                product.setNote(m.group(2));
            }else{
                product.setDescription(description);
                product.setNote("");
            }
        }
    }
    private void setProductSize(Product product){
        if(element.select("td[width=76]").first() != null){
            product.setSize(element.select("td[width=76]").first().text());
        }
    }
    private void setProductOrigin(Product product){
        if(element.select("td[width=92]").first() != null){
            product.setOrigin(element.select("td[width=92]").first().text());
        }
    }
    private void setProductPriceAndQty(Product product){
        if(element.select("td[width=72]").first() != null){
            String priceUnitS = element.select("td[width=72]").first().text();
            // Matchers
            Matcher mPriceCH = priceCH.matcher(priceUnitS);
            Matcher mPricePqt = pricePqt.matcher(priceUnitS);
            Matcher mPriceSac = priceSac.matcher(priceUnitS);
            Matcher mPriceMulti = priceMulti.matcher(priceUnitS);
            Matcher mPriceCaisse = priceCaisse.matcher(priceUnitS);
            Matcher mPricePoid = pricePoid.matcher(priceUnitS);
            //Traitement en fonction du type d'item
            if(mPriceCH.find()){
                product.setQuantity(1);
                priceUnitS = mPriceCH.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                product.setPrice(Double.parseDouble(priceS));

            }
            else if(mPricePqt.find()){
                product.setQuantity(Product.QTY_PQT);
                priceUnitS = mPricePqt.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                product.setPrice(Double.parseDouble(priceS));
            }
            else if(mPriceSac.find()){
                product.setQuantity(Product.QTY_SAC);
                priceUnitS = mPriceSac.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                product.setPrice(Double.parseDouble(priceS));
            }
            else if(mPriceMulti.find()){
                priceUnitS = mPriceMulti.group(0);
                String[] split = priceUnitS.split("/");
                product.setQuantity(Integer.parseInt(split[0].trim()));
                product.setPrice(Double.parseDouble(split[1].trim()));
            }
            else if(mPriceCaisse.find()){
                product.setQuantity(Product.QTY_CAISSE);
                priceUnitS = mPriceCaisse.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                product.setPrice(Double.parseDouble(priceS));
            }
            else if(mPricePoid.find()){
                product.setQuantity(Product.QTY_POIDS);
                priceUnitS = mPricePoid.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                product.setPrice(Double.parseDouble(priceS));
            }
        }
    }
    private void setProductRebate(Product product){
        if(element.select("td[width=65]").first() != null){
            String[] splitted = element.select("td[width=65]").first().text().split("[$]");

            // Rabais en dollars
            String sRebate = "";
            for(int i = 0; i<splitted[0].length(); i++){
                char charAt = splitted[0].charAt(i);

                if((charAt >= '0' && charAt <= '9') || charAt == '.'){
                    sRebate += charAt;
                }
            }
            product.setRebate(Double.parseDouble(sRebate));
            // Rabais en pourcent
            String sRebatePer = "";
            for(int i = 0; i<splitted[1].length(); i++){
                char charAt = splitted[1].charAt(i);

                if(charAt >= '0' && charAt <= '9'){
                    sRebatePer += charAt;
                }
            }
            product.setRebate_percent(Integer.parseInt(sRebatePer));
        }
    }
    private void setProductDates(Product product){
        if(element.select("td[width=60]").first() != null){
            String bothDates = element.select("td[width=60]").first().html();
            bothDates = bothDates.replace("&nbsp;", " ");
            String[] dateArray = bothDates.split("<br>");
            product.setStart(dateArray[0]);
            product.setEnd(dateArray[1]);
        }
    }
    private void setProductThumbnail(Product product){
        if(element.select("img").first() != null){
            product.setThumbnail(element.select("img").first().attr("src"));
        }
    }
    @Override
    public Product getObject(){
        Product product = new Product();
        // Id
        setProductId(product);
        // Description and note
        setProductDescription(product);
        // Format
        setProductSize(product);
        // Origine
        setProductOrigin(product);
        // Prix
        setProductPriceAndQty(product);
        // Rabais
        setProductRebate(product);
        // Dates
        setProductDates(product);
        // Image
        setProductThumbnail(product);


        return product;
    }
    public void setElement(Element element){
        this.element = element;
    }
}
