package Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pays {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("onlineprice")
    @Expose
    private String onlineprice;
    @SerializedName("bookprice")
    @Expose
    private String bookprice;

    public Pays(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOnlineprice() {
        return onlineprice;
    }

    public void setOnlineprice(String onlineprice) {
        this.onlineprice = onlineprice;
    }

    public String getBookprice() {
        return bookprice;
    }

    public void setBookprice(String bookprice) {
        this.bookprice = bookprice;
    }
}
