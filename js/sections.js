// ============================================================
// SECTIONS.JS - Topic navigation, code loading & search filter
// Adapted for Java module (loads .java files from subfolders)
// ============================================================

var codeCache = {};
var _searchTimer = null;

function debouncedSearch() {
  if (_searchTimer) clearTimeout(_searchTimer);
  _searchTimer = setTimeout(function () {
    search();
  }, 180);
}

/**
 * Search history module
 */
var SearchHistory = (function () {
  var STORAGE_KEY = 'sjb-search-history';
  var MAX_ITEMS = 8;

  function getHistory() {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY)) || [];
    } catch (e) { return []; }
  }

  function addEntry(query) {
    if (!query || query.length < 2) return;
    var history = getHistory();
    var idx = history.indexOf(query);
    if (idx !== -1) history.splice(idx, 1);
    history.unshift(query);
    if (history.length > MAX_ITEMS) history.pop();
    localStorage.setItem(STORAGE_KEY, JSON.stringify(history));
  }

  function clearHistory() {
    localStorage.removeItem(STORAGE_KEY);
  }

  return { getHistory: getHistory, addEntry: addEntry, clearHistory: clearHistory };
})();

/**
 * Topic notes module
 */
var TopicNotes = (function () {
  var STORAGE_KEY = 'sjb-topic-notes';

  function getAll() {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {};
    } catch (e) { return {}; }
  }

  function get(topicName) {
    return getAll()[topicName] || '';
  }

  function set(topicName, text) {
    var notes = getAll();
    if (text.trim()) {
      notes[topicName] = text;
    } else {
      delete notes[topicName];
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notes));
  }

  function count() {
    return Object.keys(getAll()).length;
  }

  return { getAll: getAll, get: get, set: set, count: count };
})();

/**
 * Resume session module — tracks last opened topic
 */
var ResumeSession = (function () {
  var KEY = 'sjb-last-session';

  function save(topicName) {
    var data = {
      topic: topicName,
      timestamp: Date.now()
    };
    localStorage.setItem(KEY, JSON.stringify(data));
  }

  function get() {
    try {
      return JSON.parse(localStorage.getItem(KEY));
    } catch (e) { return null; }
  }

  function clear() {
    localStorage.removeItem(KEY);
  }

  return { save: save, get: get, clear: clear };
})();

/**
 * Filter state persistence module
 */
var FilterState = (function () {
  var KEY = 'sjb-filter-state';

  function save(category, favActive) {
    var data = { category: category, favActive: favActive };
    localStorage.setItem(KEY, JSON.stringify(data));
  }

  function get() {
    try {
      return JSON.parse(localStorage.getItem(KEY)) || { category: 'all', favActive: false };
    } catch (e) {
      return { category: 'all', favActive: false };
    }
  }

  return { save: save, get: get };
})();

/**
 * Code font size persistence module
 */
var CodeFontSize = (function () {
  var KEY = 'sjb-code-font-size';
  var DEFAULT_SIZE = 0.82;
  var MIN_SIZE = 0.62;
  var MAX_SIZE = 1.12;
  var STEP = 0.05;

  function getSize() {
    try {
      var stored = parseFloat(localStorage.getItem(KEY));
      return isNaN(stored) ? DEFAULT_SIZE : Math.max(MIN_SIZE, Math.min(MAX_SIZE, stored));
    } catch (e) {
      return DEFAULT_SIZE;
    }
  }

  function setSize(size) {
    size = Math.max(MIN_SIZE, Math.min(MAX_SIZE, size));
    localStorage.setItem(KEY, String(size));
    return size;
  }

  function increase() {
    return setSize(Math.round((getSize() + STEP) * 100) / 100);
  }

  function decrease() {
    return setSize(Math.round((getSize() - STEP) * 100) / 100);
  }

  function reset() {
    localStorage.removeItem(KEY);
    return DEFAULT_SIZE;
  }

  function isDefault() {
    return getSize() === DEFAULT_SIZE;
  }

  function isMin() {
    return getSize() <= MIN_SIZE;
  }

  function isMax() {
    return getSize() >= MAX_SIZE;
  }

  return {
    getSize: getSize,
    increase: increase,
    decrease: decrease,
    reset: reset,
    isDefault: isDefault,
    isMin: isMin,
    isMax: isMax,
    DEFAULT_SIZE: DEFAULT_SIZE
  };
})();

/**
 * Show a toast notification
 */
function showToast(icon, message) {
  var container = document.getElementById('toastContainer');
  if (!container) return;

  var toast = document.createElement('div');
  toast.className = 'toast';
  toast.innerHTML = '<i class="material-icons">' + icon + '</i> ' + message;
  container.appendChild(toast);

  setTimeout(function () {
    if (toast.parentNode) toast.parentNode.removeChild(toast);
  }, 2500);
}

// Map topic names to their file paths
var topicPaths = {
  // basics
  variables_and_types:    'basics/VariablesAndTypes.java',
  operators_arithmetic:   'basics/OperatorsArithmetic.java',
  operators_assignment:   'basics/OperatorsAssignment.java',
  operators_logical:      'basics/OperatorsLogical.java',
  operators_conditional:  'basics/OperatorsConditional.java',
  control_flow:           'basics/ControlFlow.java',
  datetime_api:           'basics/DateTimeAPI.java',
  packages_and_access:    'basics/PackagesAndAccess.java',
  // good
  methods:                'good/Methods.java',
  strings:                'good/Strings.java',
  arrays:                 'good/ArraysDemo.java',
  optional:               'good/OptionalDemo.java',
  inner_classes:          'good/InnerClasses.java',
  regex:                  'good/RegexDemo.java',
  // pro
  oop_classes:            'pro/OopClasses.java',
  oop_inheritance:        'pro/OopInheritance.java',
  exceptions:             'pro/Exceptions.java',
  collections:            'pro/CollectionsFramework.java',
  annotations:            'pro/Annotations.java',
  jdbc:                   'pro/JDBC.java',
  junit:                  'pro/JUnitDemo.java',
  design_patterns:        'pro/DesignPatterns.java',
  build_tools:            'pro/BuildToolsDemo.java',
  // geek
  generics:               'geek/Generics.java',
  lambdas_and_functional: 'geek/LambdasAndFunctional.java',
  streams:                'geek/Streams.java',
  concurrency:            'geek/Concurrency.java',
  file_io:                'geek/FileIO.java',
  networking:             'geek/Networking.java',
  reflection:             'geek/Reflection.java',
  modules:                'geek/ModulesDemo.java'
};

