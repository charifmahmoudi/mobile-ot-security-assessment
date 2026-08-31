#!/usr/bin/env node
'use strict';

const pptxgen = require('pptxgenjs');
const fs = require('fs');
const path = require('path');

const pptx = new pptxgen();
pptx.layout = 'LAYOUT_WIDE';
pptx.author = 'Charif Mahmoudi';
pptx.company = 'Atlas OT Scout';
pptx.subject = 'P0-WATER buyer pitch and actual user-story demonstration';
pptx.title = 'Atlas OT Scout - Water OT Evidence You Can Defend';
pptx.lang = 'en-US';
pptx.theme = { headFontFace: 'Aptos Display', bodyFontFace: 'Aptos', lang: 'en-US' };

const ROOT = path.resolve(__dirname, '..');
const IMG = path.join(ROOT, 'docs', 'user-guide', 'screenshots');
const OUT = path.join(ROOT, 'docs', 'pitch', 'Atlas-OT-Scout-Pitch-and-Demo.pptx');
const VIDEO = 'https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/demo/atlas-ot-scout-emulator-demo.mp4';
const C = { navy:'0B2239', navy2:'15344F', blue:'1479D3', aqua:'21B6C7', teal:'0B8D8A', green:'147D64', amber:'B36A00', red:'B93B3B', ink:'102A43', muted:'62748A', white:'FFFFFF', light:'F6F9FC', line:'D9E3EC', paleBlue:'EAF3FC', paleAqua:'E9F9FB', paleAmber:'FFF5DD', paleRed:'FCEAEA', mint:'E6F7F4' };

pptx.defineSlideMaster({
  title: 'ATLAS', background: { color: C.light },
  objects: [
    { rect: { x:0, y:0, w:13.333, h:0.055, fill:{color:C.aqua}, line:{color:C.aqua} } },
    { text: { text:'ATLAS OT SCOUT  /  P0-WATER', options:{x:0.55,y:0.18,w:3.5,h:0.22,fontSize:7.5,bold:true,color:C.muted,charSpacing:1.3,margin:0} } },
  ],
  slideNumber: { x:12.35,y:7.05,w:0.45,h:0.18,fontSize:8,color:'8EA0B5',align:'right',margin:0 }
});

function img(name){ const p=path.join(IMG,name); if(!fs.existsSync(p)) throw new Error(`Missing ${p}`); return p; }
function card(s,x,y,w,h,fill=C.white,line=C.line){ s.addShape(pptx.ShapeType.roundRect,{x,y,w,h,rectRadius:0.07,fill:{color:fill},line:{color:line,width:1},shadow:{type:'outer',color:'8093A6',opacity:0.10,blur:1,angle:45,distance:1}}); }
function title(s,t,sub){ s.addText(t,{x:0.55,y:0.62,w:12.15,h:0.55,fontFace:'Aptos Display',fontSize:25,bold:true,color:C.navy,margin:0,fit:'shrink'}); if(sub)s.addText(sub,{x:0.57,y:1.20,w:11.9,h:0.42,fontSize:11.5,color:C.muted,margin:0,fit:'shrink'}); }
function footer(s,t='Emulated Android evidence - not physical field qualification'){ s.addText(t,{x:0.58,y:7.04,w:9.2,h:0.18,fontSize:7.5,color:'8EA0B5',margin:0}); }
function pill(s,text,x,y,w,fill,color){ s.addShape(pptx.ShapeType.roundRect,{x,y,w,h:0.34,rectRadius:0.08,fill:{color:fill},line:{color:fill}}); s.addText(text,{x,y:y+0.01,w,h:0.24,fontSize:8.2,bold:true,color,align:'center',margin:0,fit:'shrink'}); }
function phone(s,name,x,y,h,label,line=C.line){ const w=h*(1080/2400); card(s,x-0.055,y-0.055,w+0.11,h+0.11,C.white,line); s.addImage({path:img(name),x,y,w,h}); if(label)s.addText(label,{x:x-0.15,y:y+h+0.12,w:w+0.30,h:0.26,fontSize:8,bold:true,color:C.muted,align:'center',margin:0,fit:'shrink'}); return w; }
function bullet(s,text,x,y,w,color=C.aqua){ s.addShape(pptx.ShapeType.ellipse,{x,y:y+0.09,w:0.09,h:0.09,fill:{color},line:{color}}); s.addText(text,{x:x+0.18,y,w:w-0.18,h:0.43,fontSize:11.2,color:C.ink,margin:0,fit:'shrink'}); }
function metric(s,value,label,x,y,w,accent=C.blue){ card(s,x,y,w,0.92); s.addText(value,{x:x+0.1,y:y+0.10,w:w-0.2,h:0.34,fontSize:19,bold:true,color:accent,align:'center',margin:0,fit:'shrink'}); s.addText(label,{x:x+0.1,y:y+0.54,w:w-0.2,h:0.22,fontSize:8.4,bold:true,color:C.muted,align:'center',margin:0,fit:'shrink'}); }
function play(s,x,y,w=3.4,label='WATCH THE ACTUAL USER STORY'){ s.addShape(pptx.ShapeType.roundRect,{x,y,w,h:0.58,rectRadius:0.08,fill:{color:C.blue},line:{color:C.blue},hyperlink:{url:VIDEO}}); s.addText('▶',{x:x+0.17,y:y+0.13,w:0.32,h:0.23,fontSize:13,bold:true,color:C.white,align:'center',margin:0,hyperlink:{url:VIDEO}}); s.addText(label,{x:x+0.55,y:y+0.17,w:w-0.7,h:0.22,fontSize:8.4,bold:true,color:C.white,charSpacing:0.7,margin:0,fit:'shrink',hyperlink:{url:VIDEO}}); }
function notes(s,lines){ s.addNotes(lines.join('\n')); }

