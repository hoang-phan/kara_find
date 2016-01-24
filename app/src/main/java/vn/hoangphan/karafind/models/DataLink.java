package vn.hoangphan.karafind.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Hoang Phan on 1/12/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataLink {
    private int vol;
    private String link;

    @JsonProperty("updated_at")
    private long updatedAt;

    private long version;

    private String stype;

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    @Override
    public String toString() {
        return "DataLink{" +
                "vol=" + vol +
                ", link='" + link + '\'' +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                ", stype='" + stype + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataLink dataLink = (DataLink) o;

        if (vol != dataLink.vol) return false;
        if (link != null ? !link.equals(dataLink.link) : dataLink.link != null) return false;
        return stype.equals(dataLink.stype);

    }

    @Override
    public int hashCode() {
        int result = vol;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + stype.hashCode();
        return result;
    }
}
