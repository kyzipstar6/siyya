class Simulator {

  // === Java enum EvolutionType ===
  static EvolutionType = Object.freeze({
    ST: "ST",
    R1: "R1",
    R2: "R2",
    R3: "R3",
    F1: "F1",
    F2: "F2",
    F3: "F3",
  });

  constructor() {
    // mirrors:
    // ObjectProperty<EvolutionType> evolution = new SimpleObjectProperty<>(EvolutionType.ST);
    this.evolution = new ObjectProperty(Simulator.EvolutionType.ST);
  }

  /**
   * Port of:
   * public void evolveSymbols(AtomicDouble symVal, ObjectProperty<EvolutionType> evol, AtomicInteger vol)
   */
  evolveSymbols(symVal, evol, vol) {
    const acval = symVal.get();
    const v = vol.get();

    let nval;

    switch (evol.get()) {

      case Simulator.EvolutionType.ST:
        // acval * (1 + (-0.5 + (1 * Math.random())) / vol)
        nval = acval * (1 + (-0.5 + Math.random()) / v);
        break;

      case Simulator.EvolutionType.R1:
        nval = acval * (1 + (-0.42 + Math.random()) / v);
        break;

      case Simulator.EvolutionType.R2:
        nval = acval * (1 + (-0.33 + Math.random()) / v);
        break;

      case Simulator.EvolutionType.R3:
        nval = acval * (1 + (-0.25 + Math.random()) / v);
        break;

      case Simulator.EvolutionType.F1:
        nval = acval * (1 + (-0.58 + Math.random()) / v);
        break;

      case Simulator.EvolutionType.F2:
        nval = acval * (1 + (-0.66 + Math.random()) / v);
        break;

      case Simulator.EvolutionType.F3:
        nval = acval * (1 + (-0.75 + Math.random()) / v);
        break;

      default:
        return;
    }

    symVal.set(nval);
  }
}
