/**
 * AquaGuard - Municipal Smart Public Beach Safety & Water Quality Monitoring System
 * Frontend Application Controller
 */

// State Cache
let sectorsData = [];
let samplesData = [];
let activeRiskFilter = 'ALL';

// DOM Elements
const toast = document.getElementById('notice-toast');
const toastMessage = document.getElementById('toast-message');
const toastIcon = document.getElementById('toast-icon');

// Navigation Tabs
document.querySelectorAll('.nav-tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

        tab.classList.add('active');
        const targetId = tab.getAttribute('data-tab');
        const targetSection = document.getElementById(targetId);
        if (targetSection) {
            targetSection.classList.add('active');
        }
    });
});

// Toast notification helper
function showToast(message, type = 'info') {
    if (!toast) return;
    toastMessage.textContent = message;
    toast.className = 'toast';
    
    if (type === 'error') {
        toast.classList.add('toast-error');
        toastIcon.textContent = '❌';
    } else if (type === 'success') {
        toast.classList.add('toast-success');
        toastIcon.textContent = '✅';
    } else {
        toastIcon.textContent = 'ℹ️';
    }

    toast.hidden = false;
    clearTimeout(toast._timeout);
    toast._timeout = setTimeout(() => {
        toast.hidden = true;
    }, 4500);
}

// Fetch API helper
async function api(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                ...(options.body ? { 'Content-Type': 'application/json' } : {}),
                ...options.headers
            }
        });

        if (!response.ok) {
            let errorText = await response.text();
            try {
                const json = JSON.parse(errorText);
                errorText = json.message || errorText;
            } catch (_) {}
            throw new Error(errorText || `HTTP ${response.status}`);
        }

        if (response.status === 204) return null;
        return await response.json();
    } catch (err) {
        throw err;
    }
}

// Format local date for input
function getCurrentLocalDateTime() {
    const now = new Date();
    const pad = (n) => String(n).padStart(2, '0');
    return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
}

// Set initial datetime inputs
const sampleDateInput = document.getElementById('form-sample-date');
const modalDateInput = document.getElementById('modal-sample-date');
if (sampleDateInput) sampleDateInput.value = getCurrentLocalDateTime();
if (modalDateInput) modalDateInput.value = getCurrentLocalDateTime();

const btnSetNow = document.getElementById('btn-set-now');
if (btnSetNow && sampleDateInput) {
    btnSetNow.addEventListener('click', () => {
        sampleDateInput.value = getCurrentLocalDateTime();
        showToast('Set to current municipal time', 'info');
    });
}

// Live Risk Predictor calculation
function updateRiskPreview(countInputId, thresholdInputId, previewPillId, previewTextId, previewRuleId) {
    const countEl = document.getElementById(countInputId);
    const threshEl = document.getElementById(thresholdInputId);
    const pillEl = document.getElementById(previewPillId);
    const textEl = document.getElementById(previewTextId);
    const ruleEl = document.getElementById(previewRuleId);

    if (!countEl || !threshEl || !textEl) return;

    const count = parseFloat(countEl.value);
    const threshold = parseFloat(threshEl.value) || 100.0;

    if (isNaN(count)) {
        textEl.textContent = 'Enter bacteria count to calculate risk';
        if (ruleEl) ruleEl.textContent = 'Awaiting laboratory measurement.';
        return;
    }

    if (count > threshold) {
        textEl.innerHTML = `<span class="preview-dot red-dot"></span> 🔴 RED FLAG — Exceeds Threshold (${count} > ${threshold} CFU)`;
        if (pillEl) pillEl.className = 'risk-preview-pill text-rose';
        if (ruleEl) ruleEl.textContent = '⚠️ Automatically triggers Red Flag & swimming advisory warning.';
    } else {
        textEl.innerHTML = `<span class="preview-dot green-dot"></span> 🟢 GREEN — Safe for Swimming (${count} ≤ ${threshold} CFU)`;
        if (pillEl) pillEl.className = 'risk-preview-pill text-emerald';
        if (ruleEl) ruleEl.textContent = 'Water quality satisfies municipal & EPA bacteriological criteria.';
    }
}

