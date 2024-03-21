package com.centent.data.division;

import com.centent.data.DataService;
import com.centent.data.division.bean.Region;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RegionDataService implements DataService {

    private static final String ORIGIN_FILE = "classpath:data/administrative_regions_2023.txt";

    private static final List<Region> REGIONS = new ArrayList<>();

    private static final Map<Integer, Region> REGIONS_AREA = new HashMap<>();

    @Resource
    private ResourceLoader resourceLoader;

    @Override
    public String name() {
        return "中华人民共和国行政区划最新数据";
    }

    @Override
    public void load() throws IOException {
        Map<Integer, Region> PROVINCES = new LinkedHashMap<>();
        Map<Integer, Region> CITIES = new LinkedHashMap<>();

        // 加载资源文件
        Map<Integer, String> data = new LinkedHashMap<>();
        org.springframework.core.io.Resource resource = resourceLoader.getResource(ORIGIN_FILE);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\t");
                if (split.length != 2) {
                    System.out.println("错误数据：" + line);
                }
                data.put(Integer.parseInt(split[0]), split[1]);
            }
        }

        for (Entry<Integer, String> entry : data.entrySet()) {
            int code = entry.getKey();
            String name = entry.getValue();

            if (code % 10000 == 0) {
                process(PROVINCES, code, name);
            } else if (code % 100 == 0) {
                int provinceCode = code - code % 10000;
                Region province = process(PROVINCES, provinceCode, null);
                Region city = process(CITIES, code, name);
                province.getChildren().add(city);
                city.setParent(province);
            } else {
                int provinceCode = code - code % 10000;
                int cityCode = code - code % 100;
                Region province = process(PROVINCES, provinceCode, null);
                Region area = process(REGIONS_AREA, code, name);

                Region city;
                if (data.containsKey(cityCode)) {
                    city = process(CITIES, cityCode, null);
                } else {
                    city = process(CITIES, provinceCode, null);
                }
                province.getChildren().add(city);
                city.getChildren().add(area);
                area.setParent(city);
                city.setParent(province);
            }
        }

        CITIES.values().stream()
                .filter(city -> Objects.equals(city.getCode(), city.getParent().getCode()))
                .forEach(city -> city.setName(city.getParent().getName()));

        REGIONS.addAll(PROVINCES.values());
    }

    private Region process(Map<Integer, Region> regions, Integer code, String name) {
        Region region = regions.computeIfAbsent(code, Region::new);
        if (Strings.isNotBlank(name)) {
            region.setName(name);
        }
        return region;
    }

    public List<Region> getRegions() {
        return REGIONS;
    }

    public Region getArea(Integer code) {
        if (Objects.isNull(code) || !REGIONS_AREA.containsKey(code)) {
            return null;
        }
        return REGIONS_AREA.get(code);
    }
}
