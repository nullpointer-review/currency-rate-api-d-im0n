package services;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import model.CurrencyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.cbr.web.DailyInfo;
import ru.cbr.web.DailyInfoSoap;
import ru.cbr.web.GetCursOnDateXMLResponse;

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author dbychkov
 */
@RestController
@RequestMapping("/api/rate")
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    private DailyInfoSoap port;

    @PostConstruct
    public void init(){
        DailyInfo dailyInfo = new DailyInfo();
        port = dailyInfo.getDailyInfoSoap();
    }

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    public CurrencyData getRate(@PathVariable String code){
        return getRateByDate(code, new Date());
    }

    @RequestMapping(value = "/{code}/{data}", method = RequestMethod.GET)
    public CurrencyData getRateByDate(@PathVariable("code") String code,
                                @PathVariable("data") @DateTimeFormat(pattern="yyyy-MM-dd") Date date){
        CurrencyData res = new CurrencyData();
        XMLGregorianCalendar onDate = null;
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            onDate = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);

        } catch (DatatypeConfigurationException e) {
            log.error(e.getMessage());
        }

        GetCursOnDateXMLResponse.GetCursOnDateXMLResult xmlRes = port.getCursOnDateXML(onDate);

        List<Object> list = xmlRes.getContent();
        ElementNSImpl elem = (ElementNSImpl) list.get(0);
        NodeList codeList =   elem.getElementsByTagName("VchCode");

        for (int i = 0; i < codeList.getLength(); i++){

            Node curNode = codeList.item(i);
            TextImpl textimpl = (TextImpl)curNode.getFirstChild();
            String data = textimpl.getData();

            if (data.equalsIgnoreCase(code)){
                Node parent = curNode.getParentNode();
                NodeList nodeList = parent.getChildNodes();

                for (int j = 0; j < nodeList.getLength(); j++){
                    Node currentNode = nodeList.item(j);

                    String name = currentNode.getNodeName();
                    Node currentValue = currentNode.getFirstChild();
                    String value = currentValue.getNodeValue();
                    if (name.equalsIgnoreCase("Vcurs")){
                        res.setRate(Double.valueOf(value));
                    }
                    if (name.equalsIgnoreCase("Vcode")){
                        res.setCode(value);
                    }
                }

                break;
            }
        }

        res.setDate(onDate.toGregorianCalendar().getTime());

        return res;
    }
}
