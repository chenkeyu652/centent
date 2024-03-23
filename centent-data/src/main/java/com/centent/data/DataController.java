package com.centent.data;

import com.centent.core.bean.Result;
import com.centent.data.division.RegionDataService;
import com.centent.data.division.bean.Region;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/centent/data")
public class DataController {

    @Resource
    private RegionDataService regionDataService;

    @GetMapping("/regions")
    public Result<List<Region>> getRegions() {
        return Result.success(regionDataService.getRegions());
    }
}
