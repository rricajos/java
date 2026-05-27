// ============================================================
// SECTIONS.JS - Topic navigation, code loading & search filter
// Adapted for Java module (loads .java files from subfolders)
// ============================================================

var codeCache = {};

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

function updateSectionProgress() {
  var read = getReadTopics();
  document.querySelectorAll('.section').forEach(function (section) {
    var topics = section.querySelectorAll('.section-topic');
    var total = topics.length;
    var readCount = 0;
    topics.forEach(function (t) {
      if (read.indexOf(t.dataset.topic) !== -1) readCount++;
    });

    var progressEl = section.querySelector('.section-progress');
    if (!progressEl) return;
    progressEl.textContent = readCount + '/' + total;
    if (readCount === total) {
      progressEl.classList.add('complete');
    } else {
      progressEl.classList.remove('complete');
    }
  });
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

  toolbar.appendChild(copyBtn);
  toolbar.appendChild(wrapBtn);
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

  sections.forEach(function (section) {
    var topics = section.querySelectorAll('.section-topic');
    var visibleTopics = 0;

    topics.forEach(function (topic) {
      totalTopics++;
      var title = topic.querySelector('.section-topic-title').textContent.toLowerCase();
      var desc = topic.querySelector('.section-topic-desc').textContent.toLowerCase();
      var topicName = topic.dataset.topic.toLowerCase().replace(/_/g, ' ');

      var matches = !normalizedQuery ||
        title.includes(normalizedQuery) ||
        desc.includes(normalizedQuery) ||
        topicName.includes(normalizedQuery);

      if (matches) {
        topic.classList.remove('filtered-out');
        visibleTopics++;
        totalVisible++;
      } else {
        topic.classList.add('filtered-out');
      }
    });

    if (visibleTopics === 0 && normalizedQuery) {
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
  updateSectionProgress();
})();

// ============================================================
// KEYBOARD SHORTCUTS
// ============================================================

document.addEventListener('keydown', function (event) {
  if (event.key === 'Escape') {
    document.querySelectorAll('.section-topic.open').forEach(function (topic) {
      topic.classList.remove('open');
    });
  }

  if ((event.ctrlKey && event.key === 'k') || (event.key === '/' && document.activeElement.tagName !== 'INPUT')) {
    event.preventDefault();
    document.getElementById('query').focus();
  }
});

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
