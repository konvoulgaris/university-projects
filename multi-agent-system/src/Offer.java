import java.util.ArrayList;

public class Offer {
  public String agent;
  public int distance = Integer.MAX_VALUE;
  public int strength = Integer.MAX_VALUE;

  public Offer() {}

  public Offer(String agent, int distance, int strength) {
    this.agent = agent;
    this.distance = distance;
    this.strength = strength;
  }

  public int getScore() {
    return distance - strength;
  }

  public static Offer getOffer(String agent, String message) {

    if(message.contains("Offer:")) {
      String sOffer = message.split(":")[1];
      int distance = Integer.parseInt(sOffer.split(",")[0]);
      int strength = Integer.parseInt(sOffer.split(",")[1]);
      return new Offer(agent.split("-")[1], distance, strength);
    } else {
      return null;
    }
  }

  public static Offer findBest(ArrayList<Offer> offers) {
    Offer best = new Offer();

    for(Offer o : offers) {
        int currentBest = best.getScore();
        
        if(best.agent == null || Math.min(currentBest, o.getScore()) != currentBest) {
          best = o;
        }
    }

    return best;
  }
}