// Related topics map — each topic has 2-3 related topics with a short reason
var topicRelations = {
  variables_and_types: [
    { topic: 'operators_arithmetic', reason: 'Operan con estos tipos' },
    { topic: 'strings', reason: 'String es un tipo clave' },
    { topic: 'oop_classes', reason: 'Tipos custom con clases' }
  ],
  operators_arithmetic: [
    { topic: 'variables_and_types', reason: 'Tipos sobre los que operan' },
    { topic: 'operators_assignment', reason: 'Asignación compuesta' },
    { topic: 'methods', reason: 'Encapsular cálculos' }
  ],
  operators_assignment: [
    { topic: 'operators_arithmetic', reason: 'Operadores aritméticos compuestos' },
    { topic: 'variables_and_types', reason: 'Asignación a variables tipadas' }
  ],
  operators_logical: [
    { topic: 'control_flow', reason: 'Condiciones en if/while' },
    { topic: 'operators_conditional', reason: 'Ternario usa booleanos' }
  ],
  operators_conditional: [
    { topic: 'control_flow', reason: 'Switch es flujo de control' },
    { topic: 'operators_logical', reason: 'Condiciones en ternario' }
  ],
  control_flow: [
    { topic: 'operators_logical', reason: 'Condiciones con operadores lógicos' },
    { topic: 'operators_conditional', reason: 'Switch y ternario' },
    { topic: 'lambdas_and_functional', reason: 'Alternativas funcionales a loops' }
  ],
  datetime_api: [
    { topic: 'variables_and_types', reason: 'Tipos temporales' },
    { topic: 'strings', reason: 'Formateo con DateTimeFormatter' },
    { topic: 'optional', reason: 'El parsing puede fallar' }
  ],
  packages_and_access: [
    { topic: 'oop_classes', reason: 'Modificadores de acceso en clases' },
    { topic: 'modules', reason: 'Modules extienden packages' },
    { topic: 'oop_inheritance', reason: 'Acceso protected en herencia' }
  ],
  methods: [
    { topic: 'oop_classes', reason: 'Los métodos viven en clases' },
    { topic: 'lambdas_and_functional', reason: 'Lambdas como method references' },
    { topic: 'exceptions', reason: 'Los métodos lanzan excepciones' }
  ],
  strings: [
    { topic: 'regex', reason: 'Pattern matching sobre strings' },
    { topic: 'arrays', reason: 'String a char array' },
    { topic: 'streams', reason: 'Stream processing de strings' }
  ],
  arrays: [
    { topic: 'collections', reason: 'Collections extienden arrays' },
    { topic: 'streams', reason: 'Stream desde arrays' },
    { topic: 'generics', reason: 'Arrays type-safe con generics' }
  ],
  optional: [
    { topic: 'streams', reason: 'findFirst devuelve Optional' },
    { topic: 'lambdas_and_functional', reason: 'map/flatMap son funcionales' },
    { topic: 'exceptions', reason: 'Evitar NullPointerException' }
  ],
  inner_classes: [
    { topic: 'oop_classes', reason: 'Anidadas dentro de clases' },
    { topic: 'lambdas_and_functional', reason: 'Lambdas reemplazan clases anónimas' },
    { topic: 'design_patterns', reason: 'Builder usa inner classes' }
  ],
  regex: [
    { topic: 'strings', reason: 'Regex opera sobre strings' },
    { topic: 'streams', reason: 'Pattern.splitAsStream' },
    { topic: 'methods', reason: 'Validación con regex' }
  ],
  oop_classes: [
    { topic: 'oop_inheritance', reason: 'Herencia extiende clases' },
    { topic: 'inner_classes', reason: 'Clases dentro de clases' },
    { topic: 'design_patterns', reason: 'Patrones basados en OOP' }
  ],
  oop_inheritance: [
    { topic: 'oop_classes', reason: 'Fundamento: clases' },
    { topic: 'generics', reason: 'Bounded types usan herencia' },
    { topic: 'annotations', reason: '@Override' }
  ],
  exceptions: [
    { topic: 'file_io', reason: 'Operaciones IO lanzan excepciones' },
    { topic: 'jdbc', reason: 'JDBC usa checked exceptions' },
    { topic: 'optional', reason: 'Optional evita null exceptions' }
  ],
  collections: [
    { topic: 'generics', reason: 'Collections son genéricas' },
    { topic: 'streams', reason: 'Stream desde collections' },
    { topic: 'lambdas_and_functional', reason: 'forEach, removeIf usan lambdas' }
  ],
  annotations: [
    { topic: 'reflection', reason: 'Leer annotations en runtime' },
    { topic: 'junit', reason: '@Test annotation' },
    { topic: 'oop_inheritance', reason: '@Override' }
  ],
  jdbc: [
    { topic: 'exceptions', reason: 'Manejo de SQLException' },
    { topic: 'collections', reason: 'Mapear result sets' },
    { topic: 'optional', reason: 'Valores nullable de BD' }
  ],
  junit: [
    { topic: 'annotations', reason: '@Test annotations' },
    { topic: 'exceptions', reason: 'assertThrows' },
    { topic: 'reflection', reason: 'Test runner usa reflection' }
  ],
  design_patterns: [
    { topic: 'oop_classes', reason: 'Fundamento OOP' },
    { topic: 'oop_inheritance', reason: 'Polimorfismo en patrones' },
    { topic: 'inner_classes', reason: 'Builder usa inner classes' }
  ],
  build_tools: [
    { topic: 'modules', reason: 'Builds de módulos' },
    { topic: 'junit', reason: 'Integración de test lifecycle' },
    { topic: 'jdbc', reason: 'Gestión de dependencias' }
  ],
  generics: [
    { topic: 'collections', reason: 'Collections son genéricas' },
    { topic: 'lambdas_and_functional', reason: 'Interfaces funcionales genéricas' },
    { topic: 'streams', reason: 'Streams usan generics' }
  ],
  lambdas_and_functional: [
    { topic: 'streams', reason: 'Streams consumen lambdas' },
    { topic: 'methods', reason: 'Method references' },
    { topic: 'concurrency', reason: 'CompletableFuture usa lambdas' }
  ],
  streams: [
    { topic: 'lambdas_and_functional', reason: 'Streams requieren lambdas' },
    { topic: 'collections', reason: 'Stream desde/hacia collections' },
    { topic: 'optional', reason: 'Operaciones devuelven Optional' }
  ],
  concurrency: [
    { topic: 'lambdas_and_functional', reason: 'Runnables como lambdas' },
    { topic: 'streams', reason: 'Parallel streams' },
    { topic: 'collections', reason: 'Concurrent collections' }
  ],
  file_io: [
    { topic: 'exceptions', reason: 'Manejo de IOException' },
    { topic: 'streams', reason: 'Files.lines() devuelve Stream' },
    { topic: 'networking', reason: 'I/O para comunicación de red' }
  ],
  networking: [
    { topic: 'file_io', reason: 'Conceptos de I/O compartidos' },
    { topic: 'concurrency', reason: 'HTTP requests async' },
    { topic: 'exceptions', reason: 'Manejo de errores de red' }
  ],
  reflection: [
    { topic: 'annotations', reason: 'Leer annotations via reflection' },
    { topic: 'oop_classes', reason: 'Inspeccionar estructura de clases' },
    { topic: 'modules', reason: 'Control de acceso de módulos' }
  ],
  modules: [
    { topic: 'packages_and_access', reason: 'Modules extienden packages' },
    { topic: 'reflection', reason: 'opens para acceso reflectivo' },
    { topic: 'build_tools', reason: 'Build tools gestionan módulos' }
  ]
};

/**
 * Scroll smoothly to a section by ID
 */