const formBacteriaInput = document.getElementById('form-bacteria-count');
const formThresholdInput = document.getElementById('form-safety-threshold');
if (formBacteriaInput && formThresholdInput) {
    formBacteriaInput.addEventListener('input', () => updateRiskPreview('form-bacteria-count', 'form-safety-threshold', 'risk-preview-pill', 'risk-preview-text', 'risk-preview-rule'));
    formThresholdInput.addEventListener('input', () => updateRiskPreview('form-bacteria-count', 'form-safety-threshold', 'risk-preview-pill', 'risk-preview-text', 'risk-preview-rule'));
}

const modalBacteriaInput = document.getElementById('modal-bacteria-count');
const modalThresholdInput = document.getElementById('modal-safety-threshold');
if (modalBacteriaInput && modalThresholdInput) {
    modalBacteriaInput.addEventListener('input', () => updateRiskPreview('modal-bacteria-count', 'modal-safety-threshold', null, 'modal-preview-text', null));
    modalThresholdInput.addEventListener('input', () => updateRiskPreview('modal-bacteria-count', 'modal-safety-threshold', null, 'modal-preview-text', null));
}

// Modal handling
const modal = document.getElementById('sample-modal');
const btnOpenModal = document.getElementById('btn-open-sample-modal');
const btnCloseModal = document.getElementById('btn-close-modal');
const btnCancelModal = document.getElementById('btn-cancel-modal');

function openModal(preselectSectorId = null) {
    if (!modal) return;
    modal.style.display = 'grid';
    if (modalDateInput) modalDateInput.value = getCurrentLocalDateTime();
    if (preselectSectorId) {
        const modalSelect = document.getElementById('modal-sector-select');
        if (modalSelect) modalSelect.value = preselectSectorId;
    }
}

function closeModal() {
    if (modal) modal.style.display = 'none';
}

if (btnOpenModal) btnOpenModal.addEventListener('click', () => openModal());
if (btnCloseModal) btnCloseModal.addEventListener('click', closeModal);
if (btnCancelModal) btnCancelModal.addEventListener('click', closeModal);

window.addEventListener('click', (e) => {
    if (e.target === modal) closeModal();
});

// Quick Sector Preset Chips
document.querySelectorAll('.chip-preset').forEach(chip => {
    chip.addEventListener('click', () => {
        const name = chip.getAttribute('data-name');
        const loc = chip.getAttribute('data-loc');
        const nameInput = document.getElementById('form-sector-name');
        const locInput = document.getElementById('form-sector-location');
        if (nameInput) nameInput.value = name;
        if (locInput) locInput.value = loc;
    });
});

// Jump buttons
const btnJumpSamples = document.getElementById('btn-jump-to-samples');
if (btnJumpSamples) {
    btnJumpSamples.addEventListener('click', () => {
        const samplesTab = document.querySelector('[data-tab="tab-samples"]');
        if (samplesTab) samplesTab.click();
    });
}

const btnViewAlert = document.getElementById('btn-view-alert-samples');
if (btnViewAlert) {
    btnViewAlert.addEventListener('click', () => {
        const samplesTab = document.querySelector('[data-tab="tab-samples"]');
        if (samplesTab) samplesTab.click();
        const riskFilter = document.getElementById('filter-sample-risk');
        if (riskFilter) {
            riskFilter.value = 'RED';
            activeRiskFilter = 'RED';
            renderSamplesTable();
        }
    });
}

// Quick Demo Seed Handler
const btnQuickSeed = document.getElementById('btn-quick-seed');
if (btnQuickSeed) {
    btnQuickSeed.addEventListener('click', async () => {
        try {
            btnQuickSeed.disabled = true;
            btnQuickSeed.textContent = '⏳ Loading...';
            await api('/api/beach/seed-demo', { method: 'POST' });
            showToast('Loaded municipal coastal demo sectors & samples!', 'success');
            await loadAllData();
        } catch (err) {
            showToast(`Demo seed info: ${err.message}`, 'info');
            await loadAllData();
        } finally {
            btnQuickSeed.disabled = false;
            btnQuickSeed.textContent = '⚡ Load Demo Data';
        }
    });
}

// --------------------------------------------------------------------------
// Core Data Fetching & UI Rendering
// --------------------------------------------------------------------------

async function loadAllData() {
    try {
        const [sectors, samples] = await Promise.all([
            api('/api/beach/sectors'),
            api('/api/beach/samples').catch(() => [])
        ]);

        sectorsData = sectors || [];
        samplesData = samples || [];

        renderSectorDropdowns();
        renderSectorsTable();
        renderSamplesTable();
        renderOverviewMetrics();
        renderOverviewSectorCards();
        renderRecentSamplesFeed();
    } catch (err) {
        showToast(`Could not connect to backend: ${err.message}`, 'error');
    }
}

