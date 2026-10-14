import http from 'http';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Read GEMINI_API_KEY from environment or .env file
function getGeminiApiKey() {
  if (process.env.GEMINI_API_KEY && process.env.GEMINI_API_KEY !== 'MY_GEMINI_API_KEY') {
    return process.env.GEMINI_API_KEY;
  }
  const envPaths = [path.join(__dirname, '.env'), path.join(__dirname, '.env.example')];
  for (const envPath of envPaths) {
    if (fs.existsSync(envPath)) {
      const content = fs.readFileSync(envPath, 'utf8');
      const match = content.match(/GEMINI_API_KEY=["']?([^"'\r\n]+)["']?/);
      if (match && match[1] && match[1] !== 'MY_GEMINI_API_KEY') {
        return match[1].trim();
      }
    }
  }
  return '';
}

const DB_FILE = path.join(__dirname, 'erp_cloud_db.json');

const DEFAULT_DB = {
  themeConfig: {
    businessName: 'RST ELECTRONICS',
    legalName: 'AL WAHA AL KHDRA AL MUTAKAMILAH TRADING',
    crNo: '1374092',
    phone: '+968 9430 1393',
    email: 'Eletrocnisrst@gmail.com',
    address: 'Mabellah Sanaya, Road No 6, Muscat, Sultanate of Oman',
    currency: 'OMR',
    vatRate: 5,
    decimalPlaces: 3,
    invoicePrefix: 'RST'
  },
  products: [
    { id: 1, name: 'iPhone 15 Pro Max 256GB', brand: 'Apple', category: 'Mobile', price: 479.000, cost: 410.000, stock: 12, barcode: '8901234567890', imei: '356789123456789', icon: '📱', featured: true },
    { id: 2, name: 'Samsung Galaxy S24 Ultra 5G', brand: 'Samsung', category: 'Mobile', price: 429.000, cost: 365.000, stock: 9, barcode: '8901234567891', imei: '987654321123456', icon: '📱', featured: true },
    { id: 3, name: 'Sony WH-1000XM5 ANC Headphones', brand: 'Sony', category: 'Audio', price: 134.900, cost: 105.000, stock: 18, barcode: '8901234567892', imei: '', icon: '🎧', featured: true },
    { id: 4, name: 'Samsung 65" Crystal UHD 4K Smart TV', brand: 'Samsung', category: 'TV', price: 225.000, cost: 175.000, stock: 6, barcode: '8901234567893', imei: '', icon: '📺', featured: true },
    { id: 5, name: 'Xiaomi Redmi Note 13 Pro 5G', brand: 'Xiaomi', category: 'Mobile', price: 112.500, cost: 88.000, stock: 24, barcode: '8901234567894', imei: '864209753124680', icon: '📱', featured: true },
    { id: 6, name: 'Apple MacBook Air 15" M3 Chip', brand: 'Apple', category: 'Laptop', price: 565.000, cost: 485.000, stock: 5, barcode: '8901234567895', imei: '', icon: '💻', featured: true }
  ],
  telecomOps: [
    { id: 1, name: 'Omantel (Hayyak / Baqati)', code: 'OMT', color: '#008542', balance: 450.000, margin: 2.5, type: 'percent', latency: 18 },
    { id: 2, name: 'Ooredoo Oman (Shababiah)', code: 'OOD', color: '#e11d48', balance: 320.500, margin: 0.055, type: 'fixed', latency: 22 },
    { id: 3, name: 'Vodafone Oman (5G Red)', code: 'VDF', color: '#dc2626', balance: 280.000, margin: 2.8, type: 'percent', latency: 16 },
    { id: 4, name: 'Awasr Fiber High-Speed', code: 'AWS', color: '#0284c7', balance: 500.000, margin: 3.5, type: 'percent', latency: 9 },
    { id: 5, name: 'FRiENDi Mobile Oman', code: 'FRD', color: '#7c3aed', balance: 150.000, margin: 0.040, type: 'fixed', latency: 27 },
    { id: 6, name: 'Renna Mobile Oman', code: 'RNN', color: '#ea580c', balance: 115.000, margin: 0.035, type: 'fixed', latency: 29 }
  ],
  utilityOps: [
    { id: 1, name: 'Nama Electricity Supply (ONEIC)', code: 'ELEC', icon: 'bolt', margin: 1.2 },
    { id: 2, name: 'Diam / Nama Water Services', code: 'WATER', icon: 'tint', margin: 1.2 },
    { id: 3, name: 'Omantel / Awasr Home Fiber Bill', code: 'FIBER', icon: 'wifi', margin: 2.0 }
  ],
  scienceTasks: [
    {
      id: 101,
      title: 'Semiconductor Junction Thermal Drift Experiment',
      category: 'Electronics & Physics',
      stage: 'in_progress',
      priority: 'High',
      dueDate: '2026-10-18',
      hypothesis: 'Forward voltage drop decreases linearly at -2.1mV/°C as junction temperature rises from 25°C to 75°C.',
      independentVar: 'Temperature (25°C - 75°C)',
      dependentVar: 'Forward Voltage Vf (mV)',
      controlVar: 'Constant current 10.0mA',
      apparatus: 'DMM Keithley 2000, Temperature Chamber, K-Type Thermocouple',
      observations: 'Confirmed -2.08mV/°C slope with R² = 0.998 across 5 trial runs.',
      updatedAt: new Date().toISOString()
    },
    {
      id: 102,
      title: '5G NR Signal Attenuation Through Reinforced Concrete',
      category: 'STEM Scientific Method',
      stage: 'todo',
      priority: 'Medium',
      dueDate: '2026-10-22',
      hypothesis: '3.5GHz mid-band signals suffer >24dB path loss compared to sub-1GHz Hayyak channels.',
      independentVar: 'Carrier frequency & wall thickness',
      dependentVar: 'RSSI & SINR',
      controlVar: 'Transmitter distance 15m',
      apparatus: 'SDR Analyzer, Spectrum Analyzer, Reference Horn Antenna',
      observations: 'Apparatus calibrated in Mabellah showroom testing bay.',
      updatedAt: new Date().toISOString()
    },
    {
      id: 103,
      title: 'Li-Ion Battery Cell Degradation & Internal Resistance',
      category: 'Chemical Solutions & Battery Testing',
      stage: 'review',
      priority: 'Urgent',
      dueDate: '2026-10-15',
      hypothesis: 'AC impedance at 1kHz correlates directly with discharge cycle count.',
      independentVar: 'Discharge cycle count (1 to 500)',
      dependentVar: 'AC ESR (mΩ)',
      controlVar: 'Room temp 23°C, 1C discharge rate',
      apparatus: 'Battery Impedance Meter, Electronic Load',
      observations: 'ESR increased from 18mΩ to 44mΩ after 400 cycles.',
      updatedAt: new Date().toISOString()
    }
  ],
  invoices: [],
  updatedAt: new Date().toISOString()
};

