/**
 * 高德地图 Web端(JS API) 2.0 按需加载。
 * Key 与安全密钥来自 .env.local（VITE_AMAP_KEY / VITE_AMAP_SECURITY），不入仓库。
 * 未配置 Key 时 reject，由调用方降级（仅地图不显示，签到逻辑照常）。
 */
let loadingPromise = null

export function loadAMap() {
  if (window.AMap) return Promise.resolve(window.AMap)
  if (loadingPromise) return loadingPromise

  const key = import.meta.env.VITE_AMAP_KEY
  const security = import.meta.env.VITE_AMAP_SECURITY
  if (!key) return Promise.reject(new Error('未配置高德地图 Key'))

  // 安全密钥（JS API 2.0 需要）
  window._AMapSecurityConfig = { securityJsCode: security }

  loadingPromise = new Promise((resolve, reject) => {
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${key}&plugin=AMap.Geocoder`
    script.onload = () => resolve(window.AMap)
    script.onerror = () => {
      loadingPromise = null
      reject(new Error('高德地图脚本加载失败'))
    }
    document.head.appendChild(script)
  })
  return loadingPromise
}

/**
 * 逆地理编码：经纬度 → 中文地址。失败返回空串（不阻塞签到）。
 */
export function reverseGeocode(AMap, lng, lat) {
  return new Promise((resolve) => {
    try {
      const geocoder = new AMap.Geocoder()
      geocoder.getAddress([lng, lat], (status, result) => {
        if (status === 'complete' && result.regeocode) {
          resolve(result.regeocode.formattedAddress || '')
        } else {
          resolve('')
        }
      })
    } catch {
      resolve('')
    }
  })
}
