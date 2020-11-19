package kr.ac.konkuk.studyandarchive.models;

public class ModelComment {
    String cId, comment, timeStamp, uid, uEmail, uDp, uName, uField;

    public ModelComment() {
    }

    public ModelComment(String cId, String comment, String timestamp, String uid, String uEmail, String uDp, String uName, String uField) {
        this.cId = cId;
        this.comment = comment;
        this.timeStamp = timestamp;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
        this.uField = uField;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timeStamp;
    }

    public void setTimestamp(String timestamp) {
        this.timeStamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuField() {
        return uField;
    }

    public void setuField(String uField) {
        this.uField = uField;
    }
}
