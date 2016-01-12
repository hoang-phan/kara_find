package vn.hoangphan.karafind.models.net;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import vn.hoangphan.karafind.models.DataLink;

/**
 * Created by eastagile-tc on 1/12/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataLinksResponse {

    @JsonProperty("data_links")
    private List<DataLink> dataLinks;

    public List<DataLink> getDataLinks() {
        return dataLinks;
    }

    public void setDataLinks(List<DataLink> dataLinks) {
        this.dataLinks = dataLinks;
    }
}
