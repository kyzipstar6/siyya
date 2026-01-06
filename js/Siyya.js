class AtomicDouble {
  constructor(val) { this.val = Number(val) || 0; }
  set(nwval) { this.val = Number(nwval) || 0; }
  get() { return this.val; }
}

class AtomicInteger {
  constructor(val) { this.val = Number(val) || 0; }
  set(nwval) { this.val = Number(nwval) || 0; }
  get() { return this.val; }
}

class ObjectProperty {
  constructor(value) { this.value = value; }
  set(v) { this.value = v; }
  get() { return this.value; }
}

/**
 * Canvas area chart to replace JavaFX StackedAreaChart.
 * Keeps it lightweight + no external libraries.
 */
class AreaChart {
  constructor(canvas, title) {
    this.canvas = canvas;
    this.ctx = canvas.getContext("2d");
    this.title = title;

    this.data = [];
    this.maxPoints = 240; // like "over time"
    this.gridLines = 6;

    // trend memory (like your 1s, 2s, 3.5s check)
    this.mem1 = null;
    this.mem2 = null;

    this.lineColor = getComputedStyle(document.documentElement).getPropertyValue("--line-up").trim() || "#22c55e";
    this.fillColor = getComputedStyle(document.documentElement).getPropertyValue("--fill-up").trim() || "rgba(34,197,94,0.15)";
  }

  push(value) {
    this.data.push(Number(value));
    if (this.data.length > this.maxPoints) this.data.shift();
  }

  updateTrendMemory(currentValue) {
    // approximate your: 1000ms -> isfall, 2000ms -> isfall2, 3500ms -> compare
    if (this.mem1 === null) this.mem1 = currentValue;
    if (this.mem2 === null) this.mem2 = currentValue;

    // shift memory like a rolling window
    this.mem2 = this.mem1;
    this.mem1 = currentValue;

    const up = (this.mem1 < currentValue) && (this.mem2 < currentValue);
    const down = (this.mem1 > currentValue) && (this.mem2 > currentValue);

    const root = getComputedStyle(document.documentElement);
    if (up) {
      this.lineColor = (root.getPropertyValue("--line-up").trim() || "#22c55e");
      this.fillColor = (root.getPropertyValue("--fill-up").trim() || "rgba(34,197,94,0.15)");
    } else if (down) {
      this.lineColor = (root.getPropertyValue("--line-down").trim() || "#38bdf8");
      this.fillColor = (root.getPropertyValue("--fill-down").trim() || "rgba(56,189,248,0.15)");
    } else {
      // steady: slightly dimmer
      this.lineColor = (root.getPropertyValue("--muted").trim() || "#94a3b8");
      this.fillColor = "rgba(148,163,184,0.10)";
    }
  }

