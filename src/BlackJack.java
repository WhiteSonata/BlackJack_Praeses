import java.util.*;

public class BlackJack {

    public static void main(String[] args) {

        int betAmount = 1;
        double payout = 20.00;
        boolean getInsurance = false;
        boolean playerSplit = false;

        List<Card> deck = initializeDeck();
        Collections.shuffle(deck);

        List<Card> playerHand = new ArrayList<>();
        List<Card> playerHandSplit = new ArrayList<>();
        List<Card> dealerHand = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to $" + betAmount + " Blackjack!");
        System.out.printf("Current Total: $%.2f%n", payout);

        // Initial Deal
        playerHand.add(drawCard(deck));
        dealerHand.add(drawCard(deck));
        playerHand.add(drawCard(deck));
        dealerHand.add(drawCard(deck));

        // Insurance only happens if dealer shows Ace
        if (dealerHand.get(0).getRank().equals("Ace")) {
            System.out.println("\nDealer shows: " + dealerHand.get(0).display());
            System.out.println("Would you like insurance for $0.50? [Y]es or [N]o");

            while (true) {
                String action = scanner.nextLine().trim().toLowerCase();

                if (action.equals("y")) {
                    getInsurance = true;
                    break;
                } else if (action.equals("n")) {
                    break;
                } else {
                    System.out.println("Invalid input. Type 'y' or 'n'.");
                }
            }
        }
        // If dealer has blackjack, resolve immediately
        if (hasBlackjack(dealerHand)) {
            System.out.println("\nDealer has blackjack.");
            showHand("Dealer's", dealerHand);
            showHand("Your", playerHand);

            if (getInsurance) {
                System.out.println("Insurance pays 2:1. You win $1 from insurance.");
                payout += 1.00;
            }

            if (hasBlackjack(playerHand)) {
                System.out.println("You also have blackjack. Main bet pushes.");
            } else {
                System.out.println("Your main bet loses.");
                payout -= betAmount;
            }

            System.out.printf("Current Total: $%.2f%n", payout);
            return;
        } else if (getInsurance) {
            System.out.println("Dealer does not have blackjack. Insurance bet loses.");
            payout -= 0.50;
        }

        // Player's Turn
        while (true) {
            System.out.println("\n--- Your Turn ---");
            showHand("Your", playerHand);
            System.out.println("Dealer shows: " + dealerHand.get(0).display());

            int playerTotal = getHandValue(playerHand);

            if (playerTotal > 21) {
                System.out.println("You busted. Dealer wins.");
                payout -= betAmount;
                System.out.printf("Current Total: $%.2f%n", payout);
                return;
            }

            boolean canSplit = playerHand.size() == 2 &&
                    playerHand.get(0).getValue() == playerHand.get(1).getValue();

            if (canSplit && !playerSplit) {
                System.out.print("Do you want to [h]it, [s]tand, [d]ouble down, or s[p]lit? ");
            } else {
                System.out.print("Do you want to [h]it, [s]tand, or [d]ouble down? ");
            }

            String action = scanner.nextLine().trim().toLowerCase();

            if (action.equals("h")) {
                playerHand.add(drawCard(deck));
            } else if (action.equals("s")) {
                break;
            } else if (action.equals("d")) {
                playerHand.add(drawCard(deck));
                payout -= betAmount; // doubling your wager
                break;
            } else if (action.equals("p") && canSplit && !playerSplit) {
                playerSplit = true;

                // Move second card to split hand
                playerHandSplit.add(playerHand.remove(1));

                // Give each hand one new card
                playerHand.add(drawCard(deck));
                playerHandSplit.add(drawCard(deck));

                System.out.println("Hand split.");
                showHand("Your 1st", playerHand);
                showHand("Your 2nd", playerHandSplit);

                // This is a simple version where second hand will automatically stand

            } else {
                System.out.println("Invalid input.");
            }
        }

        // Dealer's Turn
        System.out.println("\n--- Dealer's Turn ---");
        while (getHandValue(dealerHand) < 17) {
            dealerHand.add(drawCard(deck));
        }

        // Show Final Hands
        showHand("Your", playerHand);

        if (playerSplit) {
            showHand("Your 2nd", playerHandSplit);
        }

        showHand("Dealer's", dealerHand);

        int playerFinal = getHandValue(playerHand);
        int playerSplitFinal = getHandValue(playerHandSplit);
        int dealerFinal = getHandValue(dealerHand);

        System.out.println("\nFinal Totals -> You: " + playerFinal + " | Dealer: " + dealerFinal);

        // Resolve main hand
        if (playerFinal > 21) {
            System.out.println("Player hand busted. Dealer wins.");
            payout -= betAmount;
        } else if (dealerFinal > 21 || playerFinal > dealerFinal) {
            System.out.println("Player hand wins.");
            payout += betAmount;
        } else if (dealerFinal > playerFinal) {
            System.out.println("Player hand loses.");
            payout -= betAmount;
        } else {
            System.out.println("Player hand pushes.");
        }

        // Resolve split hand, if any
        if (playerSplit) {
            if (playerSplitFinal > 21) {
                System.out.println("Split hand busted. Dealer wins.");
                payout -= betAmount;
            } else if (dealerFinal > 21 || playerSplitFinal > dealerFinal) {
                System.out.println("Split hand wins.");
                payout += betAmount;
            } else if (dealerFinal > playerSplitFinal) {
                System.out.println("Split hand loses.");
                payout -= betAmount;
            } else {
                System.out.println("Split hand pushes.");
            }
        }

        System.out.printf("Current Total: $%.2f%n", payout);
    }

    // --- Helper Methods ---

    private static List<Card> initializeDeck() {
        List<Card> deck = new ArrayList<>();

        String[] suits = {"♠", "♥", "♦", "♣"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                int value;

                if (rank.equals("Ace")) {
                    value = 11;
                } else if (rank.equals("Jack") || rank.equals("Queen") || rank.equals("King")) {
                    value = 10;
                } else {
                    value = Integer.parseInt(rank);
                }

                deck.add(new Card(suit, rank, value));
            }
        }

        return deck;
    }

    private static Card drawCard(List<Card> deck) {
        return deck.remove(0);
    }

    private static void showHand(String owner, List<Card> hand) {
        System.out.print(owner + " Hand: ");

        for (Card card : hand) {
            System.out.print(card.display() + " ");
        }

        System.out.println("(Total: " + getHandValue(hand) + ")");
    }

    private static int getHandValue(List<Card> hand) {
        int total = 0;
        int aces = 0;

        for (Card card : hand) {
            total += card.getValue();
            if (card.getRank().equals("Ace")) {
                aces++;
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }
    private static boolean hasBlackjack(List<Card> hand) {
        return hand.size() == 2 && getHandValue(hand) == 21;
    }

    // --- Card Class ---

    static class Card {
        private final String suit;
        private final String rank;
        private final int value;

        public Card(String suit, String rank, int value) {
            this.suit = suit;
            this.rank = rank;
            this.value = value;
        }

        public String getRank() {
            return rank;
        }

        public int getValue() {
            return value;
        }

        public String display() {
            return rank + suit;
        }
    }
}