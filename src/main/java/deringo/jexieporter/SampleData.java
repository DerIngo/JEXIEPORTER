package deringo.jexieporter;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SampleData {
    private String name;
    private String phone;
    private String email;
    private String address;
    private String postalZip;
    private String region;
    private String country;
    private String list;
    private String text;
    private String numberrange;
    private String currency;
    private String alphanumeric;
    
    public SampleData() {
    }

    public SampleData(List<String> list) {
        this(
                list.get(0),
                list.get(1),
                list.get(2),
                list.get(3),
                list.get(4),
                list.get(5),
                list.get(6),
                list.get(7),
                list.get(8),
                list.get(9),
                list.get(10),
                list.get(11));
    }
    
    public SampleData(String name, String phone, String email, String address, String postalZip, String region,
            String country, String list, String text, String numberrange, String currency, String alphanumeric) {
        super();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.postalZip = postalZip;
        this.region = region;
        this.country = country;
        this.list = list;
        this.text = text;
        this.numberrange = numberrange;
        this.currency = currency;
        this.alphanumeric = alphanumeric;
    }

    public String[] toStringArray() {
        String[] array = {name, phone, email, address, postalZip, region, country, list, text, numberrange, currency, alphanumeric};
        return array;
    }


    @Override
    public String toString() {
        return String.format(
                "SampleData [name=%s, phone=%s, email=%s, address=%s, postalZip=%s, region=%s, country=%s, list=%s, text=%s, numberrange=%s, currency=%s, alphanumeric=%s]",
                name, phone, email, address, postalZip, region, country, list, text, numberrange, currency,
                alphanumeric);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalZip() {
        return postalZip;
    }

    public void setPostalZip(String postalZip) {
        this.postalZip = postalZip;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNumberrange() {
        return numberrange;
    }

    public void setNumberrange(String numberrange) {
        this.numberrange = numberrange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAlphanumeric() {
        return alphanumeric;
    }

    public void setAlphanumeric(String alphanumeric) {
        this.alphanumeric = alphanumeric;
    }
}
