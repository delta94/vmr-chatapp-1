export function getFirstLetter(name) {
  let word = name.split(' ');
  return word[word.length - 1].charAt(0).toUpperCase();
}

export function moneyFormat(value) {
  return `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
}