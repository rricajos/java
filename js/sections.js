// ============================================================
// SECTIONS.JS - Topic navigation, code loading & search filter
// Adapted for Java module (loads .java files from subfolders)
// ============================================================

var codeCache = {};

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
    navigator.clipboard.writeText(code).then(function () {
      copyBtn.innerHTML = '<i class="material-icons" style="font-size:16px">check</i> Copied!';
      showToast('check', 'Código copiado al portapapeles');
      setTimeout(function () {
        copyBtn.innerHTML = '<i class="material-icons" style="font-size:16px">content_copy</i> Copy';
      }, 2000);
    });
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

  toolbar.appendChild(copyBtn);
  toolbar.appendChild(wrapBtn);
  toolbar.appendChild(shareBtn);
  toolbar.appendChild(dlBtn);

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
      var title = topic.querySelector('.section-topic-title').textContent.toLowerCase();
      var desc = topic.querySelector('.section-topic-desc').textContent.toLowerCase();
      var topicName = topic.dataset.topic.toLowerCase().replace(/_/g, ' ');

      var matchesText = !normalizedQuery ||
        title.includes(normalizedQuery) ||
        desc.includes(normalizedQuery) ||
        topicName.includes(normalizedQuery);

      var matchesFav = !favs || favs.indexOf(topic.dataset.topic) !== -1;
      var matchesCat = catFilter === 'all' || sectionCat === catFilter;

      if (matchesText && matchesFav && matchesCat) {
        topic.classList.remove('filtered-out');
        visibleTopics++;
        totalVisible++;
      } else {
        topic.classList.add('filtered-out');
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
      filterTopics(document.getElementById('query').value);
    });
  }

  updateFavCount();
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
