const TRIGGER = '!';
const COMMANDS = [
  'draw',
  'shuffle',
  'peek',
  'help'
];

const getCommand = (message) => message.content.slice(TRIGGER.length).split(' ')[0];

const parse = (msg) => {
  var message = msg;
  const isTriggered = message.content.startsWith(TRIGGER);
  if (!isTriggered)
    return {isValid: false};

  message = message.content.slice(TRIGGER.length);  // remove Trigger
  var [fullCommand, comment] = message.split(' ! ');
  var [command, ...opts] = fullCommand.split(' ');
  return {
    isValid: COMMANDS.includes(command),
    options: opts,
    word: command,
    comment
  };
};

const tick = (msg) => `\`${msg}\``;
const displayArray = (arr) => arr.map(tick).join(", ");
const withReason = (message, reason) => reason ? `${message} - Reason: ${tick(reason)}` : message;

const standardReply = (command, result) => {
  const message = `${command.word} ${command.options} - Result: ${displayArray(result)}`;
  return command.comment ? withReason(message, command.comment) : message;
};

module.exports = { displayArray, parse, standardReply, withReason };
