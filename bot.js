var Discord = require('discord.js');
var auth = require('./auth.json');
var Deck = require('./deck');
var { displayArray, parse, standardReply, withReason } = require('./message');

const client = new Discord.Client();

client.on("ready", () => {
  console.log(`Logged in as ${client.user.tag}!`);
});

const deck = new Deck();
deck.shuffle();

const peek = (command) => {
  const n = command.options[0];
  const result = deck.peek(n);
  return standardReply(command, result);
};

const shuffle = (command) => {
  const includeJokers = command.options[0] === "wJ";
  deck.reshuffle(includeJokers);
  const message = `Deck has been shuffled${includeJokers ? " including jokers" : ""}.`;
  return withReason(message, command.comment);
};

const draw = (command) => {
  const n = command.options[0];
  if (n <= deck.length()) {
    const result = deck.draw(n);
    return standardReply(command, result);
  } else
    return withReason(`${command.word} ${command.options} - Deck has only ${deck.length()} cards remaining.`);
};

const reply = (original, message) => (
  original.channel
    .send(`${original.author} - ${message}`)
    .catch(console.err)
);

client.on("message", async msg => {
  const command = parse(msg);
  if (command.isValid) {
    if (command.word === 'draw')
      reply(msg, draw(command));
    else if (command.word === 'shuffle')
      reply(msg, shuffle(command));
    else if (command.word === 'peek')
      reply(msg, peek(command));
    else if (command.word === 'help')
      msg.channel.send("Will make HELP later");
  }
});

client.login(auth.token);