{
  const s=pptx.addSlide(); s.background={color:C.navy};
  s.addShape(pptx.ShapeType.rect,{x:0,y:0,w:13.333,h:0.055,fill:{color:C.aqua},line:{color:C.aqua}});
  s.addText('P0-WATER / BUYER WALKTHROUGH',{x:0.68,y:0.66,w:4,h:0.22,fontSize:8.5,bold:true,color:C.aqua,charSpacing:1.3,margin:0});
  s.addText('Know what changed\nbefore you sign off.',{x:0.68,y:1.16,w:7.1,h:1.55,fontFace:'Aptos Display',fontSize:35,bold:true,color:C.white,margin:0,fit:'shrink'});
  s.addText('Turn a bounded water-OT evidence window into a reviewable inventory, explicit findings and a controlled handoff - without pretending scanner output is truth.',{x:0.70,y:3.04,w:6.35,h:0.92,fontSize:15,color:'D5E2EE',margin:0,fit:'shrink'});
  play(s,0.70,4.36,3.55);
  s.addText('Continuous Android 15 screen recording. Same application, same user story.',{x:0.70,y:5.08,w:5.55,h:0.38,fontSize:9.5,color:'9FB3C7',margin:0});
  phone(s,'03-site-dashboard-api35.png',9.52,0.84,5.82,'Actual sample water-treatment workspace',C.aqua);
  s.addText('ATLAS OT SCOUT',{x:0.70,y:6.86,w:2.4,h:0.2,fontSize:8,bold:true,color:'6FA9B7',charSpacing:1.2,margin:0});
  notes(s,['Source: docs/user-guide/screenshots/03-site-dashboard-api35.png','Source: docs/demo/atlas-ot-scout-emulator-demo.mp4','Open with the field decision, not the scanning technology.']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'The costly failure is signing off with uncertainty.','A useful field instrument closes decision gaps instead of maximizing device counts.');
  s.addText('At the end of a site visit, an authorized assessor still has to answer:',{x:0.6,y:1.83,w:8,h:0.38,fontSize:14,color:C.ink,margin:0});
  const qs=[['01','What changed?','Which identities differ from baseline or remain unknown?'],['02','What can I defend?','Which claims retain evidence, provenance and confidence?'],['03','What is safe to claim?','Which conclusions remain blocked by visibility or approval gaps?']];
  qs.forEach((q,i)=>{const x=0.6+i*4.14; card(s,x,2.48,3.75,2.5,i===1?C.paleBlue:C.white,i===1?'BBD8F3':C.line); pill(s,q[0],x+0.22,2.75,0.58,i===1?'DCEEFF':C.paleAqua,i===1?C.blue:C.teal); s.addText(q[1],{x:x+0.22,y:3.32,w:3.2,h:0.4,fontSize:19,bold:true,color:C.navy,margin:0}); s.addText(q[2],{x:x+0.22,y:3.90,w:3.2,h:0.75,fontSize:11.5,color:C.muted,margin:0,fit:'shrink'});});
  s.addShape(pptx.ShapeType.roundRect,{x:0.62,y:5.45,w:12.1,h:0.92,rectRadius:0.06,fill:{color:C.navy},line:{color:C.navy}});
  s.addText('A useful answer can be “not enough evidence yet” - as long as the missing evidence is explicit.',{x:1.0,y:5.72,w:11.3,h:0.3,fontSize:14.5,bold:true,color:C.white,align:'center',margin:0,fit:'shrink'}); footer(s);
  notes(s,['Source: README.md','Source: docs/poc/WATER-WASTEWATER-POC.md','Sell defensible decisions, not “scanning.”']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'One user story. Five decisions.','The deck and video now follow the assessor workflow in sequence.');
  const stages=[['1','Context','What exact site/process area is authorized?',C.blue],['2','Collect','What least-intrusive evidence closes the gap?',C.teal],['3','Review','Which observations are supported enough to accept?',C.aqua],['4','Reason','What condition can be defended?',C.amber],['5','Handoff','What is ready - and what remains blocked?',C.navy2]];
  stages.forEach((st,i)=>{const x=0.55+i*2.54; card(s,x,2.15,2.25,3.42,C.white,i===2?'A9E5EA':C.line); s.addShape(pptx.ShapeType.ellipse,{x:x+0.18,y:2.42,w:0.48,h:0.48,fill:{color:st[3]},line:{color:st[3]}}); s.addText(st[0],{x:x+0.18,y:2.54,w:0.48,h:0.18,fontSize:10,bold:true,color:C.white,align:'center',margin:0}); s.addText(st[1],{x:x+0.18,y:3.10,w:1.85,h:0.35,fontSize:17,bold:true,color:C.navy,margin:0}); s.addText(st[2],{x:x+0.18,y:3.67,w:1.83,h:1.05,fontSize:10.5,color:C.muted,margin:0,fit:'shrink'}); if(i<4)s.addText('→',{x:x+2.28,y:3.53,w:0.24,h:0.3,fontSize:17,bold:true,color:'9AACBC',align:'center',margin:0});});
  s.addText('The user story is the product: every screen exists to support one decision.',{x:2.0,y:6.02,w:9.3,h:0.36,fontSize:15,bold:true,color:C.navy,align:'center',margin:0}); footer(s);
  notes(s,['Source: MainActivity guided workflow','The video must never jump to a screen without showing the decision that leads to it.']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'Passive evidence is not inventory until an analyst accepts it.','This is the core trust behavior: observations enter a review queue before they change the model.');
  phone(s,'04-collection-methods-api35.png',0.72,1.77,4.83,'Choose least-intrusive method');
  phone(s,'06-live-span-result-api35.png',3.48,1.77,4.83,'Review and accept observations',C.aqua);
  card(s,6.42,1.88,6.18,4.78,C.white); pill(s,'USER STORY',6.75,2.18,1.08,C.paleAqua,C.teal);
  s.addText('“I need a current communication picture without touching the process.”',{x:6.75,y:2.76,w:5.2,h:0.74,fontSize:18,bold:true,color:C.navy,margin:0,fit:'shrink'});
  bullet(s,'Choose SPAN/TAP or an approved capture before active methods.',6.78,3.78,5.2,C.teal);
  bullet(s,'Hash and parse the bounded evidence window.',6.78,4.40,5.2,C.teal);
  bullet(s,'Accept only the observations the analyst is prepared to defend.',6.78,5.02,5.2,C.teal);
  s.addShape(pptx.ShapeType.roundRect,{x:6.75,y:5.82,w:5.18,h:0.48,rectRadius:0.05,fill:{color:C.mint},line:{color:'BDE5D9'}});
  s.addText('No acceptance → no inventory mutation.',{x:6.92,y:5.96,w:4.84,h:0.19,fontSize:10.6,bold:true,color:C.green,align:'center',margin:0}); footer(s);
  notes(s,['Source: docs/user-guide/screenshots/04-collection-methods-api35.png','Source: docs/user-guide/screenshots/06-live-span-result-api35.png','Source: MainActivity.renderPassiveResult']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'Active behavior fails closed before a packet is sent.','The demo deliberately asks for an out-of-scope target first.');
  phone(s,'05-active-authorization-api35.png',0.78,1.72,5.08,'Exact work order, target and CIDR');
  phone(s,'06-out-of-scope-blocked-api35.png',3.70,1.72,5.08,'Local scope rejection',C.red);
  card(s,6.72,1.93,5.88,4.64,C.paleRed,'F0B8B8'); pill(s,'FAIL-CLOSED',7.05,2.21,1.15,'F8D7D7',C.red);
  s.addText('192.0.2.5',{x:7.05,y:2.86,w:2.4,h:0.45,fontSize:24,bold:true,color:C.red,margin:0});
  s.addText('is rejected against',{x:7.05,y:3.38,w:2.5,h:0.28,fontSize:11,color:C.muted,margin:0});
  s.addText('10.0.2.0/24',{x:7.05,y:3.80,w:2.8,h:0.44,fontSize:22,bold:true,color:C.navy,margin:0});
  bullet(s,'Validation happens in the Case App before broker execution.',7.05,4.62,4.95,C.red);
  bullet(s,'The application does not broaden scope to make a test pass.',7.05,5.28,4.95,C.red); footer(s);
  notes(s,['Source: docs/user-guide/screenshots/05-active-authorization-api35.png','Source: docs/user-guide/screenshots/06-out-of-scope-blocked-api35.png','The out-of-scope request is intentionally shown in the live recording.']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'Then ask one exact identity question - and stop.','The controlled CI target answers a bounded Modbus basic-device-identification request.');
  metric(s,'10.0.2.2','EXACT TARGET',0.68,1.84,2.0,C.blue); metric(s,'FC 43 / 14','ONE OPERATION',2.90,1.84,2.0,C.teal); metric(s,'0','REGISTER READS / WRITES',5.12,1.84,2.0,C.green);
  card(s,0.68,3.10,6.34,2.82,C.navy); pill(s,'CONTROLLED ACTIVE PATH',1.02,3.40,1.86,'23465E',C.aqua);
  s.addText('Case App  →  signed one-use grant  →  Network Broker  →  TCP/502  →  PyModbus',{x:1.02,y:4.08,w:5.65,h:0.73,fontSize:15,bold:true,color:C.white,align:'center',margin:0,fit:'shrink'});
  s.addText('The result carries the evidence needed to decide whether the identity can be accepted into inventory.',{x:1.05,y:5.08,w:5.58,h:0.58,fontSize:10.8,color:'C9D8E5',align:'center',margin:0,fit:'shrink'});
  phone(s,'09-active-pymodbus-api35.png',8.32,1.70,5.23,'Actual PyModbus CI identity result',C.aqua); footer(s);
  notes(s,['Source: docs/user-guide/screenshots/09-active-pymodbus-api35.png','Source: Android safety CI / live-demo PyModbus testbed','Do not imply vulnerability scanning or register access.']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'From observations to a defensible handoff.','The product preserves uncertainty instead of hiding it.');
  phone(s,'07-asset-inventory-api35.png',0.72,1.78,4.82,'Evidence-backed inventory');
  phone(s,'10-guided-report-readiness-api35.png',3.45,1.78,4.82,'Explicit report blockers');
  card(s,6.38,1.90,6.25,4.72,C.white); s.addText('Customer-facing outcome',{x:6.72,y:2.20,w:3.8,h:0.4,fontSize:18.5,bold:true,color:C.navy,margin:0});
  const os=[['Inventory delta','Corroborated, unexpected, missing-from-evidence and unresolved identities.'],['Provenance','What was observed, where it came from and confidence.'],['Finding drafts','Condition, evidence, context and required validation.'],['Readiness blockers','Authorization and independent review remain visible.']];
  os.forEach((o,i)=>{const y=2.90+i*0.82; s.addShape(pptx.ShapeType.ellipse,{x:6.74,y:y+0.05,w:0.24,h:0.24,fill:{color:i<3?C.teal:C.amber},line:{color:i<3?C.teal:C.amber}}); s.addText(o[0],{x:7.12,y,w:1.62,h:0.28,fontSize:11,bold:true,color:C.navy,margin:0,fit:'shrink'}); s.addText(o[1],{x:8.80,y:y-0.03,w:3.35,h:0.46,fontSize:9.6,color:C.muted,margin:0,fit:'shrink'});});
  s.addShape(pptx.ShapeType.roundRect,{x:6.72,y:5.96,w:5.42,h:0.42,rectRadius:0.05,fill:{color:C.paleAmber},line:{color:'F0D59B'}}); s.addText('A blocked report is useful when the evidence is not ready.',{x:6.9,y:6.08,w:5.05,h:0.18,fontSize:9.4,bold:true,color:C.amber,align:'center',margin:0}); footer(s);
  notes(s,['Source: docs/user-guide/screenshots/07-asset-inventory-api35.png','Source: docs/user-guide/screenshots/10-guided-report-readiness-api35.png','Source: MainActivity.renderFindings / renderReportReadiness']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'Built for a Moroccan water-utility pilot - not generic industry.','The commercial scope now matches P0-WATER: drinking water and wastewater operations only.');
  card(s,0.60,1.88,7.25,4.75,C.white); s.addText('Water operating contexts',{x:0.92,y:2.18,w:3.4,h:0.38,fontSize:18,bold:true,color:C.navy,margin:0});
  const ctx=[['Treatment','process/control'],['Pumping','stations/boosters'],['Reservoirs','storage/transfer'],['Telemetry','remote monitoring'],['District metering','flow/pressure'],['Leak reduction','instrumentation'],['Wastewater','collection/treatment']];
  ctx.forEach((c,i)=>{const col=i%2,row=Math.floor(i/2),x=0.94+col*3.25,y=2.92+row*0.77; pill(s,c[0],x,y,1.28,i===6?C.paleAqua:C.paleBlue,i===6?C.teal:C.blue); s.addText(c[1],{x:x+1.40,y:y+0.03,w:1.55,h:0.26,fontSize:9.5,color:C.muted,margin:0,fit:'shrink'});});
  card(s,8.20,1.88,4.50,4.75,C.navy,C.navy); pill(s,'BEACHHEAD',8.57,2.18,1.08,'244B63',C.aqua);
  s.addText('ONEE - Water Branch',{x:8.57,y:2.92,w:3.3,h:0.36,fontSize:16,bold:true,color:C.aqua,margin:0}); s.addText('National water production, transmission and sanitation programs.',{x:8.57,y:3.42,w:3.35,h:0.55,fontSize:10.4,color:'D2DFEA',margin:0,fit:'shrink'});
  s.addText('Regional multiservice companies',{x:8.57,y:4.35,w:3.35,h:0.34,fontSize:14,bold:true,color:C.white,margin:0}); s.addText('Only their drinking-water and liquid-sanitation responsibilities are in scope.',{x:8.57,y:4.80,w:3.35,h:0.62,fontSize:10.4,color:'D2DFEA',margin:0,fit:'shrink'});
  s.addText('Entry offer: one bounded, passive-first evidence baseline.',{x:8.57,y:5.70,w:3.35,h:0.48,fontSize:10,bold:true,color:'AFC5D8',margin:0,fit:'shrink'}); footer(s,'Active GTM excludes electricity, automotive, aerospace, mining, ports, phosphate and other industries.');
  notes(s,['Source: docs/wiki/Morocco-Market.md','Source: docs/accounts/README.md','Do not broaden the commercial target beyond water/liquid sanitation.']);
}