// Render dropdown selectors
function renderSectorDropdowns() {
    const mainSelect = document.getElementById('form-sample-sector');
    const modalSelect = document.getElementById('modal-sector-select');

    const options = [
        '<option value="">Select a beach sector...</option>',
        ...sectorsData.map(s => `<option value="${s.id}">${s.name} (${s.location || 'Coastal Zone'})</option>`)
    ].join('');

    if (mainSelect) {
        const currentVal = mainSelect.value;
        mainSelect.innerHTML = options;
        if (currentVal) mainSelect.value = currentVal;
    }

    if (modalSelect) {
        const currentVal = modalSelect.value;
        modalSelect.innerHTML = options;
        if (currentVal) modalSelect.value = currentVal;
    }
}

// Render Overview KPI metrics & Emergency Alert Banner
function renderOverviewMetrics() {
    const totalSectorsEl = document.getElementById('kpi-total-sectors');
    const greenPercentEl = document.getElementById('kpi-green-percent');
    const redCountEl = document.getElementById('kpi-red-count');
    const redCardEl = document.getElementById('kpi-red-card');
    const redBadgeEl = document.getElementById('kpi-red-badge');
    const redFooterEl = document.getElementById('kpi-red-footer');
    const avgBacteriaEl = document.getElementById('kpi-avg-bacteria');
    const complianceBadge = document.getElementById('kpi-compliance-badge');
    const emergencyBanner = document.getElementById('emergency-alert-banner');
    const emergencyText = document.getElementById('emergency-alert-text');
    const safeBanner = document.getElementById('safe-status-banner');

    if (totalSectorsEl) totalSectorsEl.textContent = sectorsData.length;

    // Analyze latest sample per sector
    let redSectors = [];
    let greenCount = 0;
    let totalBacteria = 0;

    sectorsData.forEach(sector => {
        const sectorSamples = samplesData.filter(s => s.sectorId === sector.id);
        if (sectorSamples.length > 0) {
            const latest = sectorSamples[0];
            totalBacteria += latest.bacteriaCount;
            if (latest.riskLevel === 'RED') {
                redSectors.push({ sector, sample: latest });
            } else {
                greenCount++;
            }
        }
    });

    const monitoredCount = redSectors.length + greenCount;
    const greenPct = monitoredCount > 0 ? Math.round((greenCount / monitoredCount) * 100) : 100;
    const avgBact = monitoredCount > 0 ? (totalBacteria / monitoredCount).toFixed(1) : '0.0';

    if (greenPercentEl) greenPercentEl.textContent = `${greenPct}%`;
    if (complianceBadge) complianceBadge.textContent = `${greenPct}% Compliant`;
    if (avgBacteriaEl) avgBacteriaEl.textContent = avgBact;

    // Emergency Alert Logic
    if (redSectors.length > 0) {
        if (redCountEl) redCountEl.textContent = redSectors.length;
        if (redBadgeEl) redBadgeEl.textContent = `${redSectors.length} Red Flag${redSectors.length > 1 ? 's' : ''}`;
        if (redCardEl) redCardEl.classList.add('has-alert');
        if (redFooterEl) redFooterEl.textContent = 'Public health swimming warnings active';

        // Show prominent emergency banner
        if (emergencyBanner) {
            emergencyBanner.style.display = 'flex';
            const sectorNames = redSectors.map(r => `<strong>${r.sector.name}</strong> (${r.sample.bacteriaCount} CFU)`).join(', ');
            if (emergencyText) {
                emergencyText.innerHTML = `Elevated bacterial counts detected in: ${sectorNames}. Safety threshold is 100.0 CFU/100mL. Red flags hoisted. Swimming advisory in effect.`;
            }
        }
        if (safeBanner) safeBanner.style.display = 'none';

        // Update lifeguard tower 3 flag if affected
        const t3Flag = document.getElementById('tower-3-flag');
        const t3Badge = document.getElementById('tower-3-badge');
        if (t3Flag) t3Flag.innerHTML = '🚩 RED HAZARD';
        if (t3Badge) {
            t3Badge.className = 'badge badge-red';
            t3Badge.textContent = 'RED ADVISORY';
        }
    } else {
        if (redCountEl) redCountEl.textContent = '0';
        if (redBadgeEl) redBadgeEl.textContent = '0 Alerts';
        if (redCardEl) redCardEl.classList.remove('has-alert');
        if (redFooterEl) redFooterEl.textContent = 'No swimming bans active';

        if (emergencyBanner) emergencyBanner.style.display = 'none';
        if (safeBanner) safeBanner.style.display = 'flex';
    }
}