function scrollToSection(sectionId) {
  var section = document.getElementById(sectionId);
  if (section) {
    section.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}

/**
 * Toggle a topic open/closed and load its code on first open
 */
function toggleTopic(headerEl) {
  var topicEl = headerEl.closest('.section-topic');
  var contentEl = topicEl.querySelector('.section-topic-content');
  var isOpen = topicEl.classList.contains('open');

  if (isOpen) {
    topicEl.classList.remove('open');
    headerEl.setAttribute('aria-expanded', 'false');
    // Clear hash when closing
    if (window.location.hash === '#' + topicEl.dataset.topic) {
      history.replaceState(null, '', window.location.pathname);
    }
  } else {
    topicEl.classList.add('open');
    headerEl.setAttribute('aria-expanded', 'true');

    if (!contentEl.dataset.loaded) {
      loadTopicCode(topicEl.dataset.topic, contentEl);
    }

    // Mark topic as read
    markTopicRead(topicEl.dataset.topic);

    // Update URL hash for deep linking
    history.replaceState(null, '', '#' + topicEl.dataset.topic);

    // Save session for resume
    ResumeSession.save(topicEl.dataset.topic);

    // Scroll topic into view after a short delay for animation
    setTimeout(function () {
      topicEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }, 100);
  }
}

/**
 * Progress tracking: mark a topic as read in localStorage
 */
function markTopicRead(topicName) {
  var read = getReadTopics();
  if (read.indexOf(topicName) === -1) {
    read.push(topicName);
    localStorage.setItem('sjb-read-topics', JSON.stringify(read));
    updateReadBadge(topicName);
    updateSectionProgress();
  }
}

function getReadTopics() {
  try {
    return JSON.parse(localStorage.getItem('sjb-read-topics')) || [];
  } catch (e) {
    return [];
  }
}

function updateReadBadge(topicName) {
  var topicEl = document.querySelector('.section-topic[data-topic="' + topicName + '"]');
  if (!topicEl || topicEl.querySelector('.topic-read-badge')) return;

  var titleEl = topicEl.querySelector('.section-topic-title');
  var badge = document.createElement('i');
  badge.className = 'material-icons topic-read-badge';
  badge.textContent = 'check_circle';
  badge.title = 'Leído';
  titleEl.appendChild(badge);
}

/**
 * Favorites system
 */
function getFavorites() {
  try {
    return JSON.parse(localStorage.getItem('sjb-favorites')) || [];
  } catch (e) {
    return [];
  }
}

function toggleFavorite(topicName) {
  var favs = getFavorites();
  var idx = favs.indexOf(topicName);
  if (idx === -1) {
    favs.push(topicName);
  } else {
    favs.splice(idx, 1);
  }
  localStorage.setItem('sjb-favorites', JSON.stringify(favs));
  updateFavCount();
  return idx === -1; // returns true if added
}

function updateFavCount() {
  var el = document.getElementById('favCount');
  if (el) el.textContent = getFavorites().length;
}

var favFilterActive = false;
var activeCategory = 'all';

var completedSections = {};

function updateSectionProgress() {
  var read = getReadTopics();
  var globalTotal = 0;
  var globalRead = 0;

  document.querySelectorAll('.section').forEach(function (section) {
    var topics = section.querySelectorAll('.section-topic');
    var total = topics.length;
    var readCount = 0;
    topics.forEach(function (t) {
      if (read.indexOf(t.dataset.topic) !== -1) readCount++;
    });

    globalTotal += total;
    globalRead += readCount;

    var progressEl = section.querySelector('.section-progress');
    if (!progressEl) return;
    progressEl.textContent = readCount + '/' + total;

    var sectionId = section.id;
    if (readCount === total) {
      progressEl.classList.add('complete');
      // Fire celebration if this section just became complete
      if (!completedSections[sectionId]) {
        completedSections[sectionId] = true;
        var sectionName = section.querySelector('.section-title').childNodes[0].textContent.trim();
        showCelebration(sectionName);
      }
    } else {
      progressEl.classList.remove('complete');
      completedSections[sectionId] = false;
    }
  });

  // Update global progress badge in banner
  var globalEl = document.getElementById('globalProgress');
  if (globalEl && globalTotal > 0) {
    var pct = Math.round((globalRead / globalTotal) * 100);
    globalEl.textContent = pct + ' % leído';
    if (pct === 100) {
      globalEl.classList.add('complete');
    } else {
      globalEl.classList.remove('complete');
    }
  }

  // Auto-refresh stats dashboard if visible
  if (typeof StatsDashboard !== 'undefined') {
    var sp = document.getElementById('statsPanel');
    if (sp && sp.classList.contains('visible')) StatsDashboard.refresh();
  }
}

function showCelebration(sectionName) {
  var container = document.getElementById('toastContainer');
  if (!container) return;

  var toast = document.createElement('div');
  toast.className = 'toast celebration';
  toast.innerHTML = '<i class="material-icons">emoji_events</i> ' + sectionName + ' completado!';
  container.appendChild(toast);

  setTimeout(function () {
    if (toast.parentNode) toast.parentNode.removeChild(toast);
  }, 3500);
}

/**
 * Fetch and display code for a topic
 */
function loadTopicCode(topicName, contentEl) {
  var src = topicPaths[topicName];

  if (!src) {
    contentEl.textContent = 'Topic not found: ' + topicName;
    return;
  }

  contentEl.classList.add('loading');
  contentEl.innerHTML = '<div class="code-skeleton">'
    + '<div class="code-skeleton-line"></div>'
    + '<div class="code-skeleton-line"></div>'
    + '<div class="code-skeleton-line"></div>'
    + '<div class="code-skeleton-line"></div>'
    + '<div class="code-skeleton-line"></div>'
    + '</div>';

  if (codeCache[topicName]) {
    renderCode(contentEl, codeCache[topicName]);
    return;
  }

  fetch(src)
    .then(function (response) {
      if (!response.ok) {
        throw new Error('Failed to load ' + src + ' (' + response.status + ')');
      }
      return response.text();
    })
    .then(function (code) {
      codeCache[topicName] = code;
      renderCode(contentEl, code);
    })
    .catch(function (error) {
      contentEl.classList.remove('loading');
      contentEl.setAttribute('role', 'alert');
      contentEl.textContent = 'Error loading code: ' + error.message;
    });
}

/**
 * Render code with toolbar and syntax highlighting
 */
function renderCode(contentEl, code) {
  contentEl.classList.remove('loading');
  contentEl.innerHTML = '';

  // Copy button toolbar
  var toolbar = document.createElement('div');
  toolbar.className = 'code-toolbar';

  var copyBtn = document.createElement('button');
  copyBtn.className = 'code-toolbar-btn';
  copyBtn.innerHTML = '<i class="material-icons" style="font-size:16px">content_copy</i> Copy';
  copyBtn.addEventListener('click', function () {
    var highlightedLines = contentEl.querySelectorAll('.code-line.highlighted');
    if (highlightedLines.length > 0) {
      var selectedCode = Array.prototype.slice.call(highlightedLines).map(function (row) {
        var contentSpan = row.querySelector('.code-line-content');
        return contentSpan ? contentSpan.textContent : '';
      }).join('\n');
      navigator.clipboard.writeText(selectedCode).then(function () {
        copyBtn.innerHTML = '<i class="material-icons" style="font-size:16px">check</i> Copied!';
        showToast('check', highlightedLines.length + ' líneas copiadas');
        setTimeout(function () {
          copyBtn.innerHTML = '<i class="material-icons" style="font-size:16px">content_copy</i> Copy';
        }, 2000);
      });
    } else {
      navigator.clipboard.writeText(code).then(function () {
        copyBtn.innerHTML = '<i class="material-icons" style="font-size:16px">check</i> Copied!';
        showToast('check', 'Código copiado al portapapeles');
        setTimeout(function () {
          copyBtn.innerHTML = '<i class="material-icons" style="font-size:16px">content_copy</i> Copy';
        }, 2000);
      });
    }
  });

  // Wrap toggle button
  var wrapBtn = document.createElement('button');
  wrapBtn.className = 'code-toolbar-btn';
  wrapBtn.innerHTML = '<i class="material-icons" style="font-size:16px">wrap_text</i> Wrap';
  wrapBtn.addEventListener('click', function () {
    var codeLines = contentEl.querySelector('.code-lines');
    if (!codeLines) return;
    var isWrapped = codeLines.classList.toggle('wrapped');
    wrapBtn.innerHTML = isWrapped
      ? '<i class="material-icons" style="font-size:16px">wrap_text</i> Nowrap'
      : '<i class="material-icons" style="font-size:16px">wrap_text</i> Wrap';
  });

  // Share link button
  var shareBtn = document.createElement('button');
  shareBtn.className = 'code-toolbar-btn';
  shareBtn.innerHTML = '<i class="material-icons" style="font-size:16px">link</i> Share';
  shareBtn.addEventListener('click', function () {
    var topicEl = contentEl.closest('.section-topic');
    var topicName = topicEl ? topicEl.dataset.topic : '';
    var url = window.location.origin + window.location.pathname + '#' + topicName;
    navigator.clipboard.writeText(url).then(function () {
      shareBtn.innerHTML = '<i class="material-icons" style="font-size:16px">check</i> Copied!';
      showToast('link', 'Enlace copiado al portapapeles');
      setTimeout(function () {
        shareBtn.innerHTML = '<i class="material-icons" style="font-size:16px">link</i> Share';
      }, 2000);
    });
  });

  // Download .java file button
  var dlBtn = document.createElement('button');
  dlBtn.className = 'code-toolbar-btn';
  dlBtn.innerHTML = '<i class="material-icons" style="font-size:16px">download</i> .java';
  dlBtn.addEventListener('click', function () {
    var topicEl = contentEl.closest('.section-topic');
    var topicName = topicEl ? topicEl.dataset.topic : 'code';
    var fileName = topicPaths[topicName] ? topicPaths[topicName].split('/').pop() : topicName + '.java';
    var blob = new Blob([code], { type: 'text/x-java' });
    var url = URL.createObjectURL(blob);
    var a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    a.click();
    URL.revokeObjectURL(url);
    showToast('download', fileName + ' descargado');
  });

  // Prev/Next navigation buttons
  var allTopics = Array.prototype.slice.call(
    document.querySelectorAll('.section-topic:not(.filtered-out)')
  );
  var currentTopic = contentEl.closest('.section-topic');
  var currentIdx = allTopics.indexOf(currentTopic);

  var prevBtn = document.createElement('button');
  prevBtn.className = 'code-toolbar-btn code-nav-btn';
  prevBtn.innerHTML = '<i class="material-icons" style="font-size:16px">navigate_before</i> Prev';
  prevBtn.disabled = currentIdx <= 0;
  prevBtn.addEventListener('click', function () {
    if (currentIdx > 0) {
      var prevTopic = allTopics[currentIdx - 1];
      toggleTopic(currentTopic.querySelector('.section-topic-header'));
      var prevHeader = prevTopic.querySelector('.section-topic-header');
      if (prevHeader) toggleTopic(prevHeader);
    }
  });

  var nextBtn = document.createElement('button');
  nextBtn.className = 'code-toolbar-btn code-nav-btn';
  nextBtn.innerHTML = 'Next <i class="material-icons" style="font-size:16px">navigate_next</i>';
  nextBtn.disabled = currentIdx >= allTopics.length - 1;
  nextBtn.addEventListener('click', function () {
    if (currentIdx < allTopics.length - 1) {
      var nextTopic = allTopics[currentIdx + 1];
      toggleTopic(currentTopic.querySelector('.section-topic-header'));
      var nextHeader = nextTopic.querySelector('.section-topic-header');
      if (nextHeader) toggleTopic(nextHeader);
    }
  });

  // Notes button
  var notesBtn = document.createElement('button');
  notesBtn.className = 'code-toolbar-btn';
  var existingNote = TopicNotes.get(currentTopic ? currentTopic.dataset.topic : '');
  notesBtn.innerHTML = '<i class="material-icons" style="font-size:16px">'
    + (existingNote ? 'edit_note' : 'note_add') + '</i> Notes'
    + (existingNote ? '<span class="notes-indicator"></span>' : '');
  notesBtn.addEventListener('click', function () {
    if (currentTopic) {
      toggleNotesPanel(contentEl, currentTopic.dataset.topic);
    }
  });

  // Clear highlights button
  var clearHighlightBtn = document.createElement('button');
  clearHighlightBtn.className = 'code-toolbar-btn';
  clearHighlightBtn.innerHTML = '<i class="material-icons" style="font-size:16px">highlight_off</i> Clear';
  clearHighlightBtn.title = 'Limpiar líneas resaltadas';
  clearHighlightBtn.style.display = 'none';
  clearHighlightBtn.addEventListener('click', function () {
    contentEl.querySelectorAll('.code-line.highlighted').forEach(function (row) {
      row.classList.remove('highlighted');
    });
    clearHighlightBtn.style.display = 'none';
  });

  toolbar.appendChild(copyBtn);
  toolbar.appendChild(wrapBtn);
  toolbar.appendChild(shareBtn);
  toolbar.appendChild(dlBtn);
  toolbar.appendChild(notesBtn);
  toolbar.appendChild(clearHighlightBtn);

  // Font size zoom controls
  var zoomOutBtn = document.createElement('button');
  zoomOutBtn.className = 'code-toolbar-btn code-zoom-btn';
  zoomOutBtn.innerHTML = '<i class="material-icons" style="font-size:16px">remove</i>';
  zoomOutBtn.title = 'Reducir tamaño de fuente';
  zoomOutBtn.disabled = CodeFontSize.isMin();

  var zoomLabel = document.createElement('span');
  zoomLabel.className = 'code-zoom-label';
  var currentPct = Math.round((CodeFontSize.getSize() / CodeFontSize.DEFAULT_SIZE) * 100);
  zoomLabel.textContent = currentPct + '%';
  if (CodeFontSize.isDefault()) zoomLabel.style.opacity = '0.5';

  var zoomInBtn = document.createElement('button');
  zoomInBtn.className = 'code-toolbar-btn code-zoom-btn';
  zoomInBtn.innerHTML = '<i class="material-icons" style="font-size:16px">add</i>';
  zoomInBtn.title = 'Aumentar tamaño de fuente';
  zoomInBtn.disabled = CodeFontSize.isMax();

  function applyZoomToAll(size) {
    document.querySelectorAll('.section-topic-content').forEach(function (el) {
      el.style.fontSize = size + 'em';
    });
    var pct = Math.round((size / CodeFontSize.DEFAULT_SIZE) * 100);
    document.querySelectorAll('.code-zoom-label').forEach(function (lbl) {
      lbl.textContent = pct + '%';
      lbl.style.opacity = CodeFontSize.isDefault() ? '0.5' : '1';
    });
    document.querySelectorAll('.code-zoom-btn').forEach(function (b) {
      if (b.title.indexOf('Reducir') !== -1) b.disabled = CodeFontSize.isMin();
      if (b.title.indexOf('Aumentar') !== -1) b.disabled = CodeFontSize.isMax();
    });
  }

  zoomOutBtn.addEventListener('click', function () {
    applyZoomToAll(CodeFontSize.decrease());
  });

  zoomInBtn.addEventListener('click', function () {
    applyZoomToAll(CodeFontSize.increase());
  });

  zoomLabel.addEventListener('dblclick', function () {
    applyZoomToAll(CodeFontSize.reset());
    showToast('text_fields', 'Tamaño de fuente restaurado');
  });

  toolbar.appendChild(zoomOutBtn);
  toolbar.appendChild(zoomLabel);
  toolbar.appendChild(zoomInBtn);

  var navSpacer = document.createElement('span');
  navSpacer.className = 'code-toolbar-spacer';
  toolbar.appendChild(navSpacer);
  toolbar.appendChild(prevBtn);
  toolbar.appendChild(nextBtn);

  contentEl.appendChild(toolbar);

  // Render code lines with line numbers
  var lines = code.split('\n');
  var codeTable = document.createElement('div');
  codeTable.className = 'code-lines';

  var inBlockComment = false;

  lines.forEach(function (line, index) {
    var row = document.createElement('div');
    row.className = 'code-line';

    // Line number
    var num = document.createElement('span');
    num.className = 'code-line-num';
    num.textContent = index + 1;
    num.addEventListener('click', function () {
      row.classList.toggle('highlighted');
      var hasHighlights = contentEl.querySelector('.code-line.highlighted');
      if (clearHighlightBtn) clearHighlightBtn.style.display = hasHighlights ? '' : 'none';
    });
    row.appendChild(num);

    // Line content
    var content = document.createElement('span');
    content.className = 'code-line-content';

    if (inBlockComment) {
      // Inside a multi-line block comment
      var endIdx = line.indexOf('*/');
      if (endIdx !== -1) {
        inBlockComment = false;
        var commentText = line.substring(0, endIdx + 2);
        var restText = line.substring(endIdx + 2);
        if (restText.trim()) {
          content.innerHTML = '<span class="code-comment">' + escapeHtml(commentText) + '</span>' + highlightLine(restText);
        } else {
          content.className += ' code-comment';
          content.textContent = line;
        }
      } else {
        content.className += ' code-comment';
        content.textContent = line;
      }
    } else if (/^\/\/\/{3,}/.test(line.trim())) {
      content.className += ' code-separator';
      content.textContent = line;
    } else if (/^\s*\/\//.test(line)) {
      content.className += ' code-comment';
      content.textContent = line;
    } else {
      // Check for block comment start
      var bcStart = findBlockCommentStart(line);
      if (bcStart !== -1) {
        var bcEnd = line.indexOf('*/', bcStart + 2);
        if (bcEnd !== -1) {
          // Single-line block comment /* ... */ — highlightLine handles it
          content.innerHTML = highlightLine(line);
        } else {
          // Multi-line block comment starts here
          inBlockComment = true;
          var codeBefore = line.substring(0, bcStart);
          var commentAfter = line.substring(bcStart);
          if (codeBefore.trim()) {
            content.innerHTML = highlightLine(codeBefore) + '<span class="code-comment">' + escapeHtml(commentAfter) + '</span>';
          } else {
            content.className += ' code-comment';
            content.textContent = line;
          }
        }
      } else {
        content.innerHTML = highlightLine(line);
      }
    }

    row.appendChild(content);
    codeTable.appendChild(row);
  });

  contentEl.appendChild(codeTable);
  contentEl.dataset.loaded = 'true';

  // Apply saved code font size
  var savedFontSize = CodeFontSize.getSize();
  if (savedFontSize !== CodeFontSize.DEFAULT_SIZE) {
    contentEl.style.fontSize = savedFontSize + 'em';
  }

  // Add line count + reading time badges to the topic description
  var topicEl = contentEl.closest('.section-topic');
  if (topicEl && !topicEl.querySelector('.topic-lines-badge')) {
    var descEl = topicEl.querySelector('.section-topic-desc');
    if (descEl) {
      var linesBadge = document.createElement('span');
      linesBadge.className = 'topic-lines-badge';
      linesBadge.textContent = lines.length + ' lines';
      descEl.appendChild(document.createTextNode(' '));
      descEl.appendChild(linesBadge);

      var readMins = Math.max(1, Math.ceil(lines.length / 25));
      var timeBadge = document.createElement('span');
      timeBadge.className = 'topic-lines-badge topic-time-badge';
      timeBadge.innerHTML = '<i class="material-icons" style="font-size:12px;vertical-align:-2px">schedule</i> ~' + readMins + ' min';
      descEl.appendChild(document.createTextNode(' '));
      descEl.appendChild(timeBadge);
    }
  }

  // Related topics — "See also" section
  if (topicEl) {
    var currentTopicName = topicEl.dataset.topic;
    var relations = topicRelations[currentTopicName];
    if (relations && relations.length > 0) {
      var seeAlso = document.createElement('div');
      seeAlso.className = 'see-also';
      seeAlso.innerHTML = '<div class="see-also-header">'
        + '<i class="material-icons">link</i> See also</div>';

      var seeAlsoList = document.createElement('div');
      seeAlsoList.className = 'see-also-list';

      relations.forEach(function (rel) {
        var relTopicEl = document.querySelector('.section-topic[data-topic="' + rel.topic + '"]');
        if (!relTopicEl) return;

        var relTitle = relTopicEl.querySelector('.section-topic-title');
        var relIcon = relTopicEl.querySelector('.section-topic-icon');
        var titleTexts = [];
        relTitle.childNodes.forEach(function (n) {
          if (n.nodeType === 3 && n.textContent.trim()) titleTexts.push(n.textContent.trim());
        });
        var displayName = titleTexts.join(' ') || rel.topic;
        var iconName = relIcon ? relIcon.textContent : 'code';

        var chip = document.createElement('button');
        chip.className = 'see-also-chip';
        chip.title = rel.reason;
        chip.innerHTML = '<i class="material-icons">' + iconName + '</i>'
          + '<span class="see-also-chip-name">' + displayName + '</span>'
          + '<span class="see-also-chip-reason">' + rel.reason + '</span>';

        chip.addEventListener('click', function () {
          var currentHeader = topicEl.querySelector('.section-topic-header');
          if (currentHeader) toggleTopic(currentHeader);
          var relHeader = relTopicEl.querySelector('.section-topic-header');
          if (relHeader) toggleTopic(relHeader);
        });

        seeAlsoList.appendChild(chip);
      });

      seeAlso.appendChild(seeAlsoList);
      contentEl.appendChild(seeAlso);
    }
  }
}

/**
 * Toggle personal notes panel for a topic
 */
function toggleNotesPanel(contentEl, topicName) {
  var existing = contentEl.querySelector('.notes-panel');
  if (existing) {
    existing.parentNode.removeChild(existing);
    return;
  }

  var panel = document.createElement('div');
  panel.className = 'notes-panel';
  panel.innerHTML = '<div class="notes-panel-header">'
    + '<i class="material-icons">edit_note</i> Notas personales</div>';

  var textarea = document.createElement('textarea');
  textarea.className = 'notes-textarea';
  textarea.placeholder = 'Escribe tus notas sobre este topic...';
  textarea.value = TopicNotes.get(topicName);
  textarea.rows = 4;

  var saveTimer = null;
  textarea.addEventListener('input', function () {
    if (saveTimer) clearTimeout(saveTimer);
    saveTimer = setTimeout(function () {
      TopicNotes.set(topicName, textarea.value);
      // Update notes badge on topic card
      var topicEl = contentEl.closest('.section-topic');
      if (topicEl) {
        var badge = topicEl.querySelector('.topic-notes-badge');
        if (textarea.value.trim() && !badge) {
          var titleEl = topicEl.querySelector('.section-topic-title');
          var nb = document.createElement('i');
          nb.className = 'material-icons topic-notes-badge';
          nb.textContent = 'edit_note';
          nb.title = 'Tiene notas';
          titleEl.appendChild(nb);
        } else if (!textarea.value.trim() && badge) {
          badge.parentNode.removeChild(badge);
        }
      }
      showToast('save', 'Nota guardada');
    }, 800);
  });

  panel.appendChild(textarea);

  // Insert before .see-also if it exists, else at the end
  var seeAlso = contentEl.querySelector('.see-also');
  if (seeAlso) {
    contentEl.insertBefore(panel, seeAlso);
  } else {
    contentEl.appendChild(panel);
  }

  textarea.focus();
  textarea.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

/**
 * Escape HTML special characters
 */
function escapeHtml(text) {
  return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

/**
 * Find the start index of a block comment (/*) outside strings
 */
function findBlockCommentStart(line) {
  var inString = false;
  var stringChar = '';

  for (var i = 0; i < line.length - 1; i++) {
    var ch = line[i];
    var nx = line[i + 1];

    if (inString) {
      if (ch === '\\') { i++; continue; }
      if (ch === stringChar) { inString = false; }
    } else {
      if (ch === '"' || ch === "'") {
        inString = true;
        stringChar = ch;
      } else if (ch === '/' && nx === '/') {
        return -1; // line comment comes first
      } else if (ch === '/' && nx === '*') {
        return i;
      }
    }
  }
  return -1;
}

/**
 * Java syntax highlighting for a single line
 */
function highlightLine(line) {
  if (!line.trim()) return '\n';

  var html = escapeHtml(line);

  // Inline comments (//)
  var commentIndex = findCommentIndex(html);
  var codePart = html;
  var commentPart = '';

  if (commentIndex !== -1) {
    codePart = html.substring(0, commentIndex);
    commentPart = '<span class="code-comment">' + html.substring(commentIndex) + '</span>';
  }

  // Single-line block comments (/* ... */)
  codePart = codePart.replace(
    /\/\*[\s\S]*?\*\//g,
    '<span class="code-comment">$&</span>'
  );

  // Strings
  codePart = codePart.replace(
    /("(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')/g,
    '<span class="code-string">$1</span>'
  );

  // Annotations
  codePart = codePart.replace(
    /@\w+/g,
    '<span class="code-annotation">$&</span>'
  );

  // Numbers
  codePart = codePart.replace(
    /\b(\d+\.?\d*[dDfFlL]?)\b/g,
    '<span class="code-number">$1</span>'
  );

  // Java keywords
  codePart = codePart.replace(
    /\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|record|return|sealed|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|var|void|volatile|while|yield|true|false|null|permits|non-sealed)\b/g,
    '<span class="code-keyword">$1</span>'
  );

  // Common types (after keywords to avoid conflicts)
  codePart = codePart.replace(
    /\b(String|Integer|Double|Float|Long|Boolean|Character|Byte|Short|Object|System|Math|List|Map|Set|Queue|Deque|ArrayList|LinkedList|HashMap|HashSet|TreeMap|TreeSet|LinkedHashMap|PriorityQueue|Stack|Properties|Optional|Stream|Thread|Future|CompletableFuture|ExecutorService|Executors|AtomicInteger|ReentrantLock|Semaphore|Path|Files|File|BufferedReader|BufferedWriter|InputStream|OutputStream|Scanner|Arrays|Collections|StringBuilder|Comparator|Iterator|Exception|RuntimeException|ArithmeticException|IllegalArgumentException|IllegalStateException|NullPointerException|IOException|SQLException|InvocationTargetException|Runnable|Callable|Method|Field|Constructor|Class|Annotation|RetentionPolicy|ElementType|Connection|Statement|PreparedStatement|ResultSet|DriverManager|Pattern|Matcher|Duration|Instant|LocalDate|LocalTime|LocalDateTime|DateTimeFormatter|Random|UUID)\b/g,
    '<span class="code-type">$&</span>'
  );

  return codePart + commentPart;
}

/**
 * Find the index of an inline comment, ignoring comments inside strings
 */
function findCommentIndex(line) {
  var inString = false;
  var stringChar = '';

  for (var i = 0; i < line.length - 1; i++) {
    var char = line[i];
    var next = line[i + 1];

    if (inString) {
      if (char === '\\') {
        i++;
        continue;
      }
      if (char === stringChar) {
        inString = false;
      }
    } else {
      if (char === '"' || char === "'") {
        inString = true;
        stringChar = char;
      } else if (char === '/' && next === '/') {
        return i;
      }
    }
  }
  return -1;
}

// ============================================================
// SEARCH FILTER
// ============================================================

/**
 * Highlight matching text in an element, preserving child nodes (icons, badges)
 */
function highlightMatch(element, query) {
  // Find all text nodes to work with
  if (!element) return;
  if (!element.dataset.originalHtml) {
    element.dataset.originalHtml = element.innerHTML;
  }

  if (!query) {
    element.innerHTML = element.dataset.originalHtml;
    return;
  }

  // Restore original first to avoid stacking highlights
  element.innerHTML = element.dataset.originalHtml;

  var escaped = query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  var regex = new RegExp('(' + escaped + ')', 'gi');

  // Walk text nodes only
  var walker = document.createTreeWalker(element, NodeFilter.SHOW_TEXT, null, false);
  var textNodes = [];
  var node;
  while (node = walker.nextNode()) {
    if (regex.test(node.textContent)) {
      textNodes.push(node);
    }
    regex.lastIndex = 0;
  }

  textNodes.forEach(function (tn) {
    var parts = tn.textContent.split(regex);
    if (parts.length <= 1) return;
    var frag = document.createDocumentFragment();
    parts.forEach(function (part) {
      if (regex.test(part)) {
        var mark = document.createElement('mark');
        mark.className = 'search-highlight';
        mark.textContent = part;
        frag.appendChild(mark);
      } else {
        frag.appendChild(document.createTextNode(part));
      }
      regex.lastIndex = 0;
    });
    tn.parentNode.replaceChild(frag, tn);
  });
}

function search() {
  smoothScrollToTop();
  var query = document.getElementById("query").value;
  var banner = document.getElementById("banner");
  var clearBtn = document.getElementById("searchClearBtn");

  if (query !== "") {
    banner.style.height = "0px";
    if (clearBtn) clearBtn.classList.add('visible');
  } else {
    banner.style.height = "";
    if (clearBtn) clearBtn.classList.remove('visible');
  }

  filterTopics(query);

  // Save to search history
  if (query.trim().length >= 2) {
    SearchHistory.addEntry(query.trim());
  }
}

function clearSearch() {
  var input = document.getElementById("query");
  input.value = "";
  input.focus();
  search();
}

function filterTopics(query) {
  var normalizedQuery = query.toLowerCase().trim();
  var sections = document.querySelectorAll('.section');
  var totalVisible = 0;
  var totalTopics = 0;
  var favs = favFilterActive ? getFavorites() : null;
  var catFilter = typeof activeCategory !== 'undefined' ? activeCategory : 'all';

  sections.forEach(function (section) {
    var sectionCat = section.dataset.category || '';
    var topics = section.querySelectorAll('.section-topic');
    var visibleTopics = 0;

    topics.forEach(function (topic) {
      totalTopics++;
      var titleEl = topic.querySelector('.section-topic-title');
      var descEl = topic.querySelector('.section-topic-desc');
      var title = titleEl.textContent.toLowerCase();
      var desc = descEl.textContent.toLowerCase();
      var topicName = topic.dataset.topic.toLowerCase().replace(/_/g, ' ');

      var matchesText = !normalizedQuery ||
        title.includes(normalizedQuery) ||
        desc.includes(normalizedQuery) ||
        topicName.includes(normalizedQuery);

      var matchesFav = !favs || favs.indexOf(topic.dataset.topic) !== -1;
      var matchesCat = catFilter === 'all' || sectionCat === catFilter;

      if (matchesText && matchesFav && matchesCat) {
        topic.classList.remove('filtered-out');
        highlightMatch(titleEl, normalizedQuery);
        highlightMatch(descEl, normalizedQuery);
        visibleTopics++;
        totalVisible++;
      } else {
        topic.classList.add('filtered-out');
        highlightMatch(titleEl, '');
        highlightMatch(descEl, '');
      }
    });

    if (visibleTopics === 0 && (normalizedQuery || favs || catFilter !== 'all')) {
      section.classList.add('filtered-out');
    } else {
      section.classList.remove('filtered-out');
    }
  });

  // Update search results count
  var countEl = document.getElementById('searchResultsCount');
  if (countEl) {
    countEl.textContent = normalizedQuery
      ? totalVisible + ' de ' + totalTopics + ' resultados'
      : '';
  }

  // Show/hide empty state
  var emptyEl = document.getElementById('searchEmpty');
  if (emptyEl) {
    if (normalizedQuery && totalVisible === 0) {
      emptyEl.classList.add('visible');
    } else {
      emptyEl.classList.remove('visible');
    }
  }
}

// ============================================================
// BACK TO TOP BUTTON
// ============================================================

(function () {
  var backToTop = document.getElementById('backToTop');
  var scrollProgress = document.getElementById('scrollProgress');
  var nav = document.querySelector('.search-box-form-container');

  window.addEventListener('scroll', function () {
    var y = window.scrollY;

    // Back to top visibility
    if (backToTop) {
      if (y > 300) {
        backToTop.classList.add('visible');
      } else {
        backToTop.classList.remove('visible');
      }
    }

    // Scroll progress bar
    if (scrollProgress) {
      var docHeight = document.documentElement.scrollHeight - window.innerHeight;
      var pct = docHeight > 0 ? (y / docHeight) * 100 : 0;
      scrollProgress.style.width = pct + '%';
    }

    // Compact nav — hide cubes when scrolled past banner
    if (nav) {
      if (y > 200) {
        nav.classList.add('compact');
      } else {
        nav.classList.remove('compact');
      }
    }

    // Scroll spy — highlight active section cube
    var sections = document.querySelectorAll('.section');
    var cubes = document.querySelectorAll('.cube');
    var activeIdx = -1;
    var offset = 150;

    sections.forEach(function (section, i) {
      var rect = section.getBoundingClientRect();
      if (rect.top <= offset && rect.bottom > offset) {
        activeIdx = i;
      }
    });

    cubes.forEach(function (cube, i) {
      if (i === activeIdx) {
        cube.classList.add('active');
      } else {
        cube.classList.remove('active');
      }
    });
  }, { passive: true });
})();

// ============================================================
// TOPIC COUNTS
// ============================================================

(function () {
  document.querySelectorAll('.section').forEach(function (section) {
    var topics = section.querySelectorAll('.section-topic');
    var count = topics.length;
    var title = section.querySelector('.section-title');
    if (!title || count === 0) return;

    // Topic count badge
    var badge = document.createElement('span');
    badge.className = 'section-title-count';
    badge.textContent = '(' + count + ' topics)';
    title.appendChild(badge);

    // Progress counter
    var progress = document.createElement('span');
    progress.className = 'section-progress';
    progress.textContent = '0/' + count;
    title.appendChild(progress);

    // Expand/collapse all button
    var toggleBtn = document.createElement('button');
    toggleBtn.className = 'section-toggle-btn';
    toggleBtn.innerHTML = '<i class="material-icons" style="font-size:18px">unfold_more</i>';
    toggleBtn.title = 'Expandir / Colapsar todo';
    toggleBtn.addEventListener('click', function () {
      var openTopics = section.querySelectorAll('.section-topic.open');
      var allOpen = openTopics.length === count;

      topics.forEach(function (topic) {
        var header = topic.querySelector('.section-topic-header');
        var contentEl = topic.querySelector('.section-topic-content');

        if (allOpen) {
          topic.classList.remove('open');
          if (header) header.setAttribute('aria-expanded', 'false');
        } else {
          topic.classList.add('open');
          if (header) header.setAttribute('aria-expanded', 'true');
          if (contentEl && !contentEl.dataset.loaded) {
            loadTopicCode(topic.dataset.topic, contentEl);
          }
          markTopicRead(topic.dataset.topic);
        }
      });

      toggleBtn.querySelector('i').textContent = allOpen ? 'unfold_more' : 'unfold_less';
    });

    title.appendChild(toggleBtn);
  });
})();

// ============================================================
// RESTORE READ PROGRESS
// ============================================================

(function () {
  var read = getReadTopics();
  read.forEach(function (topicName) {
    updateReadBadge(topicName);
  });

  // Pre-mark already-complete sections so they don't trigger celebration on load
  document.querySelectorAll('.section').forEach(function (section) {
    var topics = section.querySelectorAll('.section-topic');
    var total = topics.length;
    var readCount = 0;
    topics.forEach(function (t) {
      if (read.indexOf(t.dataset.topic) !== -1) readCount++;
    });
    if (readCount === total) {
      completedSections[section.id] = true;
    }
  });

  updateSectionProgress();
})();

// ============================================================
// KEYBOARD SHORTCUTS
// ============================================================

document.addEventListener('keydown', function (event) {
  var isInput = document.activeElement.tagName === 'INPUT' || document.activeElement.tagName === 'TEXTAREA';

  if (event.key === 'Escape') {
    // Close shortcuts modal if open
    if (window._shortcutsModal && window._shortcutsModal.isOpen()) {
      window._shortcutsModal.close();
      return;
    }
    document.querySelectorAll('.section-topic.open').forEach(function (topic) {
      topic.classList.remove('open');
      var h = topic.querySelector('.section-topic-header');
      if (h) h.setAttribute('aria-expanded', 'false');
    });
    history.replaceState(null, '', window.location.pathname);
  }

  if ((event.ctrlKey && event.key === 'k') || (event.key === '/' && !isInput)) {
    event.preventDefault();
    document.getElementById('query').focus();
  }

  // ? — toggle keyboard shortcuts modal
  if (event.key === '?' && !isInput) {
    if (window._shortcutsModal) {
      if (window._shortcutsModal.isOpen()) {
        window._shortcutsModal.close();
      } else {
        window._shortcutsModal.open();
      }
    }
    return;
  }

  // J/K navigation between topics
  if (!isInput && (event.key === 'j' || event.key === 'k')) {
    var allTopics = Array.prototype.slice.call(
      document.querySelectorAll('.section-topic:not(.filtered-out)')
    );
    if (allTopics.length === 0) return;

    var openTopics = document.querySelectorAll('.section-topic.open');
    var currentIdx = -1;

    if (openTopics.length > 0) {
      var lastOpen = openTopics[openTopics.length - 1];
      currentIdx = allTopics.indexOf(lastOpen);
    }

    var nextIdx;
    if (event.key === 'j') {
      nextIdx = currentIdx < allTopics.length - 1 ? currentIdx + 1 : 0;
    } else {
      nextIdx = currentIdx > 0 ? currentIdx - 1 : allTopics.length - 1;
    }

    // Close all open topics
    openTopics.forEach(function (t) {
      t.classList.remove('open');
      var h = t.querySelector('.section-topic-header');
      if (h) h.setAttribute('aria-expanded', 'false');
    });

    // Open the target topic
    var target = allTopics[nextIdx];
    var header = target.querySelector('.section-topic-header');
    if (header) toggleTopic(header);
  }
});

// ============================================================
// SHORTCUTS MODAL — close button, overlay click, focus trap
// ============================================================

(function () {
  var overlay = document.getElementById('shortcutsOverlay');
  var closeBtn = document.getElementById('shortcutsClose');
  if (!overlay) return;

  var previousFocus = null;

  function openModal() {
    previousFocus = document.activeElement;
    overlay.classList.add('visible');
    if (closeBtn) closeBtn.focus();
  }

  function closeModal() {
    overlay.classList.remove('visible');
    if (previousFocus && previousFocus.focus) previousFocus.focus();
  }

  if (closeBtn) {
    closeBtn.addEventListener('click', closeModal);
  }

  overlay.addEventListener('click', function (e) {
    if (e.target === overlay) closeModal();
  });

  // Focus trap: Tab/Shift+Tab cycle within modal
  overlay.addEventListener('keydown', function (e) {
    if (e.key !== 'Tab') return;
    var focusable = overlay.querySelectorAll('button, [href], [tabindex]:not([tabindex="-1"])');
    if (focusable.length === 0) return;

    var first = focusable[0];
    var last = focusable[focusable.length - 1];

    if (e.shiftKey) {
      if (document.activeElement === first) {
        e.preventDefault();
        last.focus();
      }
    } else {
      if (document.activeElement === last) {
        e.preventDefault();
        first.focus();
      }
    }
  });

  // Expose open/close for keyboard handler
  window._shortcutsModal = { open: openModal, close: closeModal, isOpen: function () {
    return overlay.classList.contains('visible');
  }};
})();

// ============================================================
// THEME TOGGLE (dark / light)
// ============================================================

(function () {
  var toggle = document.getElementById('themeToggle');
  var icon = document.getElementById('themeIcon');
  if (!toggle) return;

  // Determine initial theme: localStorage > system preference > light
  var saved = localStorage.getItem('sjb-theme');
  var prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  var theme = saved || (prefersDark ? 'dark' : 'light');

  applyTheme(theme);

  toggle.addEventListener('click', function () {
    var current = document.documentElement.getAttribute('data-theme') || 'light';
    var next = current === 'dark' ? 'light' : 'dark';
    applyTheme(next);
    localStorage.setItem('sjb-theme', next);
  });

  function applyTheme(t) {
    document.documentElement.setAttribute('data-theme', t);
    icon.textContent = t === 'dark' ? 'light_mode' : 'dark_mode';
  }
})();

// ============================================================
// RESET PROGRESS
// ============================================================

(function () {
  var btn = document.getElementById('resetProgressBtn');
  if (!btn) return;

  btn.addEventListener('click', function () {
    var read = getReadTopics();
    if (read.length === 0) {
      showToast('info', 'No hay progreso que reiniciar');
      return;
    }

    if (!confirm('¿Reiniciar el progreso de lectura? (' + read.length + ' topics leídos)')) return;

    localStorage.removeItem('sjb-read-topics');

    // Remove all read badges
    document.querySelectorAll('.topic-read-badge').forEach(function (b) {
      b.parentNode.removeChild(b);
    });

    // Reset progress counters
    updateSectionProgress();

    showToast('restart_alt', 'Progreso reiniciado');
  });
})();

// ============================================================
// QUICK-JUMP DROPDOWN (table of contents)
// ============================================================

(function () {
  var toggleBtn = document.getElementById('quickjumpToggle');
  var dropdown = document.getElementById('quickjumpDropdown');
  if (!toggleBtn || !dropdown) return;

  // Build dropdown content from DOM
  function buildDropdown() {
    dropdown.innerHTML = '';
    var read = getReadTopics();

    document.querySelectorAll('.section').forEach(function (section) {
      var titleEl = section.querySelector('.section-title');
      var sectionLabel = titleEl ? titleEl.childNodes[0].textContent.trim() : '';

      var sectionHeader = document.createElement('div');
      sectionHeader.className = 'quickjump-section';
      sectionHeader.textContent = sectionLabel;
      dropdown.appendChild(sectionHeader);

      section.querySelectorAll('.section-topic').forEach(function (topic) {
        var topicTitle = topic.querySelector('.section-topic-title');
        var iconEl = topic.querySelector('.section-topic-icon');
        var iconName = iconEl ? iconEl.textContent : 'code';
        var name = topicTitle ? topicTitle.childNodes[topicTitle.childNodes.length - 1].textContent.trim() : topic.dataset.topic;
        // Clean: remove any badge text from the title
        var titleTexts = [];
        topicTitle.childNodes.forEach(function (n) {
          if (n.nodeType === 3 && n.textContent.trim()) titleTexts.push(n.textContent.trim());
        });
        if (titleTexts.length > 0) name = titleTexts.join(' ');

        var item = document.createElement('button');
        item.className = 'quickjump-item';
        item.innerHTML = '<i class="material-icons">' + iconName + '</i>'
          + '<span class="quickjump-item-name">' + name + '</span>'
          + (read.indexOf(topic.dataset.topic) !== -1
            ? '<i class="material-icons topic-read-check">check_circle</i>'
            : '');

        item.addEventListener('click', function () {
          dropdown.classList.remove('visible');
          // Close any open topics first
          document.querySelectorAll('.section-topic.open').forEach(function (t) {
            t.classList.remove('open');
            var h = t.querySelector('.section-topic-header');
            if (h) h.setAttribute('aria-expanded', 'false');
          });
          // Open this topic
          var header = topic.querySelector('.section-topic-header');
          if (header) toggleTopic(header);
        });

        dropdown.appendChild(item);
      });
    });
  }

  toggleBtn.addEventListener('click', function () {
    var isVisible = dropdown.classList.contains('visible');
    if (!isVisible) {
      buildDropdown(); // Refresh every time it opens
    }
    dropdown.classList.toggle('visible');
  });

  // Close on click outside
  document.addEventListener('click', function (e) {
    if (!toggleBtn.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.classList.remove('visible');
    }
  });
})();

// ============================================================
// CATEGORY CHIPS — filter by difficulty level
// ============================================================

(function () {
  var chips = document.querySelectorAll('.category-chip');
  chips.forEach(function (chip) {
    chip.addEventListener('click', function () {
      chips.forEach(function (c) { c.classList.remove('active'); });
      chip.classList.add('active');
      activeCategory = chip.dataset.cat;
      FilterState.save(activeCategory, favFilterActive);
      filterTopics(document.getElementById('query').value);
    });
  });
})();

// ============================================================
// FAVORITES — star buttons & filter
// ============================================================

(function () {
  var favs = getFavorites();

  // Add star button to each topic header
  document.querySelectorAll('.section-topic').forEach(function (topic) {
    var header = topic.querySelector('.section-topic-header');
    var arrow = header.querySelector('.section-topic-arrow');
    if (!header || !arrow) return;

    var starBtn = document.createElement('button');
    starBtn.className = 'topic-fav-btn';
    starBtn.title = 'Añadir a favoritos';
    var isFav = favs.indexOf(topic.dataset.topic) !== -1;
    starBtn.innerHTML = '<i class="material-icons">' + (isFav ? 'star' : 'star_border') + '</i>';
    if (isFav) starBtn.classList.add('active');

    starBtn.addEventListener('click', function (e) {
      e.stopPropagation();
      var added = toggleFavorite(topic.dataset.topic);
      starBtn.innerHTML = '<i class="material-icons">' + (added ? 'star' : 'star_border') + '</i>';
      if (added) {
        starBtn.classList.add('active');
        showToast('star', 'Añadido a favoritos');
      } else {
        starBtn.classList.remove('active');
        showToast('star_border', 'Eliminado de favoritos');
        // Re-filter if favorites filter is active
        if (favFilterActive) {
          filterTopics(document.getElementById('query').value);
        }
      }
    });

    header.insertBefore(starBtn, arrow);
  });

  // Favorites filter pill
  var favBtn = document.getElementById('favFilterBtn');
  if (favBtn) {
    favBtn.addEventListener('click', function () {
      favFilterActive = !favFilterActive;
      favBtn.classList.toggle('active', favFilterActive);
      FilterState.save(activeCategory, favFilterActive);
      filterTopics(document.getElementById('query').value);
    });
  }

  updateFavCount();
})();

// ============================================================
// RESTORE FILTER STATE
// ============================================================

(function () {
  var state = FilterState.get();

  if (state.category && state.category !== 'all') {
    var chips = document.querySelectorAll('.category-chip');
    chips.forEach(function (c) { c.classList.remove('active'); });
    var target = document.querySelector('.category-chip[data-cat="' + state.category + '"]');
    if (target) {
      target.classList.add('active');
      activeCategory = state.category;
    }
  }

  if (state.favActive) {
    favFilterActive = true;
    var favBtn = document.getElementById('favFilterBtn');
    if (favBtn) favBtn.classList.add('active');
  }

  if (state.category !== 'all' || state.favActive) {
    filterTopics(document.getElementById('query').value);
  }
})();

// ============================================================
// PREFETCH CODE ON TOPIC HOVER
// ============================================================

(function () {
  document.querySelectorAll('.section-topic-header').forEach(function (header) {
    header.addEventListener('mouseenter', function () {
      var topicEl = header.closest('.section-topic');
      if (!topicEl) return;
      var topicName = topicEl.dataset.topic;
      var src = topicPaths[topicName];
      if (!src || codeCache[topicName]) return;

      fetch(src).then(function (response) {
        if (response.ok) return response.text();
      }).then(function (code) {
        if (code) codeCache[topicName] = code;
      }).catch(function () {});
    }, { passive: true });
  });
})();

// ============================================================
// RANDOM TOPIC (SHUFFLE) BUTTON
// ============================================================

(function () {
  var shuffleBtn = document.getElementById('shuffleBtn');
  if (!shuffleBtn) return;

  function openRandomTopic() {
    var allTopics = Array.prototype.slice.call(
      document.querySelectorAll('.section-topic:not(.filtered-out)')
    );
    if (allTopics.length === 0) return;

    var read = getReadTopics();
    var unread = allTopics.filter(function (t) {
      return read.indexOf(t.dataset.topic) === -1;
    });

    var pool = unread.length > 0 ? unread : allTopics;
    var randomEl = pool[Math.floor(Math.random() * pool.length)];

    document.querySelectorAll('.section-topic.open').forEach(function (t) {
      t.classList.remove('open');
      var h = t.querySelector('.section-topic-header');
      if (h) h.setAttribute('aria-expanded', 'false');
    });

    var header = randomEl.querySelector('.section-topic-header');
    if (header) toggleTopic(header);

    showToast('shuffle', 'Topic aleatorio');
  }

  shuffleBtn.addEventListener('click', function (e) {
    e.preventDefault();
    openRandomTopic();
  });

  document.addEventListener('keydown', function (e) {
    if (e.ctrlKey && e.shiftKey && e.key === 'R') {
      e.preventDefault();
      openRandomTopic();
    }
  });
})();

// ============================================================
// STATS DASHBOARD
// ============================================================

var StatsDashboard = (function () {
  var panel = document.getElementById('statsPanel');
  var grid = document.getElementById('statsGrid');
  var btn = document.getElementById('statsToggleBtn');

  if (!panel || !grid || !btn) return { refresh: function () {} };

  btn.addEventListener('click', function () {
    var isVisible = panel.classList.contains('visible');
    if (!isVisible) refresh();
    panel.classList.toggle('visible');
  });

  function refresh() {
    grid.innerHTML = '';
    var read = getReadTopics();
    var favs = getFavorites();
    var notesCount = TopicNotes.count();
    var totalTopics = Object.keys(topicPaths).length;

    var summaryData = [
      { value: read.length + '/' + totalTopics, label: 'Topics leídos' },
      { value: Math.round((read.length / totalTopics) * 100) + '%', label: 'Completado' },
      { value: String(favs.length), label: 'Favoritos' },
      { value: String(notesCount), label: 'Notas' }
    ];

    summaryData.forEach(function (item) {
      var card = document.createElement('div');
      card.className = 'stats-card';
      card.innerHTML = '<div class="stats-card-value">' + item.value + '</div>'
        + '<div class="stats-card-label">' + item.label + '</div>';
      grid.appendChild(card);
    });

    var categories = ['basics', 'good', 'pro', 'geek'];
    categories.forEach(function (cat) {
      var section = document.querySelector('.section[data-category="' + cat + '"]');
      if (!section) return;
      var topics = section.querySelectorAll('.section-topic');
      var total = topics.length;
      var readCount = 0;
      topics.forEach(function (t) {
        if (read.indexOf(t.dataset.topic) !== -1) readCount++;
      });
      var pct = total > 0 ? Math.round((readCount / total) * 100) : 0;

      var bar = document.createElement('div');
      bar.className = 'stats-category-bar';
      bar.innerHTML = '<span class="stats-category-name">' + cat.charAt(0).toUpperCase() + cat.slice(1) + '</span>'
        + '<div class="stats-bar-track"><div class="stats-bar-fill' + (pct === 100 ? ' complete' : '') + '" style="width:' + pct + '%"></div></div>'
        + '<span class="stats-category-pct">' + pct + '%</span>';
      grid.appendChild(bar);
    });
  }

  return { refresh: refresh };
})();

// ============================================================
// TOPIC CARD ENTRANCE ANIMATION (IntersectionObserver)
// ============================================================

(function () {
  var cards = document.querySelectorAll('.section-topic');
  if (!('IntersectionObserver' in window)) {
    // Fallback: show all cards immediately
    cards.forEach(function (c) { c.classList.add('visible'); });
    return;
  }

  var observer = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.05, rootMargin: '0px 0px -30px 0px' });

  cards.forEach(function (card, i) {
    card.style.transitionDelay = (i % 6) * 0.04 + 's';
    observer.observe(card);
  });
})();

// ============================================================
// SECTION TITLE HIGHLIGHT ON SCROLL INTO VIEW
// ============================================================

(function () {
  var titles = document.querySelectorAll('.section-title');
  if (!('IntersectionObserver' in window) || titles.length === 0) return;

  var observer = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) {
        entry.target.classList.add('highlight');
        observer.unobserve(entry.target);
        setTimeout(function () {
          entry.target.classList.remove('highlight');
        }, 1200);
      }
    });
  }, { threshold: 0.8, rootMargin: '0px 0px -60px 0px' });

  titles.forEach(function (title) {
    observer.observe(title);
  });
})();