{
  const s=pptx.addSlide('ATLAS'); title(s,'What is proven today - and what is not.','Credibility comes from stating the proof boundary before the buyer asks.');
  card(s,0.62,1.88,5.96,4.88,'F4FBF9','BDE5D9'); pill(s,'PROVEN IN CI / EMULATION',0.95,2.16,2.02,C.mint,C.green);
  ['Android API 29 + API 35 guided journeys','PCAP / PCAPNG passive analysis with provenance','Receive-only capture boundary exercised in CI emulation','One signed Modbus basic identity operation','Interoperability: PyModbus, modbus-tk and Conpot','Review-first inventory, findings and readiness UI'].forEach((t,i)=>bullet(s,t,0.98,2.84+i*0.57,5.15,C.green));
  card(s,6.78,1.88,5.94,4.88,'FFF9EE','ECD6AB'); pill(s,'NOT YET QUALIFIED',7.11,2.16,1.63,C.paleAmber,C.amber);
  ['Physical Samsung / custom appliance image','USB-Ethernet + physical SPAN/TAP compatibility','Packet loss and behavior at production rates','Real PLC firmware and production OT networks','Encrypted multi-user case vault + reviewer signature','Deterministic signed final report package'].forEach((t,i)=>bullet(s,t,7.14,2.84+i*0.57,5.08,C.amber));
  s.addShape(pptx.ShapeType.roundRect,{x:3.85,y:6.16,w:5.62,h:0.44,rectRadius:0.06,fill:{color:C.navy},line:{color:C.navy}}); s.addText('The pilot exists to close the physical-field qualification gap.',{x:4.04,y:6.28,w:5.25,h:0.18,fontSize:9.8,bold:true,color:C.white,align:'center',margin:0}); footer(s);
  notes(s,['Source: IMPLEMENTATION.md','Source: .github/workflows/android-ci.yml','Source: docs/architecture/DEDICATED-ANDROID-APPLIANCE.md']);
}

