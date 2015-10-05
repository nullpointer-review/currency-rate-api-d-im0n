package model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author dbychkov
 */
public class CurrencyData {
    private String code;
    private double rate;
    private Date date;

    public CurrencyData(){}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @JsonFormat(pattern="yyyy-MM-dd")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
