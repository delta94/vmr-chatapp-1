const colorList = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#007aff'];

export function randColor() {
  return colorList[Math.round(Math.random() * 10) % colorList.length];
}

export function getColor(i) {
  return colorList[i % colorList.length];
}
