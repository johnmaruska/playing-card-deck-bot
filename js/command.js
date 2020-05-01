var { deckTooSmall, displayArray, standardReply, withReason } = require('./message');

const peek = (deck, command) => {
  const n = command.options[0];
  if (n <= deck.length())
    return standardReply(command, displayArray(deck.peek(n)));
  else
    return deckTooSmall(command, deck.length());
};

const shuffle = (deck, command) => {
  const includeJokers = command.options[0] === "wJ";
  deck.reshuffle(includeJokers);
  const message = `Deck has been shuffled${includeJokers ? " including jokers" : ""}.`;
  return withReason(message, command.comment);
};

const draw = (deck, command) => {
  console.log("deck " + deck);
  console.log("command " + command);
  const n = command.options[0];
  if (n <= deck.length())
    return standardReply(command, displayArray(deck.draw(n)));
  else
    return deckTooSmall(command, deck.length());
};

module.exports = { draw, peek, shuffle };