// ============================================================
// SEARCH HISTORY DROPDOWN
// ============================================================

(function () {
  var input = document.getElementById('query');
  var dropdown = document.getElementById('searchHistoryDropdown');
  if (!input || !dropdown) return;

  function showHistory() {
    var history = SearchHistory.getHistory();
    if (history.length === 0 || input.value.trim()) {
      dropdown.classList.remove('visible');
      return;
    }
    dropdown.innerHTML = '<div class="search-history-header">'
      + '<span>Búsquedas recientes</span>'
      + '<button class="search-history-clear" id="clearHistoryBtn" title="Borrar historial">'
      + '<i class="material-icons" style="font-size:14px">delete_outline</i></button></div>';

    history.forEach(function (term) {
      var item = document.createElement('button');
      item.className = 'search-history-item';
      item.innerHTML = '<i class="material-icons" style="font-size:16px">history</i> ' + escapeHtml(term);
      item.addEventListener('click', function () {
        input.value = term;
        dropdown.classList.remove('visible');
        search();
      });
      dropdown.appendChild(item);
    });

    dropdown.querySelector('#clearHistoryBtn').addEventListener('click', function (e) {
      e.stopPropagation();
      SearchHistory.clearHistory();
      dropdown.classList.remove('visible');
      showToast('delete_outline', 'Historial de búsqueda eliminado');
    });

    dropdown.classList.add('visible');
  }

  input.addEventListener('focus', showHistory);
  input.addEventListener('input', function () {
    if (input.value.trim()) {
      dropdown.classList.remove('visible');
    } else {
      showHistory();
    }
  });

  document.addEventListener('click', function (e) {
    if (!input.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.classList.remove('visible');
    }
  });
})();

