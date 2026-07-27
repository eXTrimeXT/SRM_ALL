export const generateUid = (prefix) => {
  // const random = Math.floor(Date.now() + (Math.random() * 10000000000000001))
  const random = Math.floor(Date.now() + (window.crypto.getRandomValues(new Uint8Array(1)) * 0.001 * 10000000000000001))

  return prefix ? `${prefix}_${random}` : `${random}`
}
