package kr.ac.konkuk.studyandarchive.models;

public class ModelPost {
    // 포스트 업로드 할때와 같은 name 사용

    String pId, pTitle, pDescription, pImage, pStudyTime, pUrl, pTime, uid, uName, uField, uDp, uEmail;

    public ModelPost() {
    }

    public ModelPost(String pId, String pTitle, String pDescription, String pImage, String pStudyTime, String pUrl, String pTime, String uid, String uName, String uField, String uDp, String uEmail) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pStudyTime = pStudyTime;
        this.pUrl = pUrl;
        this.pTime = pTime;
        this.uid = uid;
        this.uName = uName;
        this.uField = uField;
        this.uDp = uDp;
        this.uEmail = uEmail;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpStudyTime() {
        return pStudyTime;
    }

    public void setpStudyTime(String pStudyTime) {
        this.pStudyTime = pStudyTime;
    }

    public String getpUrl() {
        return pUrl;
    }

    public void setpUrl(String pUrl) {
        this.pUrl = pUrl;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }
}
