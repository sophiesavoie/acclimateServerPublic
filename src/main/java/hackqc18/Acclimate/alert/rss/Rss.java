package hackqc18.Acclimate.alert.rss;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "rss")
public class Rss {

    @JacksonXmlProperty(isAttribute = true)
    private String version;

    private ChannelRSS channel;


    public Rss() {

    }


    public ChannelRSS getChannel() {
        return channel;
    }


    public void setChannel(ChannelRSS channel) {
        this.channel = channel;
    }


    public String getVersion() {
        return version;
    }


    public void setVersion(String version) {
        this.version = version;
    }
}
