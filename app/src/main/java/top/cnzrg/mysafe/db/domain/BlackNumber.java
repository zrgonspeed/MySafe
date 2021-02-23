package top.cnzrg.mysafe.db.domain;

/**
 * FileName: BlackNumber
 * Author: ZRG
 * Date: 2019/6/22 19:39
 */
public class BlackNumber {
    private String phone;
    private String mode;

    public BlackNumber(String phone, String mode) {
        this.phone = phone;
        this.mode = mode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackNumber{" +
                "phone='" + phone + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
