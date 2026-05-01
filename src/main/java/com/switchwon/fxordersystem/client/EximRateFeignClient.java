package com.switchwon.fxordersystem.client;

import com.switchwon.fxordersystem.dto.EximRateItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "eximRateClient", url = "${fx.provider.base-url}")
public interface EximRateFeignClient {

    @GetMapping("${fx.provider.latest-rate-path}")
    List<EximRateItem> getLatestRates(
            @RequestParam("authkey") String authKey,
            @RequestParam("searchdate") String searchDate,
            @RequestParam("data") String data
    );
}
