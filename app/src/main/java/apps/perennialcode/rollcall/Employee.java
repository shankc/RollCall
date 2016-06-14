package apps.perennialcode.rollcall;

/**
 * Created by mayuukhvarshney on 26/05/16.
 */
public class Employee {

    private int RegID;
    private String UserName;
    private String InStatus;
    private String OutStatus;
    private String InTime;
    private String OutTime;

    public void setRegID(int RegiID) {
        this.RegID = RegiID;

    }

    public void setUserName(String username) {
        this.UserName = username;
    }

    public void setInStatus(String instatus) {
        this.InStatus = instatus;
    }

    public void setOutStatus(String outstatus) {
        this.OutStatus = outstatus;
    }

    public void setInTime(String intime) {
        this.InTime = intime;
    }

    public void setOutTime(String outtime) {
        this.OutTime = outtime;
    }

    public int getRegID() {
        return this.RegID;
    }

    public String getUserName() {
        return this.UserName;
    }

    public String getInStatus() {
        return this.InStatus;

    }

    public String getOutStatus() {
        return this.OutStatus;
    }

    public String getInTime() {
        return this.InTime;
    }

    public String getOutTime() {
        return this.OutTime;
    }
}