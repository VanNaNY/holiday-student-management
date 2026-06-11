/**
 * 获取当前定位（浏览器 Geolocation）。
 * 返回 Promise<{ lat, lng }>，失败 reject Error。
 * 注意：非 https/localhost 环境浏览器可能拒绝定位。
 */
export function getLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('当前浏览器不支持定位'))
      return
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => resolve({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
      (err) => {
        const msgs = {
          1: '定位权限被拒绝，请在浏览器中允许定位',
          2: '无法获取位置信息',
          3: '定位超时，请重试'
        }
        reject(new Error(msgs[err.code] || '定位失败'))
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    )
  })
}
