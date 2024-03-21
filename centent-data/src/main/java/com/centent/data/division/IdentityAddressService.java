package com.centent.data.division;

import com.centent.core.exception.BusinessException;
import com.centent.data.DataService;
import com.centent.data.division.mapper.IdentityAddressChangeMapper;
import com.centent.data.division.mapper.IdentityAddressMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IdentityAddressService implements DataService {

    private static final Map<Integer, List<IdentityAddress>> IDENTITY_ADDRESS = Maps.newHashMap();

    @Resource
    private IdentityAddressMapper mapper;

    @Resource
    private IdentityAddressChangeMapper changeMapper;

    @Override
    public String name() {
        return "中华人民共和国行政区划全数据";
    }

    @Override
    public void load() {
        IDENTITY_ADDRESS.clear();
        // 获取全量的行政区划数据
        List<IdentityAddress> addresses = mapper.selectList(null);
        List<IdentityAddressChange> allChanges = changeMapper.selectList(null);
        // 行政区划数据按code分组，缓存起来
        IDENTITY_ADDRESS.putAll(addresses.stream().collect(Collectors.groupingBy(IdentityAddress::getCode)));

        for (IdentityAddress address : addresses) {
            if (Objects.nonNull(address.getEnds())) {
                if (address.getCode() % 100 == 0) {
                    // 忽略市级及以上行政区域
                    continue;
                }

                List<IdentityAddressChange> changes = allChanges.parallelStream()
                        .filter(change -> Objects.equals(change.getCode(), address.getCode()) && Objects.equals(change.getTime(), address.getEnds()))
                        .toList();
                Set<IdentityAddress> collected = new HashSet<>();
                if (changes.isEmpty()) {
                    // 尝试直接找新的记录
                    collected = addresses.parallelStream()
                            .filter(a -> Objects.equals(a.getStart(), address.getEnds())
                                    && Objects.equals(a.getName(), address.getName()))
                            .collect(Collectors.toSet());
                    if (collected.isEmpty()) {
                        log.debug("已废止行政区划change表无记录，也无法匹配到新的记录，[{}]{}", address.getCode(), address.getName());
                    }
                    // 尝试模糊匹配新的记录
                    String prefixName = address.getName().substring(0, 2);
                    collected = addresses.parallelStream()
                            .filter(a -> Objects.equals(a.getStart(), address.getEnds()) && a.getName().contains(prefixName))
                            .collect(Collectors.toSet());
                    if (collected.isEmpty()) {
                        log.debug("已废止行政区划change表无记录，即使模糊匹配也无法匹配到新的记录，[{}]{}", address.getCode(), address.getName());
                    }
                } else {
                    for (IdentityAddressChange change : changes) {
                        Set<IdentityAddress> na = addresses.parallelStream()
                                .filter(a -> Objects.equals(a.getCode(), change.getNewCode()) && a.getStart() <= change.getTime())
                                .collect(Collectors.toSet());
                        if (na.isEmpty()) {
                            log.debug("已废止行政区划通过change表数据找不到新的行政区划，address[{}]{}，change{}={}",
                                    address.getCode(), address.getName(), change.getCode(), change.getTime());
                        }
                        collected.addAll(na);
                    }
                }
                address.setNext(collected);
            }
        }
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
        if (Objects.isNull(area)) {
            throw new BusinessException("无法获取身份证地址区域信息，identity：" + identity + "，identityAddress：" + identityAddress);
        }
        // 之前已经计算过了，直接返回数据即可
        if (Objects.nonNull(area.getProvince())) {
            return area;
        }

        IdentityAddress city = this.matchBest(IDENTITY_ADDRESS.get(cityCode), identityAddress);
        IdentityAddress province = this.matchBest(IDENTITY_ADDRESS.get(provinceCode), identityAddress);
        if (Objects.isNull(province)) {
            throw new BusinessException("无法获取身份证地址省份信息，identity：" + identity + "，identityAddress：" + identityAddress);
        }
        if (Objects.isNull(city)) {
            city = province; // 直辖市特殊处理
        }
        area.setCity(city);
        area.setProvince(province);

        Set<IdentityAddress> newest = this.findNewest(area);
        if ((newest.size() == 1 && Objects.equals(newest.iterator().next(), area))) {
            area.setCurrent(newest);
            return area;
        }

        for (IdentityAddress newArea : newest) {
            if (Objects.equals(newArea, area)) {
                continue;
            }
            assert newArea != null;
            cityCode = newArea.getCode() - newArea.getCode() % 100;
            provinceCode = newArea.getCode() - newArea.getCode() % 10000;
            IdentityAddress newCity = this.matchBest(IDENTITY_ADDRESS.get(cityCode), identityAddress);
            IdentityAddress newProvince = this.matchBest(IDENTITY_ADDRESS.get(provinceCode), identityAddress);
            if (Objects.isNull(newProvince)) {
                throw new BusinessException("无法获取身份证地址省份信息，identity1：" + identity + "，identityAddress：" + identityAddress);
            }
            if (Objects.isNull(newCity)) {
                newCity = newProvince; // 直辖市特殊处理
            }
            newArea.setCity(newCity);
            newArea.setProvince(newProvince);
        }
        area.setCurrent(newest);
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

    private Set<IdentityAddress> findNewest(IdentityAddress current) {
        if (CollectionUtils.isEmpty(current.getNext())) {
            return Sets.newHashSet(current);
        }
        return current.getNext().stream()
                .map(this::findNewest)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
