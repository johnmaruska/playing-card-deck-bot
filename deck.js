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

class Deck {
  // default order
  suits = ['Clubs', 'Diamonds', 'Hearts', 'Spades'];
  ranks = ['Ace', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine', 'Ten', 'Jack', 'Queen', 'King'];
  deck = [];
  drawnCards = [];
  constructor() {
    this.suits.forEach(
      (suit) => this.ranks.forEach(
        (rank) => this.deck.push(new Card(suit, rank))
      )
    );
    this.deck.push(new Card("Joker", null, true, "Black"));
    this.deck.push(new Card("Joker", null, true, "Red"));
  }

  length = () => this.deck.length

  //http://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
  shuffle = () => {
    for (let i = this.deck.length - 1; i > 0; i--) {
      let j = Math.floor(Math.random() * (i + 1));
      let swap = this.deck[i];
      this.deck[i] = this.deck[j];
      this.deck[j] = swap;
    }
  }

  reshuffle = () => {
    this.drawnCards.forEach((card) => this.deck.push(card));
    this.drawnCards = [];
    this.shuffle();
  }

  peek = (n = 1) => this.deck.slice(0, n);

  drawCard = () => {
    let card = this.deck.shift();
    this.drawnCards.push(card);
    return card;
  }

  draw = (n = 1) =>
    Array(Number.parseInt(n)).fill(null).map(this.drawCard);
}

module.exports = Deck;