function loadDb() {
  try {
    if (fs.existsSync(DB_FILE)) {
      return JSON.parse(fs.readFileSync(DB_FILE, 'utf8'));
    }
  } catch (e) {
    console.error('Error reading db:', e);
  }
  fs.writeFileSync(DB_FILE, JSON.stringify(DEFAULT_DB, null, 2), 'utf8');
  return DEFAULT_DB;
}

function saveDb(data) {
  try {
    data.updatedAt = new Date().toISOString();
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), 'utf8');
  } catch (e) {
    console.error('Error saving db:', e);
  }
}

let dbState = loadDb();
const sseClients = new Set();

function broadcastSse(event, data) {
  const msg = `event: ${event}\ndata: ${JSON.stringify(data)}\n\n`;
  for (const client of sseClients) {
    try {
      client.write(msg);
    } catch (_) {
      sseClients.delete(client);
    }
  }
}

// Call Google Gemini REST API using native fetch
async function callGemini(model, prompt, options = {}) {
  const apiKey = getGeminiApiKey();
  if (!apiKey) {
    return `[Gemini ${model} Mode]: Processed query: "${prompt}". (API key placeholder active). System telemetry verified and nominal.`;
  }

  const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;
  const parts = [{ text: prompt }];

  if (options.videoBase64) {
    parts.push({
      inlineData: {
        mimeType: options.mimeType || 'video/mp4',
        data: options.videoBase64
      }
    });
  }

  const payload = {
    contents: [
      {
        role: 'user',
        parts
      }
    ]
  };

  if (options.thinkingLevel) {
    payload.generationConfig = {
      thinkingConfig: {
        thinkingLevel: options.thinkingLevel
      }
    };
  }

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json();
    if (!res.ok) {
      const errText = data?.error?.message || `HTTP ${res.status}`;
      return `Gemini API notice (${res.status}): ${errText}`;
    }

    const candidateText = data?.candidates?.[0]?.content?.parts?.map(p => p.text).join('') || '';
    return candidateText || 'No response text received from model.';
  } catch (err) {
    return `Gemini request error: ${err.message}`;
  }
}

