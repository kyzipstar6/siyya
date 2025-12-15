class Simulator {
  static EvolutionType = Object.freeze({
    F3: "F3",
    F2: "F2",
    F1: "F1",
    ST: "ST",
    R3: "R3",
    R2: "R2",
    R1: "R1",
  });

  evolveSymbols(atomicDouble, evolutionRef, volAtomicInt) {
    // Replace this with your real Simulator.java logic later.
    // This is a reasonable placeholder that respects "trend" + "volatility".
    const vol = Math.max(1, Number(volAtomicInt.get()) || 1);
    const mode = evolutionRef.get();

    const strength = (() => {
      switch (mode) {
        case Simulator.EvolutionType.F3: return -3;
        case Simulator.EvolutionType.F2: return -2;
        case Simulator.EvolutionType.F1: return -1;
        case Simulator.EvolutionType.ST: return 0;
        case Simulator.EvolutionType.R1: return 1;
        case Simulator.EvolutionType.R2: return 2;
        case Simulator.EvolutionType.R3: return 3;
        default: return 0;
      }
    })();

    const noise = (Math.random() - 0.5) * (vol / 10000);
    const drift = strength * (vol / 200000); // gentle drift
    const next = Math.max(0, atomicDouble.get() + noise + drift);

    atomicDouble.set(next);
  }
}
