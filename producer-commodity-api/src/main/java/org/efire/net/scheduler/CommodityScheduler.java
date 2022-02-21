package org.efire.net.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.efire.net.entity.Commodity;
import org.efire.net.service.CommodityProducer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class CommodityScheduler {

    private RestTemplate restTemplate;
    private CommodityProducer commodityProducer;
    private Commodity commOne;
    private Commodity commTwo;
    private Commodity commThree;

    public CommodityScheduler(RestTemplate restTemplate, CommodityProducer commodityProducer) {
        this.restTemplate = restTemplate;
        this.commodityProducer = commodityProducer;
        var now = System.currentTimeMillis();

        commOne = new Commodity();
        commTwo = new Commodity();
        commThree = new Commodity();

        //TODO: insert commodity prices
        // commOne: 0 + 1
        // commTwo: 100
        // commThree: 95

        commOne.setTimestamp(now);
        commOne.setName("coffee");
        commOne.setPrice("0");
        commTwo.setTimestamp(now);
        commTwo.setPrice("110");
        commTwo.setName("coffee-2");
        commThree.setTimestamp(now);
        commThree.setName("coffee-3");
        commThree.setPrice("95");

    }

    //@Scheduled(fixedRate = 2000)
    public void fetchCommodities() {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8080/v1/commodities").build().toUri();
        RequestEntity requestEntity = RequestEntity.get(uri).build();
        List<Commodity> responseCommodities = restTemplate.exchange(requestEntity,
                new ParameterizedTypeReference<List<Commodity>>() {}).getBody();
        responseCommodities.stream().forEach(commodity -> {
            try {
                commodityProducer.sendCommodityEvent(commodity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This scheduled task Producer is to demonstrate the Message Filter in Kafka
     */
    @Scheduled(fixedRate = 10000)
    public void generateCommodityPrices() throws JsonProcessingException {
        try {
            var now = System.currentTimeMillis();
            commOne.setTimestamp(now);
            commTwo.setTimestamp(now);
            commThree.setTimestamp(now);

            commOne.setPrice(String.valueOf(commOne.getPriceInt() + 1));
            commTwo.setPrice(String.valueOf(commTwo.getPriceInt() - 1));
            commThree.setPrice(String.valueOf(commThree.getPriceInt() + 1));

            commodityProducer.sendCommodityEvent(commOne);
            log.info("Sent : {}", commOne);
            commodityProducer.sendCommodityEvent(commTwo);
            log.info("Sent : {}", commTwo);
            commodityProducer.sendCommodityEvent(commThree);
            log.info("Sent : {}", commThree);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
