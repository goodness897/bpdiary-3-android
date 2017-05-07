package kr.co.openit.bpdiary.services;

public class CsvBean {

    private String name;

    private String address;

    private String telNo;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the telNo
     */
    public String getTelNo() {
        return telNo;
    }

    /**
     * @param telNo the telNo to set
     */
    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();
        str.append("name=").append(name).append(", ");
        str.append("address=").append(address).append(", ");
        str.append("telNo=").append(telNo);

        return str.toString();
    }

    public String csvString() {

        StringBuilder str = new StringBuilder();
        str.append(name).append(",");
        str.append(address).append(",");
        str.append(telNo);

        return str.toString();
    }
}
