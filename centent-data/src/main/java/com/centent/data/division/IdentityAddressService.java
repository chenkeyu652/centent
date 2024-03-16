package com.centent.data.division;

import com.centent.core.exception.BusinessException;
import com.centent.data.DataService;
import com.centent.data.division.mapper.IdentityAddressMapper;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IdentityAddressService implements DataService {

    private static final Map<Integer, List<IdentityAddress>> IDENTITY_ADDRESS = Maps.newHashMap();

    @Resource
    private IdentityAddressMapper mapper;

    @Override
    public String name() {
        return "中华人民共和国行政区划";
    }

    @Override
    public void loadCache() {
        IDENTITY_ADDRESS.clear();
        // 获取全量的行政区划数据
        List<IdentityAddress> addresses = mapper.selectList(null);
        // 行政区划数据按code分组，缓存起来
        IDENTITY_ADDRESS.putAll(addresses.stream().collect(Collectors.groupingBy(IdentityAddress::getCode)));
    }

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
        if (Strings.isBlank(identity) || identity.length() < 6) {
            return null;
        }
        // 截取身份证前6位
        int areaCode = Integer.parseInt(identity.substring(0, 6));
        int cityCode = areaCode - areaCode % 100;
        int provinceCode = areaCode - areaCode % 10000;

        IdentityAddress area = this.matchBest(IDENTITY_ADDRESS.get(areaCode), identityAddress);
        IdentityAddress city = this.matchBest(IDENTITY_ADDRESS.get(cityCode), identityAddress);
        IdentityAddress province = this.matchBest(IDENTITY_ADDRESS.get(provinceCode), identityAddress);
        if (Objects.isNull(area)) {
            throw new BusinessException("无法获取身份证地址区域信息，identity：" + identity + "，identityAddress：" + identityAddress);
        }
        if (Objects.isNull(province)) {
            throw new BusinessException("无法获取身份证地址省份信息，identity：" + identity + "，identityAddress：" + identityAddress);
        }
        if (Objects.isNull(city)) {
            city = province; // 直辖市特殊处理
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

    private IdentityAddress matchBest(List<IdentityAddress> matches, String identityAddress) {
        if (CollectionUtils.isEmpty(matches)) {
            return null;
        }
        // 如果只有一个匹配项，直接返回
        if (matches.size() == 1) {
            return matches.get(0);
        }
        // 根据身份证完整地址检查匹配
        for (IdentityAddress match : matches) {
            if (identityAddress.contains(match.getName())) {
                return match;
            }
        }
        return null;
    }
}
