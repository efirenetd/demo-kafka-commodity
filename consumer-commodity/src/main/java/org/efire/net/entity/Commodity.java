package org.efire.net.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Commodity {
    private String name;
    private String price;
    private String measurement;
    private long timestamp;

    public void setPriceInTwoDecimal(double price) {
        this.setPrice(String.valueOf(Math.round(price * 100d) / 100d));
    }

    public int getPriceInt() {
        return Integer.parseInt(this.getPrice());
    }
}
