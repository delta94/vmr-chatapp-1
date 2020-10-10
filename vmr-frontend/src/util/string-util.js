import moment from 'moment';

export function getFirstLetter(name) {
  let word = name.split(' ');
  return word[word.length - 1].charAt(0).toUpperCase();
}

export function moneyFormat(value) {
  return `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
}

export function timestampSecond2String(timestamp) {
  return moment(timestamp * 1000).format('HH:mm - DD/MM/YYYY');
}

export function timestampSecond2Month(timestamp) {
  return moment(timestamp * 1000).format('MM/YYYY');
}
