package kr.ac.konkuk.studyandarchive;

public class ModelUser {

    //firebase databse와 동일한 이름 사용
    String name, email, search, field, image, cover, uid, bio, phone;

    public ModelUser() {
    }

    public ModelUser(String name, String email, String search, String field, String image, String cover, String uid, String bio, String phone) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.field = field;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
        this.bio = bio;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