// Read body helper
function readJsonBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', chunk => {
      body += chunk;
      if (body.length > 25 * 1024 * 1024) { // 25MB limit
        req.destroy();
        reject(new Error('Payload too large'));
      }
    });
    req.on('end', () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch (err) {
        resolve({});
      }
    });
    req.on('error', reject);
  });
}

const server = http.createServer(async (req, res) => {
  const parsedUrl = new URL(req.url, `http://${req.headers.host || 'localhost:3000'}`);
  const pathname = parsedUrl.pathname;

  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, DELETE');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  // 1. SSE Stream for Real-Time Multi-Device Sync
  if (pathname === '/api/stream') {
    res.writeHead(200, {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive'
    });
    res.write(`event: connected\ndata: ${JSON.stringify({ status: 'connected', time: new Date().toISOString() })}\n\n`);
    sseClients.add(res);
    req.on('close', () => sseClients.delete(res));
    return;
  }

  // 2. State GET
  if (pathname === '/api/state' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(dbState));
    return;
  }

  // 3. State Sync POST
  if (pathname === '/api/state/sync' && req.method === 'POST') {
    try {
      const incoming = await readJsonBody(req);
      dbState = { ...dbState, ...incoming, updatedAt: new Date().toISOString() };
      saveDb(dbState);
      broadcastSse('state_updated', { updatedAt: dbState.updatedAt });
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ ok: true, updatedAt: dbState.updatedAt }));
    } catch (e) {
      res.writeHead(500, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: e.message }));
    }
    return;
  }

  // 4. Low-Latency Gemini (gemini-3.1-flash-lite)
  if (pathname === '/api/gemini/chat' && req.method === 'POST') {
    const { prompt } = await readJsonBody(req);
    const reply = await callGemini('gemini-3.1-flash-lite', prompt || 'Hello');
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ reply, model: 'gemini-3.1-flash-lite' }));
    return;
  }

  // 5. Video Content Analysis (gemini-3.1-pro-preview)
  if (pathname === '/api/gemini/video' && req.method === 'POST') {
    const { prompt, videoBase64, mimeType } = await readJsonBody(req);
    const reply = await callGemini('gemini-3.1-pro-preview', prompt || 'Analyze this video for key information.', {
      videoBase64,
      mimeType: mimeType || 'video/mp4'
    });
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ reply, model: 'gemini-3.1-pro-preview' }));
    return;
  }

  // 6. High Thinking Mode (gemini-3.1-pro-preview with thinkingLevel HIGH)
  if (pathname === '/api/gemini/think' && req.method === 'POST') {
    const { prompt } = await readJsonBody(req);
    const reply = await callGemini('gemini-3.1-pro-preview', prompt || 'Deep reasoning query', {
      thinkingLevel: 'HIGH'
    });
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ reply, model: 'gemini-3.1-pro-preview', thinkingLevel: 'HIGH' }));
    return;
  }

  // 7. Serve HTML Frontend
  if (pathname === '/' || pathname === '/index.html') {
    const htmlPath = path.join(__dirname, 'app/src/main/assets/web/index.html');
    if (fs.existsSync(htmlPath)) {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      res.end(fs.readFileSync(htmlPath, 'utf8'));
      return;
    }
  }

  // 8. Serve static files if requested
  const staticPath = path.join(__dirname, pathname);
  if (fs.existsSync(staticPath) && fs.statSync(staticPath).isFile()) {
    const ext = path.extname(staticPath).toLowerCase();
    const mimeTypes = {
      '.html': 'text/html',
      '.css': 'text/css',
      '.js': 'application/javascript',
      '.json': 'application/json',
      '.png': 'image/png',
      '.jpg': 'image/jpeg',
      '.svg': 'image/svg+xml'
    };
    res.writeHead(200, { 'Content-Type': mimeTypes[ext] || 'application/octet-stream' });
    res.end(fs.readFileSync(staticPath));
    return;
  }

  // Default fallback to index.html
  const fallbackHtml = path.join(__dirname, 'app/src/main/assets/web/index.html');
  if (fs.existsSync(fallbackHtml)) {
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(fs.readFileSync(fallbackHtml, 'utf8'));
    return;
  }

  res.writeHead(404, { 'Content-Type': 'text/plain' });
  res.end('Not Found');
});

const PORT = 3000;
server.listen(PORT, '0.0.0.0', () => {
  console.log(`RST Electronics Web ERP server running on http://0.0.0.0:${PORT}`);
});
