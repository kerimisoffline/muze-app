package mahmutcankahya.com.mzeappdemo;

public class Eserler {
    private String  eserAdi,eserDetay,eserResim,eserQrCode;

    public Eserler(String eserAdi, String eserDetay, String eserResim) {
        this.eserAdi = eserAdi;
        this.eserDetay = eserDetay;
        this.eserResim = eserResim;
    }



    public String getEserAdi() {
        return eserAdi;
    }

    public void setEserAdi(String eserAdi) {
        this.eserAdi = eserAdi;
    }

    public String getEserDetay() {
        return eserDetay;
    }

    public void setEserDetay(String eserDetay) {
        this.eserDetay = eserDetay;
    }

    public String getEserResim() {
        return eserResim;
    }

}