// Render Beach Sector Status Cards in Overview
function renderOverviewSectorCards() {
    const grid = document.getElementById('sector-cards-grid');
    if (!grid) return;

    if (sectorsData.length === 0) {
        grid.innerHTML = `
            <div class="empty-state">
                <p>No beach sectors registered yet.</p>
                <button class="btn-primary mt-4" onclick="document.querySelector('[data-tab=\\'tab-sectors\\']').click()">
                    + Add First Sector
                </button>
            </div>
        `;
        return;
    }

    grid.innerHTML = sectorsData.map((sector, idx) => {
        const sectorSamples = samplesData.filter(s => s.sectorId === sector.id);
        const latestSample = sectorSamples.length > 0 ? sectorSamples[0] : null;

        const isRed = latestSample && latestSample.riskLevel === 'RED';
        const cardClass = isRed ? 'flag-red' : 'flag-green';
        const riskText = isRed ? '🔴 RED FLAG' : (latestSample ? '🟢 GREEN FLAG' : '⚪ PENDING');
        const riskClass = isRed ? 'badge-red' : 'badge-green';

        const bacteriaVal = latestSample ? latestSample.bacteriaCount : '--';
        const thresholdVal = latestSample ? latestSample.safetyThreshold : 100.0;
        const pct = latestSample ? Math.min(Math.round((latestSample.bacteriaCount / latestSample.safetyThreshold) * 100), 100) : 0;
        const barFillClass = isRed ? 'fill-red' : 'fill-green';

        // Simulated telemetry parameters per sector
        const temps = ['22.4°C', '21.8°C', '23.1°C', '22.0°C'];
        const waves = ['1.1m Gentle', '0.8m Calm', '1.6m Moderate', '1.2m Rolling'];
        const temp = temps[idx % temps.length];
        const wave = waves[idx % waves.length];

        const dateStr = latestSample ? new Date(latestSample.collectedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'Never';

        return `
            <div class="sector-card ${cardClass}">
                <div class="sector-card-top">
                    <div>
                        <div class="sector-name">${escapeHtml(sector.name)}</div>
                        <div class="sector-location">${escapeHtml(sector.location || 'Coastal Perimeter')}</div>
                    </div>
                    <span class="risk-flag-pill ${riskClass}">${riskText}</span>
                </div>

                <div class="sector-metrics">
                    <div class="metric-row">
                        <span class="metric-label">Latest Bacteria:</span>
                        <span class="metric-value ${isRed ? 'text-rose' : 'text-emerald'}">${bacteriaVal} CFU</span>
                    </div>
                    <div class="progress-container">
                        <div class="progress-fill ${barFillClass}" style="width: ${pct}%;"></div>
                    </div>
                    <div class="metric-row">
                        <span class="metric-label">Safety Threshold:</span>
                        <span class="metric-value">${thresholdVal} CFU</span>
                    </div>
                    <div class="metric-row">
                        <span class="metric-label">Water Temp / Wave:</span>
                        <span class="metric-value">${temp} • ${wave}</span>
                    </div>
                    <div class="metric-row">
                        <span class="metric-label">Last Sampled:</span>
                        <span class="metric-value">${dateStr}</span>
                    </div>
                </div>

                <div class="sector-card-actions">
                    <button class="btn-card-action" onclick="openModal(${sector.id})">
                        + Log Water Sample
                    </button>
                    <button class="btn-card-action" onclick="filterBySector(${sector.id})">
                        View History (${sectorSamples.length})
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

// Render Recent Samples Feed in Overview
function renderRecentSamplesFeed() {
    const feed = document.getElementById('recent-samples-feed');
    if (!feed) return;

    if (samplesData.length === 0) {
        feed.innerHTML = '<div class="empty-state">No water samples recorded yet.</div>';
        return;
    }

    const recent = samplesData.slice(0, 4);
    feed.innerHTML = recent.map(sample => {
        const isRed = sample.riskLevel === 'RED';
        const badgeClass = isRed ? 'badge-red' : 'badge-green';
        const timeAgo = formatTimeAgo(new Date(sample.collectedAt));

        return `
            <div class="recent-sample-card">
                <div class="recent-sample-info">
                    <strong>${escapeHtml(sample.sectorName || 'Sector')}</strong>
                    <div class="recent-sample-meta">${timeAgo} • ${new Date(sample.collectedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                </div>
                <div style="text-align: right;">
                    <span class="badge ${badgeClass}">${sample.riskLevel}</span>
                    <div style="font-size: 11px; font-family: var(--font-mono); color: var(--text-muted); margin-top: 3px;">
                        ${sample.bacteriaCount} CFU
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// Render Samples Table in Tab 2
function renderSamplesTable() {
    const tbody = document.getElementById('samples-table-body');
    if (!tbody) return;

    let filtered = samplesData;
    if (activeRiskFilter !== 'ALL') {
        filtered = filtered.filter(s => s.riskLevel === activeRiskFilter);
    }

    if (filtered.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    ${samplesData.length === 0 ? 'No water quality samples ingested yet. Ingest one using the form on the left.' : 'No samples matching the selected filter.'}
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filtered.map(sample => {
        const isRed = sample.riskLevel === 'RED';
        const badgeClass = isRed ? 'badge-red' : 'badge-green';
        const fillClass = isRed ? 'background: #ef4444' : 'background: #10b981';
        const pct = Math.min(Math.round((sample.bacteriaCount / sample.safetyThreshold) * 100), 100);

        return `
            <tr>
                <td><code>#WS-${String(sample.id).padStart(4, '0')}</code></td>
                <td><strong>${escapeHtml(sample.sectorName || 'Sector ' + sample.sectorId)}</strong></td>
                <td>${new Date(sample.collectedAt).toLocaleString()}</td>
                <td><strong class="${isRed ? 'text-rose' : 'text-emerald'}">${sample.bacteriaCount} CFU</strong></td>
                <td>${sample.safetyThreshold} CFU</td>
                <td>
                    <div class="table-bar-wrap">
                        <div class="table-bar-track">
                            <div class="table-bar-fill" style="width: ${pct}%; ${fillClass}"></div>
                        </div>
                        <span class="table-bar-percent">${pct}%</span>
                    </div>
                </td>
                <td><span class="badge ${badgeClass}">${sample.riskLevel} FLAG</span></td>
            </tr>
        `;
    }).join('');
}

// Render Sectors Table in Tab 3
function renderSectorsTable() {
    const tbody = document.getElementById('sectors-table-body');
    if (!tbody) return;

    if (sectorsData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-state">
                    No beach sectors registered. Register a sector using the form on the left.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = sectorsData.map(sector => {
        const count = samplesData.filter(s => s.sectorId === sector.id).length;
        return `
            <tr>
                <td><code>#SEC-${String(sector.id).padStart(3, '0')}</code></td>
                <td><strong>${escapeHtml(sector.name)}</strong></td>
                <td>${escapeHtml(sector.location || 'Municipal Coastline')}</td>
                <td><span class="badge badge-green">Surveillance Active</span></td>
                <td>
                    <button class="btn-chip" onclick="openModal(${sector.id})">
                        + Ingest Sample (${count})
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

// Filter helper from card
window.filterBySector = function(sectorId) {
    const tabSamples = document.querySelector('[data-tab="tab-samples"]');
    if (tabSamples) tabSamples.click();
    const select = document.getElementById('form-sample-sector');
    if (select) select.value = sectorId;
};

window.openModal = openModal;

// Risk Filter listener
const riskFilterSelect = document.getElementById('filter-sample-risk');
if (riskFilterSelect) {
    riskFilterSelect.addEventListener('change', (e) => {
        activeRiskFilter = e.target.value;
        renderSamplesTable();
    });
}

// --------------------------------------------------------------------------
// Form Submissions
// --------------------------------------------------------------------------

// 1. Submit Sector Form
const sectorForm = document.getElementById('beach-sector-form');
if (sectorForm) {
    sectorForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nameInput = document.getElementById('form-sector-name');
        const locInput = document.getElementById('form-sector-location');
        const submitBtn = document.getElementById('btn-submit-sector');

        try {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span>⏳</span> Registering...';

            await api('/api/beach/sectors', {
                method: 'POST',
                body: JSON.stringify({
                    name: nameInput.value.trim(),
                    location: locInput.value.trim() || null
                })
            });

            showToast(`Sector "${nameInput.value.trim()}" registered successfully!`, 'success');
            sectorForm.reset();
            await loadAllData();
        } catch (err) {
            showToast(`Could not register sector: ${err.message}`, 'error');
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span>🏖️</span> Register Beach Sector';
        }
    });
}

// 2. Submit Main Water Sample Form
const sampleForm = document.getElementById('water-sample-form');
if (sampleForm) {
    sampleForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const sectorId = document.getElementById('form-sample-sector').value;
        const dateVal = document.getElementById('form-sample-date').value;
        const bacteria = parseFloat(document.getElementById('form-bacteria-count').value);
        const threshold = parseFloat(document.getElementById('form-safety-threshold').value);
        const submitBtn = document.getElementById('btn-submit-sample');

        if (!sectorId) {
            showToast('Please select a beach sector', 'error');
            return;
        }

        try {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span>⏳</span> Ingesting...';

            const payload = {
                sectorId: Number(sectorId),
                collectedAt: new Date(dateVal).toISOString(),
                bacteriaCount: bacteria,
                safetyThreshold: threshold
            };

            const response = await api('/api/beach/samples', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            const isRed = response.riskLevel === 'RED';
            showToast(
                isRed ? `⚠️ RED ALERT! Bacteria ${bacteria} CFU exceeds threshold. Alert issued.` : `Sample saved: Status GREEN (Compliant).`,
                isRed ? 'error' : 'success'
            );

            sampleForm.reset();
            if (sampleDateInput) sampleDateInput.value = getCurrentLocalDateTime();
            updateRiskPreview('form-bacteria-count', 'form-safety-threshold', 'risk-preview-pill', 'risk-preview-text', 'risk-preview-rule');

            await loadAllData();
        } catch (err) {
            showToast(`Could not ingest sample: ${err.message}`, 'error');
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span>🧪</span> Ingest Laboratory Sample';
        }
    });
}

// 3. Submit Modal Water Sample Form
const modalForm = document.getElementById('modal-sample-form');
if (modalForm) {
    modalForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const sectorId = document.getElementById('modal-sector-select').value;
        const dateVal = document.getElementById('modal-sample-date').value;
        const bacteria = parseFloat(document.getElementById('modal-bacteria-count').value);
        const threshold = parseFloat(document.getElementById('modal-safety-threshold').value);

        if (!sectorId) {
            showToast('Please select a beach sector', 'error');
            return;
        }

        try {
            const payload = {
                sectorId: Number(sectorId),
                collectedAt: new Date(dateVal).toISOString(),
                bacteriaCount: bacteria,
                safetyThreshold: threshold
            };

            const response = await api('/api/beach/samples', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            closeModal();
            modalForm.reset();

            const isRed = response.riskLevel === 'RED';
            showToast(
                isRed ? `⚠️ RED ALERT! High bacteria count recorded. Advisory active.` : `Sample ingested: Status GREEN.`,
                isRed ? 'error' : 'success'
            );

            await loadAllData();
        } catch (err) {
            showToast(`Could not ingest sample: ${err.message}`, 'error');
        }
    });
}

// Refresh buttons
const btnRefreshOverview = document.getElementById('btn-refresh-overview');
if (btnRefreshOverview) btnRefreshOverview.addEventListener('click', () => { loadAllData(); showToast('Telemetry refreshed', 'info'); });

const btnRefreshSectors = document.getElementById('btn-refresh-sectors-table');
if (btnRefreshSectors) btnRefreshSectors.addEventListener('click', () => { loadAllData(); showToast('Sectors refreshed', 'info'); });

const btnRefreshSamples = document.getElementById('btn-refresh-samples-table');
if (btnRefreshSamples) btnRefreshSamples.addEventListener('click', () => { loadAllData(); showToast('Samples refreshed', 'info'); });

// Utility helpers
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function formatTimeAgo(date) {
    const seconds = Math.floor((new Date() - date) / 1000);
    if (seconds < 60) return 'Just now';
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}m ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    return `${Math.floor(hours / 24)}d ago`;
}

// Initialize on page load
loadAllData();