{
  const s=pptx.addSlide(); s.background={color:C.navy}; s.addShape(pptx.ShapeType.rect,{x:0,y:0,w:13.333,h:0.055,fill:{color:C.aqua},line:{color:C.aqua}});
  s.addText('THE PILOT ASK',{x:0.68,y:0.63,w:3,h:0.22,fontSize:8.5,bold:true,color:C.aqua,charSpacing:1.3,margin:0});
  s.addText('Pilot one bounded water segment.',{x:0.68,y:1.08,w:7.6,h:0.68,fontFace:'Aptos Display',fontSize:31,bold:true,color:C.white,margin:0,fit:'shrink'});
  s.addText('Not a plant-wide scan. Not compliance certification. A witnessed test of whether the evidence pack helps an authorized assessor make a better decision.',{x:0.70,y:1.90,w:8.4,h:0.82,fontSize:13.8,color:'D1DEEA',margin:0,fit:'shrink'});
  const steps=[['1','Scope','Name one treatment, pumping or wastewater control segment.'],['2','Baseline','Use approved passive evidence first.'],['3','Close a gap','Run one exact active identity request only when needed.'],['4','Review','Judge inventory delta, evidence quality and remaining blockers.']];
  steps.forEach((st,i)=>{const x=0.72+i*3.06; card(s,x,3.18,2.72,2.12,i===3?'183A55':'12304A',i===3?'2B617D':'244B63'); s.addShape(pptx.ShapeType.ellipse,{x:x+0.18,y:3.45,w:0.48,h:0.48,fill:{color:C.aqua},line:{color:C.aqua}}); s.addText(st[0],{x:x+0.18,y:3.56,w:0.48,h:0.18,fontSize:10,bold:true,color:C.navy,align:'center',margin:0}); s.addText(st[1],{x:x+0.78,y:3.46,w:1.62,h:0.3,fontSize:14.5,bold:true,color:C.white,margin:0,fit:'shrink'}); s.addText(st[2],{x:x+0.20,y:4.14,w:2.28,h:0.74,fontSize:9.6,color:'BFD0DF',margin:0,fit:'shrink'});});
  play(s,0.72,5.78,4.0); s.addText('Decision after the pilot',{x:7.30,y:5.72,w:2.4,h:0.24,fontSize:8.5,bold:true,color:C.aqua,charSpacing:0.8,margin:0}); s.addText('Is the evidence good enough to justify physical hardware qualification and a controlled field evaluation?',{x:7.30,y:6.04,w:5.15,h:0.62,fontSize:14.4,bold:true,color:C.white,margin:0,fit:'shrink'}); s.addText('ATLAS OT SCOUT  /  P0-WATER',{x:0.70,y:6.90,w:3,h:0.22,fontSize:7.8,bold:true,color:'6FA9B7',charSpacing:1.2,margin:0});
  notes(s,['Source: docs/poc/WATER-WASTEWATER-POC.md','Source: ROADMAP.md','Source: docs/accounts/PORTFOLIO-SYNTHESIS.md','A successful pilot earns the next investment in physical qualification.']);
}

fs.mkdirSync(path.dirname(OUT),{recursive:true});
pptx.writeFile({fileName:OUT});
console.log(`Wrote ${OUT}`);
