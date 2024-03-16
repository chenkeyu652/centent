package com.centent.data.division;

import com.centent.core.exception.BusinessException;
import com.centent.data.DataService;
import com.centent.data.division.mapper.IdentityAddressMapper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class IdentityAddressService implements DataService {

    private static final List<IdentityAddress> ALL = Lists.newArrayList();

    @Resource
    private IdentityAddressMapper mapper;

    /**
     * 身份证号码+完整的身份证地址，较为精准的匹配行政区划
     *
     * @param identity        身份证号码
     * @param identityAddress 身份证地址
     * @return 县级行政区划信息，内部包含了市/省级行政区划信息
     * @throws BusinessException 匹配失败将会抛出异常
     */
    public IdentityAddress matchArea(String identity, String identityAddress) throws BusinessException {
        if (Strings.isBlank(identityAddress)) {
            identityAddress = "（未传入身份证地址）";
        }
        if (Strings.isBlank(identity) || identity.length() < 11) {
            return null;
        }
        // 截取身份证前6位
        int areaCode = Integer.parseInt(identity.substring(0, 6));
        int cityCode = areaCode - areaCode % 100;
        int provinceCode = areaCode - areaCode % 10000;

        Map<Integer, List<IdentityAddress>> matches = this.find(areaCode, cityCode, provinceCode);
        IdentityAddress area = this.matchBest(matches.get(areaCode), identityAddress);
        IdentityAddress city = this.matchBest(matches.get(cityCode), identityAddress);
        IdentityAddress province = this.matchBest(matches.get(provinceCode), identityAddress);
        if (Objects.isNull(area)) {
            throw new BusinessException("无法获取身份证地址区域信息，identity：" + identity + "，identityAddress：" + identityAddress);
        }
        if (Objects.isNull(city)) {
            throw new BusinessException("无法获取身份证地址市区信息，identity：" + identity + "，identityAddress：" + identityAddress);
        }
        if (Objects.isNull(province)) {
            throw new BusinessException("无法获取身份证地址省份信息，identity：" + identity + "，identityAddress：" + identityAddress);
        }
        area.setCity(city);
        area.setProvince(province);
        return area;
    }

    /**
     * 身份证号码匹配行政区划
     *
     * @param identity 身份证号码
     * @return 县级行政区划信息，内部包含了市/省级行政区划信息
     * @throws BusinessException 匹配失败将会抛出异常
     */
    public IdentityAddress matchArea(String identity) throws BusinessException {
        return this.matchArea(identity, null);
    }

    private Map<Integer, List<IdentityAddress>> find(int areaCode, int cityCode, int provinceCode) {
        Map<Integer, List<IdentityAddress>> result = Map.of(
                areaCode, new ArrayList<>(),
                cityCode, new ArrayList<>(),
                provinceCode, new ArrayList<>()
        );
        for (IdentityAddress address : ALL) {
            if (Objects.equals(address.getCode(), areaCode)) {
                result.get(areaCode).add(address);
            } else if (Objects.equals(address.getCode(), cityCode)) {
                result.get(cityCode).add(address);
            } else if (Objects.equals(address.getCode(), provinceCode)) {
                result.get(provinceCode).add(address);
            }
        }
        // 直辖市使用省份区划作为市区划
        if (CollectionUtils.isEmpty(result.get(cityCode))) {
            result.get(cityCode).addAll(result.get(provinceCode));
        }
        return result;
    }

    private IdentityAddress matchBest(List<IdentityAddress> matches, String identityAddress) {
        if (CollectionUtils.isEmpty(matches)) {
            return null;
        }
        // 如果只有一个匹配项，直接返回
        if (matches.size() == 1) {
            return matches.get(0);
        }
        // 如果年份匹配仍然失败，根据身份证完整地址匹配
        for (IdentityAddress match : matches) {
            if (identityAddress.contains(match.getName())) {
                return match;
            }
        }
        return null;
    }

    @Override
    public String name() {
        return "中华人民共和国行政区划";
    }

    @Override
    public void loadCache() {
        ALL.clear();
        ALL.addAll(mapper.selectList(null));
    }
}
