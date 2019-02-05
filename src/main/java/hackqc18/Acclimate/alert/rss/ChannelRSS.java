package hackqc18.Acclimate.alert.rss;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class ChannelRSS {

    private String title;
    private String description;
    private String link;
    private String pubDate;
    private ImageRSS image;
    @JacksonXmlElementWrapper(useWrapping = false)
    private ItemRSS[] item;


    public ChannelRSS() {

    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getLink() {
        return link;
    }


    public void setLink(String link) {
        this.link = link;
    }


    public String getPubDate() {
        return pubDate;
    }


    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }


    public ImageRSS getImage() {
        return image;
    }


    public void setImage(ImageRSS image) {
        this.image = image;
    }


    public ItemRSS[] getItem() {
        return item;
    }


    public void setItem(ItemRSS[] item) {
        this.item = item;
    }
}
