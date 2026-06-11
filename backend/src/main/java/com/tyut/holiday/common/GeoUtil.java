package com.tyut.holiday.common;

/**
 * 地理计算工具：Haversine 公式求两经纬度间距离（米）。
 */
public final class GeoUtil {

    private GeoUtil() {
    }

    /** 地球平均半径（米） */
    private static final double EARTH_RADIUS = 6_371_000d;

    /**
     * 计算两点间球面距离（米）。
     */
    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double dLat = radLat2 - radLat1;
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