// ============================================================
// TOPIC NOTES BADGES — show badge on topics that have notes
// ============================================================

(function () {
  var notes = TopicNotes.getAll();
  Object.keys(notes).forEach(function (topicName) {
    var topicEl = document.querySelector('.section-topic[data-topic="' + topicName + '"]');
    if (!topicEl || topicEl.querySelector('.topic-notes-badge')) return;
    var titleEl = topicEl.querySelector('.section-topic-title');
    var badge = document.createElement('i');
    badge.className = 'material-icons topic-notes-badge';
    badge.textContent = 'edit_note';
    badge.title = 'Tiene notas';
    titleEl.appendChild(badge);
  });
})();

// ============================================================
// EXPORT / IMPORT PROGRESS DATA
// ============================================================

var DataPortability = (function () {
  function exportData() {
    var data = {
      version: 1,
      exportedAt: new Date().toISOString(),
      readTopics: getReadTopics(),
      favorites: getFavorites(),
      notes: TopicNotes.getAll(),
      searchHistory: SearchHistory.getHistory(),
      theme: localStorage.getItem('sjb-theme'),
      filterState: FilterState.get(),
      codeFontSize: localStorage.getItem('sjb-code-font-size')
    };

    var json = JSON.stringify(data, null, 2);
    var blob = new Blob([json], { type: 'application/json' });
    var url = URL.createObjectURL(blob);
    var a = document.createElement('a');
    a.href = url;
    a.download = 'sjb-progress-' + new Date().toISOString().split('T')[0] + '.json';
    a.click();
    URL.revokeObjectURL(url);
    showToast('download', 'Progreso exportado correctamente');
  }

  function importData() {
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = '.json';
    input.addEventListener('change', function () {
      var file = input.files[0];
      if (!file) return;

      var reader = new FileReader();
      reader.onload = function (e) {
        try {
          var data = JSON.parse(e.target.result);
          if (!data.version) throw new Error('Formato inválido');

          var existing = getReadTopics().length + getFavorites().length + TopicNotes.count();
          if (existing > 0 && !confirm('Tienes datos existentes. ¿Reemplazar con los importados?')) return;

          if (data.readTopics) localStorage.setItem('sjb-read-topics', JSON.stringify(data.readTopics));
          if (data.favorites) localStorage.setItem('sjb-favorites', JSON.stringify(data.favorites));
          if (data.notes) localStorage.setItem('sjb-topic-notes', JSON.stringify(data.notes));
          if (data.searchHistory) localStorage.setItem('sjb-search-history', JSON.stringify(data.searchHistory));
          if (data.theme) localStorage.setItem('sjb-theme', data.theme);
          if (data.filterState) localStorage.setItem('sjb-filter-state', JSON.stringify(data.filterState));
          if (data.codeFontSize) localStorage.setItem('sjb-code-font-size', data.codeFontSize);

          showToast('upload', 'Progreso importado — recargando...');
          setTimeout(function () { location.reload(); }, 1200);
        } catch (err) {
          showToast('error', 'Error al importar: ' + err.message);
        }
      };
      reader.readAsText(file);
    });
    input.click();
  }

  return { exportData: exportData, importData: importData };
})();

