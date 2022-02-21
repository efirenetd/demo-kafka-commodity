package org.efire.net.service;

import org.efire.net.common.CommodityProperties;
import org.efire.net.entity.Commodity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class CommodityService {

    private CommodityProperties commodityProps;

    public CommodityService(CommodityProperties commodityProps) {
        this.commodityProps = commodityProps;
    }

    public Commodity createDummyCommodity(String name) {
        if (!commodityProps.getMap().keySet().contains(name)) {
            throw new IllegalArgumentException("Invalid commodity : "+ name);
        }
        var commodity = commodityProps.getMap().get(name);
        var newPrice = Double.parseDouble(commodity.getPrice()) * ThreadLocalRandom.current()
                .nextDouble(commodityProps.getMinAdjustment(), commodityProps.getMaxAdjustment());
        commodity.setPriceInTwoDecimal(newPrice);
        commodity.setTimestamp(System.currentTimeMillis());
        return commodity;
    }

    public List<Commodity> createDummyCommodities() {
        return commodityProps.getMap().keySet().stream()
                .map(s -> createDummyCommodity(s)).collect(Collectors.toList());
    }
}
