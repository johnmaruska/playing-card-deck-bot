var Discord = require('discord.js');
var auth = require('./auth.json');
var commands = require('./command');
var message = require('./message');
var Deck = require('./deck');

const client = new Discord.Client();

client.on("ready", () => {
  console.log(`Logged in as ${client.user.tag}!`);
});

const deck = new Deck();
deck.shuffle();

const peek = (command) => commands.peek(deck, command);
const shuffle = (command) => commands.shuffle(deck, command);
const draw = (command) => commands.draw(deck, command);

const reply = (original, message) => (
  original.channel
    .send(`${original.author} - ${message}`)
    .catch(console.err)
);

client.on("message", async msg => {
  const command = message.parse(msg);
  if (command.isValid) {
    // Deck commands
    if (command.word === 'draw')
      reply(msg, draw(command));
    else if (command.word === 'shuffle')
      reply(msg, shuffle(command));
    else if (command.word === 'peek')
      reply(msg, peek(command));
    // Dice commands
    else if (command.word === 'roll')
      reply(msg, commands.roll(command));
    else if (command.word === 'help')
      msg.channel.send("Will make HELP later");
  }
});

client.login(auth.token);
