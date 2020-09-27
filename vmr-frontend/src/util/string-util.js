export function getFirstLetter(name) {
  let word = name.split(' ');
  return word[word.length - 1].charAt(0).toUpperCase();
}
