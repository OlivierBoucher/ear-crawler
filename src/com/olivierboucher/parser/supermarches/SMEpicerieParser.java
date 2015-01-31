package com.olivierboucher.parser.supermarches;
import com.olivierboucher.model.EpicerieProduct;
import com.olivierboucher.model.EpicerieRebate;
import com.olivierboucher.model.supermarches.SMEpicerieProduct;
import com.olivierboucher.model.supermarches.SMEpicerieRebate;
import com.olivierboucher.parser.AbstractEpicerieParser;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMEpicerieParser extends AbstractEpicerieParser {
    // Patterns as member so they do not need to compile for each product
    private Pattern priceCH = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?ch");
    private Pattern priceSac = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?sac");
    private Pattern pricePqt = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?pqt");
    private Pattern priceMulti = Pattern.compile("\\d+[\\s]?\\/[\\s]\\d+[.]\\d{2}");
    private Pattern priceCaisse = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?caisse");
    private Pattern pricePoid = Pattern.compile("\\d+[.]\\d{2}[\\s]?\\/[\\s]?lb[\\s]?\\d+[.]\\d{2}[\\s]?\\/[\\s]?kg");

    public SMEpicerieParser(){
    }

    private void setProductId(SMEpicerieProduct product){
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
    private void setProductDescription(SMEpicerieProduct product){
        if(element.select("[width=230] > a, [width=228] > a").first() != null){
            String description;
            description = element.select("[width=230] > a, [width=228] > a").first().text().trim();
            Pattern productWithNote = Pattern.compile(
                    "(.+)(\\*\\sVoir.+|\\*\\sJusqu.+|\\*\\sExcepté.+|\\*\\sAchetez-en.+|\\*\\sBonus.+|" +
                            "Économisez.+|Limite.+|Achetez-en.+|Jusqu.+|Voir\\svari.+|Provenant.+|Catégorie.+|" +
                            "|Du\\sQuébec.+|Du\\sCanada.+|Du\\sMexique.+|Des\\sÉtats.+)");

            Matcher m = productWithNote.matcher(description);
            if(m.find()){
                String tes = m.group(0);
                String test = m.group(1);
                String testt = m.group(2);
                product.setDescription(m.group(1));
                product.setNote(m.group(2));
            }
            else{
                product.setDescription(description);
                product.setNote("");
            }
        }
    }
    private void setProductSize(SMEpicerieProduct product){
        if(element.select("td[width=76]").first() != null){
            product.setSize(element.select("td[width=76]").first().text());
        }
    }
    private void setProductOrigin(SMEpicerieProduct product){
        if(element.select("td[width=92]").first() != null){
            product.setOrigin(element.select("td[width=92]").first().text());
        }
    }
    private void setProductPriceAndQty(SMEpicerieProduct product){
        if(element.select("td[width=72]").first() != null){
            String priceUnitS = element.select("td[width=72]").first().text();
            // Matchers
            Matcher mPriceCH = priceCH.matcher(priceUnitS);
            Matcher mPricePqt = pricePqt.matcher(priceUnitS);
            Matcher mPriceSac = priceSac.matcher(priceUnitS);
            Matcher mPriceMulti = priceMulti.matcher(priceUnitS);
            Matcher mPriceCaisse = priceCaisse.matcher(priceUnitS);
            Matcher mPricePoid = pricePoid.matcher(priceUnitS);

            SMEpicerieRebate rebate = new SMEpicerieRebate();

            //Traitement en fonction du type d'item
            if(mPriceCH.find()){
                rebate.setQuantity(1);
                priceUnitS = mPriceCH.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                rebate.setPrice(Double.parseDouble(priceS));

            }
            else if(mPricePqt.find()){
                rebate.setQuantity(SMEpicerieRebate.QTY_PQT);
                priceUnitS = mPricePqt.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                rebate.setPrice(Double.parseDouble(priceS));
            }
            else if(mPriceSac.find()){
                rebate.setQuantity(SMEpicerieRebate.QTY_SAC);
                priceUnitS = mPriceSac.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                rebate.setPrice(Double.parseDouble(priceS));
            }
            else if(mPriceMulti.find()){
                priceUnitS = mPriceMulti.group(0);
                String[] split = priceUnitS.split("/");
                rebate.setQuantity(Integer.parseInt(split[0].trim()));
                rebate.setPrice(Double.parseDouble(split[1].trim()));
            }
            else if(mPriceCaisse.find()){
                rebate.setQuantity(SMEpicerieRebate.QTY_CAISSE);
                priceUnitS = mPriceCaisse.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                rebate.setPrice(Double.parseDouble(priceS));
            }
            else if(mPricePoid.find()){
                rebate.setQuantity(SMEpicerieRebate.QTY_POIDS);
                priceUnitS = mPricePoid.group(0);
                String priceS = priceUnitS.split("/")[0].trim();
                rebate.setPrice(Double.parseDouble(priceS));
            }
            product.setRebate(rebate);
        }
    }
    private void setProductRebate(SMEpicerieProduct product){
        if(element.select("td[width=65]").first() != null){
            String[] splitted = element.select("td[width=65]").first().text().split("[$]");

            SMEpicerieRebate rebate = product.getRebate();

            // Rabais en dollars
            String sRebate = "";
            for(int i = 0; i<splitted[0].length(); i++){
                char charAt = splitted[0].charAt(i);

                if((charAt >= '0' && charAt <= '9') || charAt == '.'){
                    sRebate += charAt;
                }
            }
            rebate.setRebate(Double.parseDouble(sRebate));
            // Rabais en pourcent
            String sRebatePer = "";
            for(int i = 0; i<splitted[1].length(); i++){
                char charAt = splitted[1].charAt(i);

                if(charAt >= '0' && charAt <= '9'){
                    sRebatePer += charAt;
                }
            }
            rebate.setRebate_percent(Integer.parseInt(sRebatePer));

            //Reassign to the product
            product.setRebate(rebate);
        }
    }
    private void setProductDates(SMEpicerieProduct product){
        if(element.select("td[width=60]").first() != null){
            String bothDates = element.select("td[width=60]").first().html();
            bothDates = bothDates.replace("&nbsp;", " ");
            String[] dateArray = bothDates.split("<br>");

            SMEpicerieRebate rebate = product.getRebate();

            rebate.setStart(GetRealDate(dateArray[0]));
            rebate.setEnd(GetRealDate(dateArray[1]));

            //Reassign to the product
            product.setRebate(rebate);
        }
    }
    private void setProductThumbnail(SMEpicerieProduct product){
        if(element.select("img").first() != null){
            product.setThumbnail(element.select("img").first().attr("src"));
        }
    }
    @Override
    public SMEpicerieProduct getProduct(){
        SMEpicerieProduct product = new SMEpicerieProduct();

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

        //TODO : fix this
        return product;
    }
    private Date GetRealDate(String date){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        String[] splitted = date.split(" ");
        switch(splitted[1].charAt(0)){

            case 'J':
                // Deuxième lettre
                // Possibilités : Janvier, Juin, Juillet
                switch (splitted[1].charAt(1))
                {
                    case 'a':
                        splitted[1] = "01";
                        break;
                    case 'u':
                        // Quatrieme lettre
                        switch (splitted[1].charAt(3))
                        {
                            case 'n':
                                splitted[1] = "06";
                                break;
                            case 'l':
                                splitted[1] = "07";
                                break;
                        }
                        break;
                }
                break;
            case 'M':
                // Troisième lettre
                // Possibilités : Mars, Mai
                switch (splitted[1].charAt(2))
                {
                    case 'r':
                        splitted[1] = "03";
                        break;
                    case 'i':
                        splitted[1] = "05";
                        break;

                }
                break;
            case 'A':
                // Deuxième lettre
                // Possibilités : Avril, Aout
                switch (splitted[1].charAt(1))
                {
                    case 'v':
                        splitted[1] = "04";
                        break;
                    case 'o':
                        splitted[1] = "08";
                        break;
                }
                break;
            case 'F':
                splitted[1] = "02";
                break;
            case 'S':
                splitted[1] = "09";
                break;
            case 'O':
                splitted[1] = "10";
                break;
            case 'N':
                splitted[1] = "11";
                break;
            case 'D':
                splitted[1] = "12";
                break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(splitted[0]);
        sb.append("/");
        sb.append(splitted[1]);
        sb.append("/");
        sb.append("20");
        sb.append(splitted[2]);

        try {
            return df.parse(sb.toString());
        }
        catch (ParseException e) {
            return null;
        }
    }
    public void setElement(Element element){
        this.element = element;
    }
}
