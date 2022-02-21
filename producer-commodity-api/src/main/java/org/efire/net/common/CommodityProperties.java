package org.efire.net.common;

import org.efire.net.entity.Commodity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "commodity")
public class CommodityProperties {
    private String inputTopic;
    private Double maxAdjustment;
    private Double minAdjustment;
    private Map<String, Commodity> map = new HashMap<>();

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public Double getMaxAdjustment() {
        return maxAdjustment;
    }

    public void setMaxAdjustment(Double maxAdjustment) {
        this.maxAdjustment = maxAdjustment;
    }

    public Double getMinAdjustment() {
        return minAdjustment;
    }

    public void setMinAdjustment(Double minAdjustment) {
        this.minAdjustment = minAdjustment;
    }

    public Map<String, Commodity> getMap() {
        return map;
    }

    public void setMap(Map<String, Commodity> map) {
        this.map = map;
    }
}