  draw() {
    const ctx = this.ctx;
    const w = this.canvas.width;
    const h = this.canvas.height;

    ctx.clearRect(0, 0, w, h);

    // plot area padding
    const padL = 42, padR = 12, padT = 12, padB = 26;
    const pw = w - padL - padR;
    const ph = h - padT - padB;

    // background
    ctx.fillStyle = getComputedStyle(document.documentElement).getPropertyValue("--bg").trim() || "#0b0f14";
    ctx.fillRect(0, 0, w, h);

    // grid
    ctx.strokeStyle = getComputedStyle(document.documentElement).getPropertyValue("--grid").trim() || "rgba(148,163,184,0.18)";
    ctx.lineWidth = 1;

    for (let i = 0; i <= this.gridLines; i++) {
      const y = padT + (ph * i) / this.gridLines;
      ctx.beginPath();
      ctx.moveTo(padL, y);
      ctx.lineTo(padL + pw, y);
      ctx.stroke();
    }

    if (this.data.length < 2) return;

    const min = Math.min(...this.data);
    const max = Math.max(...this.data);
    const range = Math.max(1e-9, max - min);

    const xStep = pw / (this.data.length - 1);

    const toXY = (i, v) => {
      const x = padL + i * xStep;
      const y = padT + ph - ((v - min) / range) * ph;
      return [x, y];
    };

    // area fill
    ctx.fillStyle = this.fillColor;
    ctx.beginPath();
    {
      const [x0, y0] = toXY(0, this.data[0]);
      ctx.moveTo(x0, padT + ph);
      ctx.lineTo(x0, y0);

      for (let i = 1; i < this.data.length; i++) {
        const [x, y] = toXY(i, this.data[i]);
        ctx.lineTo(x, y);
      }

      const [xLast] = toXY(this.data.length - 1, this.data[this.data.length - 1]);
      ctx.lineTo(xLast, padT + ph);
      ctx.closePath();
      ctx.fill();
    }

    // line
    ctx.strokeStyle = this.lineColor;
    ctx.lineWidth = 2;
    ctx.beginPath();
    for (let i = 0; i < this.data.length; i++) {
      const [x, y] = toXY(i, this.data[i]);
      if (i === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.stroke();

    // y labels (min/max)
    ctx.fillStyle = getComputedStyle(document.documentElement).getPropertyValue("--muted").trim() || "#94a3b8";
    ctx.font = "12px Segoe UI, Inter, Arial";
    ctx.fillText(max.toFixed(5), 6, padT + 12);
    ctx.fillText(min.toFixed(5), 6, padT + ph);
    
  }
  
}
class Clock {
  constructor() {
    this.seconds = 0;
    this._timer = null;
  }
  model(cl) {
    if (this._timer) return; // already running
  }
  getTimeString() {
    const hrs = Math.floor(this.seconds / 3600);
    const mins = Math.floor((this.seconds % 3600) / 60);
    const secs = this.seconds % 60;
    return `${String(hrs).padStart(2, '0')}:${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  }
}

class SiyyaApp {
  constructor() {
    // model setup (same defaults as Java)
    
    this.coin1 = new AtomicDouble(139.5);
    this.coin2 = new AtomicDouble(1786.8);

    this.sim = new Simulator();
    this.cl = new AtomicInteger(1);
    //this.ls = new LoadSave();

    this.vol = new AtomicInteger(100);
    this.evolution = new ObjectProperty(Simulator.EvolutionType.ST);

    // UI
    this.timeLbl = document.getElementById("hour");
    this.coin1Lbl = document.getElementById("coin1Lbl");
    this.coin2Lbl = document.getElementById("coin2Lbl");


    this.volInput = document.getElementById("volInput");
    this.setVolBtn = document.getElementById("setVolBtn");
    this.startBtn = document.getElementById("startBtn");
   

    this.setNamesBtn = document.getElementById("setNamesBtn");

    this.evolutionButtonsHost = document.getElementById("evolutionButtons");

    // charts
    this.ordaChart = new AreaChart(document.getElementById("ordaChart"), "Ordacoin");
    this.virtuaChart = new AreaChart(document.getElementById("virtuaChart"), "Virtuacoin");

    this.coin1name = "KLM";
    this.coin2name = "Martian Cupper";

    const coin1NameInput = document.getElementById("coin1NameInput");
    const coin1PriceInput = document.getElementById("coin1PriceInput");
    const coin2NameInput = document.getElementById("coin2NameInput");
    const coin2PriceInput = document.getElementById("coin2PriceInput");
     const loadBtn = document.getElementById("loadBtn");
    const saveBtn = document.getElementById("saveBtn");
    const resetBtn = document.getElementById("resetBtn");

    // initial point like series.getData().add(0, val)
    this.ordaChart.push(this.coin1.get());
    this.virtuaChart.push(this.coin2.get());
    this.ordaChart.draw();
    this.virtuaChart.draw();

    this._evolveTimer = null;
    this._guiTimer = null;
    this._chartTimer = null;

    this.buildEvolutionButtons();
    this.wireEvents();
    this.startGuiUpdateLoops(); // charts + labels update like your Timelines
  }
  

  buildEvolutionButtons() {
    const labels = [
      "Fall Mode 3 (Strong sell trend)",
      "Fall Mode 2",
      "Fall Mode 1",
      "Steady",
      "Rise Mode 3",
      "Rise Mode 2",
      "Rise Mode 1 (Light buy trend)",
    ];

    const evols = [
      Simulator.EvolutionType.F3,
      Simulator.EvolutionType.F2,
      Simulator.EvolutionType.F1,
      Simulator.EvolutionType.ST,
      Simulator.EvolutionType.R3,
      Simulator.EvolutionType.R2,
      Simulator.EvolutionType.R1,
    ];

    this.evolutionButtonsHost.innerHTML = "";
    labels.forEach((txt, i) => {
      const btn = document.createElement("button");
      btn.className = "button";
      btn.textContent = txt;

      // same semantic coloring you did
      if (i <= 2) btn.classList.add("danger-button");
      else if (i === 3) {/* steady default */}
      else btn.classList.add("good-button");

      btn.addEventListener("click", () => this.evolution.set(evols[i]));
      this.evolutionButtonsHost.appendChild(btn);
    });
  }

  wireEvents() {
   
    const ls = new LoadSave();
    this.setVolBtn.addEventListener("click", () => {
      const n = parseInt(this.volInput.value, 10);
      if (Number.isFinite(n)) this.vol.set(n);
    });
    this.setNamesBtn.addEventListener("click", () => {
      console.log("Applying new coin names and prices");
      this.coin1name = coin1NameInput.value.trim();
      this.coin2name = coin2NameInput.value.trim();
      this.coin1.set(parseFloat(coin1PriceInput.value) || 1.003);
      this.coin2.set(parseFloat(coin2PriceInput.value) || 1.003);
      this.coin1Lbl.textContent = `${this.coin1name}: ${this.coin1.get().toFixed(5)} USD`;
      this.coin2Lbl.textContent = `${this.coin2name}: ${this.coin2.get().toFixed(5)} USD`;
    });

    this.startBtn.addEventListener("click", () => this.startSimulation());
    saveBtn.addEventListener("click", () => ls.wsimSaveCurrentUserSnapshot());
    loadBtn.addEventListener("click", () => ls.wsimLoadLastSnapshot());
    resetBtn.addEventListener("click", () => this.reset());
  }

  startSimulation() {
    // matches your: Timeline every 500ms evolve both + clock.model(cl)
    if (this._evolveTimer) return;

    this._evolveTimer = setInterval(() => {
      this.sim.evolveSymbols(this.coin1, this.evolution, this.vol);
      this.sim.evolveSymbols(this.coin2, this.evolution, this.vol);
    }, 500);

  }
  reset(){
    
    const coin1n = document.getElementById("coin1n");
    const coin2n = document.getElementById("coin2n");
    this.coin1.set(139.5);
    this.coin2.set(1786.8);
    this.ordaChart = new AreaChart(document.getElementById("ordaChart"), "Ordacoin");
    this.coin1name = "Coin 1";
    this.coin2name = "Coin 2";

    coin1n.textContent = this.coin1name;
    coin2n.textContent = this.coin2name;

    this.coin1Lbl.textContent = `${this.coin1.get()} USD`;
    this.coin2Lbl.textContent = `${this.coin2.get()} USD`;

  }
  
  startGuiUpdateLoops() {
    // labels update like your guiUpdate Timeline(1s)
    this._guiTimer = setInterval(() => {
     
      this.coin1Lbl.textContent = (this.coin1.get() < 10) ? `${this.coin1.get().toFixed(5)} USD` : (this.coin1.get()>10 &&
    this.coin1.get()<100)? `${this.coin1.get().toFixed(4)} USD`: `${this.coin1.get().toFixed(3)} USD`
      ;
      this.coin2Lbl.textContent = (this.coin2.get() < 10) ? `${this.coin2.get().toFixed(5)} USD` : (this.coin2.get()>10 &&
    this.coin2.get()<100)? `${this.coin2.get().toFixed(4)} USD`: `${this.coin2.get().toFixed(3)} USD`
      ;
    }, 1000);

    // charts update like chartUpdater Timeline(1s)
    this._chartTimer = setInterval(() => {
      const o = this.coin1.get();
      const v = this.coin2.get();

      this.ordaChart.push(o);
      this.virtuaChart.push(v);

      // approximate your trend memory behavior (up/down/else)
      this.ordaChart.updateTrendMemory(o);
      this.virtuaChart.updateTrendMemory(v);

      this.ordaChart.draw();
      this.virtuaChart.draw();
    }, 1000);
  }
}
class LoadSave {
  constructor() {
    // placeholder for future load/save functionality
     const WSIM_USERS_KEY = "wsim_users_v1";
    const WSIM_CURRENT_USER_KEY = "wsim_current_user_v1";
    const WSIM_USER_SNAPSHOTS_KEY = "wsim_user_snapshots_v1";
    const coin1Span = document.getElementById("coin1Lbl");
  const coin1Name = document.getElementById("coin1n");
  const coin2Span = document.getElementById("coin2Lbl");
  const coin2Name = document.getElementById("coin2n");

  // Time display
  const dayInput = document.getElementById("day");
  const monthInput = document.getElementById("month");
  const hourInput = document.getElementById("hour");
  const minuteInput = document.getElementById("minute");

  }
 

  wsimLoadUserSnapshots() {
  const raw = localStorage.getItem(WSIM_USER_SNAPSHOTS_KEY);
  return raw ? JSON.parse(raw) : {};
}

wsimSaveUserSnapshots(data) {
  localStorage.setItem(WSIM_USER_SNAPSHOTS_KEY, JSON.stringify(data));
}
wsimBuildCurrentSnapshot() {
  // Main "displayed" weather variables
  

  return {
    savedAt: new Date().toISOString(),

    display: {
      coin1Span: coin1Span ? coin1Span.textContent : null,
      coin2Span: coin2Span ? coin2Span.textContent : null,
      coin1Name: coin1Name ? coin1Name.textContent : null,
      coin2Name: coin2Name ? coin2Name.textContent : null
    },

    time: {
      hourText: hourInput ? hourInput.value : null,
      minuteText: minuteInput ? minuteInput.value : null,
      dayText: dayInput ? dayInput.value : null,
      monthText: monthInput ? monthInput.value : null
    }
  };
}
wsimGetCurrentUser() {
  const raw = localStorage.getItem(WSIM_CURRENT_USER_KEY);
  return raw ? JSON.parse(raw) : null;
}
// Save one snapshot for the current user
wsimSaveCurrentUserSnapshot() {
 // const current = wsimGetCurrentUser();
  //if (!current || !current.username) {
    //return { ok: false, message: "You need to be logged in to save." };
//  }

  const username = current.username;
  const allSnapshots = wsimLoadUserSnapshots();
  if (!allSnapshots[username]) {
    allSnapshots[username] = [];
  }

  const snapshot = wsimBuildCurrentSnapshot();
  allSnapshots[username].push(snapshot);

  wsimSaveUserSnapshots(allSnapshots);
  return { ok: true, message: "Weather + chart data saved for user." };
}

// Optional helper: get all snapshots for current user
wsimGetCurrentUserSnapshots() {
  const current = wsimGetCurrentUser();
  if (!current || !current.username) return [];
  const allSnapshots = wsimLoadUserSnapshots();
  return allSnapshots[current.username] || [];
}
wsimLoadLastSnapshot() {
  const snaps = wsimGetCurrentUserSnapshots();
  const siyya = new SiyyaApp();

  if (!snaps.length) {
    return { ok: false, message: "No saved session found." };
  }

  const snap = snaps[snaps.length - 1];

  // ---- restore inputs (source of truth) ----
  const setVal = (id, v) => {
    const el = document.getElementById(id);
    if (el && v !== null && v !== undefined) el.value = v;
  };

  // ---- sync simulator variables ----
  this.siyya.coin1.set(parseFloat(snap.display.coin1Span.split(" ").replace(/[^0-9.-]/g, '')));
  this.siyya.coin2.set(parseFloat(snap.display.coin2Span.split(" ").replace(/[^0-9.-]/g, '')));
  this.siyya.coin1n.textContent = snap.display.coin1Name;
  this.siyya.coin2n.textContent = snap.display.coin2Name;

  

  return { ok: true, message: "Last session restored." };
}
}
// boot
window.addEventListener("DOMContentLoaded", () => {
  new SiyyaApp();
});