(function () {
  var exportBtn = document.getElementById('exportBtn');
  var importBtn = document.getElementById('importBtn');
  if (exportBtn) exportBtn.addEventListener('click', DataPortability.exportData);
  if (importBtn) importBtn.addEventListener('click', DataPortability.importData);
})();

// ============================================================
// DEEP LINKING — open topic from URL hash on page load
// ============================================================

(function () {
  var hash = window.location.hash.replace('#', '');
  if (!hash) return;

  var topicEl = document.querySelector('.section-topic[data-topic="' + hash + '"]');
  if (!topicEl) return;

  var header = topicEl.querySelector('.section-topic-header');
  var contentEl = topicEl.querySelector('.section-topic-content');

  topicEl.classList.add('open');
  if (header) header.setAttribute('aria-expanded', 'true');
  if (contentEl && !contentEl.dataset.loaded) {
    loadTopicCode(hash, contentEl);
  }
  markTopicRead(hash);

  // Scroll to the topic after a short delay for rendering
  setTimeout(function () {
    topicEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }, 150);
})();

// ============================================================
// RESUME SESSION — offer to continue where user left off
// ============================================================

(function () {
  // Don't show resume prompt if there's already a deep link hash
  if (window.location.hash) return;

  var session = ResumeSession.get();
  if (!session || !session.topic) return;

  // Only show if less than 7 days old
  if (Date.now() - session.timestamp > 7 * 24 * 60 * 60 * 1000) return;

  var topicEl = document.querySelector('.section-topic[data-topic="' + session.topic + '"]');
  if (!topicEl) return;

  var titleEl = topicEl.querySelector('.section-topic-title');
  var titleTexts = [];
  titleEl.childNodes.forEach(function (n) {
    if (n.nodeType === 3 && n.textContent.trim()) titleTexts.push(n.textContent.trim());
  });
  var displayName = titleTexts.join(' ') || session.topic;

  var container = document.getElementById('toastContainer');
  if (!container) return;

  var toast = document.createElement('div');
  toast.className = 'toast resume-toast';
  toast.innerHTML = '<i class="material-icons">history</i>'
    + '<span>Continuar con <strong>' + displayName + '</strong>?</span>'
    + '<button class="resume-toast-btn resume-yes">'
    + '<i class="material-icons" style="font-size:14px">arrow_forward</i> Sí</button>'
    + '<button class="resume-toast-btn resume-no">'
    + '<i class="material-icons" style="font-size:14px">close</i></button>';
  container.appendChild(toast);

  toast.querySelector('.resume-yes').addEventListener('click', function () {
    if (toast.parentNode) toast.parentNode.removeChild(toast);
    var header = topicEl.querySelector('.section-topic-header');
    if (header) toggleTopic(header);
  });

  toast.querySelector('.resume-no').addEventListener('click', function () {
    if (toast.parentNode) toast.parentNode.removeChild(toast);
    ResumeSession.clear();
  });

  // Auto-dismiss after 8 seconds
  setTimeout(function () {
    if (toast.parentNode) {
      toast.classList.add('fading');
      setTimeout(function () {
        if (toast.parentNode) toast.parentNode.removeChild(toast);
      }, 300);
    }
  }, 8000);
})();

