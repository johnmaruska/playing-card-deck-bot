const isRed = (suit) => suit == "Hearts" || "Diamonds";

class Card {
  constructor(suit, rank, joker = false, color = "Default") {
    this.joker = joker;
    this.suit = suit;
    this.rank = rank;
    if(color == "Default")
      this.color = isRed(suit) ? "Red" : "Black";
    else
      this.color = color;
  }

  toString() {
    if(this.joker)
      return `${this.color} Joker`;
    else
      return `${this.rank} of ${this.suit}`;
  }
}

const SUITS = ['Clubs', 'Diamonds', 'Hearts', 'Spades'];
const RANKS = ['Ace', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine', 'Ten', 'Jack', 'Queen', 'King'];

class Deck {
  // default order
  constructor() {
    this.deck = [];
    this.drawnCards = [];
    SUITS.forEach(
      (suit) => RANKS.forEach(
        (rank) => this.deck.push(new Card(suit, rank))
      )
    );
    this.deck.push(new Card("Joker", null, true, "Black"));
    this.deck.push(new Card("Joker", null, true, "Red"));

    // caveman bindings
    this.length = this.length.bind(this);
    this.shuffle = this.shuffle.bind(this);
    this.reshuffle = this.reshuffle.bind(this);
    this.peek = this.peek.bind(this);
    this.drawCard = this.drawCard.bind(this);
    this.draw = this.draw.bind(this);
  }

  length() { return this.deck.length; }

  //http://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
  shuffle() {
    for (let i = this.deck.length - 1; i > 0; i--) {
      let j = Math.floor(Math.random() * (i + 1));
      let swap = this.deck[i];
      this.deck[i] = this.deck[j];
      this.deck[j] = swap;
    }
    return this.deck;
  }

  reshuffle() {
    this.drawnCards.forEach((card) => this.deck.push(card));
    this.drawnCards = [];
    return this.shuffle();
  }

  peek(n = 1) { return this.deck.slice(0, n); }

  drawCard() {
    let card = this.deck.shift();
    this.drawnCards.push(card);
    return card;
  }

  draw(n = 1) {
    return Array(Number.parseInt(n)).fill(null).map(this.drawCard);
  }
}

module.exports = Deck;
