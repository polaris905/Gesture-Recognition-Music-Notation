package reaction;

import graphicslib.I;

public abstract class Mass extends Reaction.List implements I.Show {

  public Layer layer;

  public Mass(String layerName) {
    this.layer = Layer.byName.get(layerName);
    if (this.layer == null) {
      System.out.println("Layer not found: " + layerName);
    } else {
      this.layer.add(this);
    }
  }

  public void deleteMass() {
    this.layer.remove(this);
    this.clearAll(); // clears everything in the Reaction list of the Mass
  }

}