// ============================================================
// BREADCRUMB BAR — shows current section + topic on scroll
// ============================================================

(function () {
  var breadcrumbBar = document.getElementById('breadcrumbBar');
  var breadcrumbSection = document.getElementById('breadcrumbSection');
  var breadcrumbTopic = document.getElementById('breadcrumbTopic');
  if (!breadcrumbBar) return;

  var currentVisible = null;

  var observer = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) {
        currentVisible = entry.target;
      } else if (entry.target === currentVisible) {
        currentVisible = null;
      }
    });
    updateBreadcrumb();
  }, { rootMargin: '-70px 0px -50% 0px', threshold: 0 });

  function updateBreadcrumb() {
    if (!currentVisible || !currentVisible.classList.contains('open')) {
      breadcrumbBar.classList.remove('visible');
      return;
    }
    var section = currentVisible.closest('.section');
    var sectionTitle = section ? section.querySelector('.section-title') : null;
    var topicTitle = currentVisible.querySelector('.section-topic-title');
    if (sectionTitle && topicTitle) {
      breadcrumbSection.textContent = sectionTitle.textContent.trim();
      breadcrumbTopic.textContent = topicTitle.textContent.trim();
      breadcrumbBar.classList.add('visible');
    } else {
      breadcrumbBar.classList.remove('visible');
    }
  }

  // Observe all topics
  document.querySelectorAll('.section-topic').forEach(function (topic) {
    observer.observe(topic);
  });

  // Re-check when topics open/close
  document.addEventListener('click', function () {
    setTimeout(updateBreadcrumb, 350);
  });
})();

// ============================================================
// ONLINE / OFFLINE INDICATOR
// ============================================================

(function () {
  window.addEventListener('online', function () {
    showToast('wifi', 'Conexión restaurada');
  });
  window.addEventListener('offline', function () {
    showToast('wifi_off', 'Sin conexión — modo offline');
  });
})();

// ============================================================
// SERVICE WORKER REGISTRATION
// ============================================================

if ('serviceWorker' in navigator) {
  window.addEventListener('load', function () {
    navigator.serviceWorker.register('./sw.js').catch(function () {});
  });
}
