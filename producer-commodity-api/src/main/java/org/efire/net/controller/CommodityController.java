package org.efire.net.controller;

import org.efire.net.entity.Commodity;
import org.efire.net.service.CommodityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/commodities")
public class CommodityController {

    private CommodityService commodityService;

    public CommodityController(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @GetMapping
    public List<Commodity> retrieveCommodities() {
        List<Commodity> dummyCommodities = commodityService.createDummyCommodities();
        return dummyCommodities;
    }
}
