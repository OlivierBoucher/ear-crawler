package com.olivierboucher.parser;

import com.olivierboucher.exception.ProductParseException;
import com.olivierboucher.model.supermarches.SMEpicerieProduct;
import com.olivierboucher.model.supermarches.SMEpicerieRebate;
import com.olivierboucher.parser.supermarches.SMEpicerieParser;
import com.olivierboucher.utilities.TestRessourcesUtilities;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by olivierboucher on 15-04-05.
 */
public class SMEpicerieParserTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SMEpicerieParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SMEpicerieParserTest.class);
    }

    public void testGetProductPlain() throws ParseException {
        //Setup
        SMEpicerieProduct product = null;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        SMEpicerieParser parser = new SMEpicerieParser();
        Document page = Jsoup.parse(TestRessourcesUtilities.readStreamToEnd(getClass().getResourceAsStream("/SMPage.html")));
        Elements elements = page.select("tbody tr [onmouseover=this.bgColor = '#FFFFD9']");
        /*
        *           TESTS FOR EACH TYPE OF PRODUCT PRICE
        * */
        //First
        parser.setElement(elements.get(0));
        try {
            product = parser.getProduct();
        } catch (ProductParseException e) {
            fail(e.getMessage());
        }

        //Assertions
        final String DESCRIPTION1 = "Légumes surgelés Arctic Gardens";
        final String SIZE1 = "500-750 g";
        final String ORIGIN1 = "Arctic Gardens";
        final double PRICE1 = 5.00;
        final double REBATE1 = 1.29;
        final int REBATE_PER1 = 34;
        final Date START1 = formatter.parse("02/04/15");
        final Date END1 = formatter.parse("08/04/15");
        final int QTY1 = 2;

        assertEquals(DESCRIPTION1, product.getDescription());
        assertEquals(SIZE1, product.getSize());
        assertEquals(ORIGIN1, product.getOrigin());
        assertEquals(PRICE1, product.getRebate().getPrice());
        assertEquals(REBATE1, product.getRebate().getRebate());
        assertEquals(REBATE_PER1, product.getRebate().getRebate_percent());
        assertEquals(START1, product.getRebate().getStart());
        assertEquals(END1, product.getRebate().getEnd());
        assertEquals(QTY1, product.getRebate().getQuantity());

        //Second
        parser.setElement(elements.get(1));
        try {
            product = parser.getProduct();
        } catch (ProductParseException e) {
            fail(e.getMessage());
        }

        //Assertions
        final String DESCRIPTION2 = "Pizza surgelée Lève-au-four";
        final String NOTE2 = "Achetez-en 2 et obtenez gratuitement 1 sac de croustilles Ruffles (215-220 g), d'une valeur de 3,69 $";
        final String SIZE2 = "770-900 g";
        final String ORIGIN2 = "McCain";
        final double PRICE2 = 11.98;
        final double REBATE2 = 3.86;
        final int REBATE_PER2 = 39;
        final Date START2 = formatter.parse("02/04/15");
        final Date END2 = formatter.parse("08/04/15");
        final int QTY2 = 2;

        assertEquals(DESCRIPTION2, product.getDescription());
        assertEquals(NOTE2, product.getNote());
        assertEquals(SIZE2, product.getSize());
        assertEquals(ORIGIN2, product.getOrigin());
        assertEquals(PRICE2, product.getRebate().getPrice());
        assertEquals(REBATE2, product.getRebate().getRebate());
        assertEquals(REBATE_PER2, product.getRebate().getRebate_percent());
        assertEquals(START2, product.getRebate().getStart());
        assertEquals(END2, product.getRebate().getEnd());
        assertEquals(QTY2, product.getRebate().getQuantity());

        //Third
        parser.setElement(elements.get(2));
        try {
            product = parser.getProduct();
        } catch (ProductParseException e) {
            fail(e.getMessage());
        }

        //Assertions
        final String DESCRIPTION3 = "Plat surgelé Quisine";
        final String NOTE3 = "Voir variétés en magasin";
        final String SIZE3 = "600 g";
        final String ORIGIN3 = "Flamingo";
        final double PRICE3 = 5.99;
        final double REBATE3 = 4.48;
        final int REBATE_PER3 = 43;
        final Date START3 = formatter.parse("02/04/15");
        final Date END3 = formatter.parse("08/04/15");
        final int QTY3 = 1;

        assertEquals(DESCRIPTION3, product.getDescription());
        assertEquals(NOTE3, product.getNote());
        assertEquals(SIZE3, product.getSize());
        assertEquals(ORIGIN3, product.getOrigin());
        assertEquals(PRICE3, product.getRebate().getPrice());
        assertEquals(REBATE3, product.getRebate().getRebate());
        assertEquals(REBATE_PER3, product.getRebate().getRebate_percent());
        assertEquals(START3, product.getRebate().getStart());
        assertEquals(END3, product.getRebate().getEnd());
        assertEquals(QTY3, product.getRebate().getQuantity());

        //Fourth
        parser.setElement(elements.get(3));
        try {
            product = parser.getProduct();
        } catch (ProductParseException e) {
            fail(e.getMessage());
        }

        //Assertions
        final String DESCRIPTION4 = "Bière Leffe";
        final String SIZE4 = "6 x 330 ml";
        final String ORIGIN4 = "Leffe";
        final double PRICE4 = 12.99;
        final double REBATE4 = 5.00;
        final int REBATE_PER4 = 38;
        final Date START4 = formatter.parse("02/04/15");
        final Date END4 = formatter.parse("08/04/15");
        final int QTY4 = 1;

        assertEquals(DESCRIPTION4, product.getDescription());
        assertEquals(SIZE4, product.getSize());
        assertEquals(ORIGIN4, product.getOrigin());
        assertEquals(PRICE4, product.getRebate().getPrice());
        assertEquals(REBATE4, product.getRebate().getRebate());
        assertEquals(REBATE_PER4, product.getRebate().getRebate_percent());
        assertEquals(START4, product.getRebate().getStart());
        assertEquals(END4, product.getRebate().getEnd());
        assertEquals(QTY4, product.getRebate().getQuantity());

        //Fifth
        parser.setElement(elements.get(4));
        try {
            product = parser.getProduct();
        } catch (ProductParseException e) {
            fail(e.getMessage());
        }

        //Assertions
        final String DESCRIPTION5 = "Gigot d'agneau frais";
        final String NOTE5 = "Du Québec";
        final String SIZE5 = "---";
        final String ORIGIN5 = "Québec";
        final double PRICE5 = 8.99;
        final double REBATE5 = 3.67;
        final int REBATE_PER5 = 29;
        final Date START5 = formatter.parse("02/04/15");
        final Date END5 = formatter.parse("08/04/15");
        final int QTY5 = SMEpicerieRebate.QTY_POIDS;

        assertEquals(DESCRIPTION5, product.getDescription());
        assertEquals(NOTE5, product.getNote());
        assertEquals(SIZE5, product.getSize());
        assertEquals(ORIGIN5, product.getOrigin());
        assertEquals(PRICE5, product.getRebate().getPrice());
        assertEquals(REBATE5, product.getRebate().getRebate());
        assertEquals(REBATE_PER5, product.getRebate().getRebate_percent());
        assertEquals(START5, product.getRebate().getStart());
        assertEquals(END5, product.getRebate().getEnd());
        assertEquals(QTY5, product.getRebate().getQuantity());


    }
}
