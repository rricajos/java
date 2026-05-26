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
  } else {
    topicEl.classList.add('open');

    if (!contentEl.dataset.loaded) {
      loadTopicCode(topicEl.dataset.topic, contentEl);
    }
  }
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
  contentEl.textContent = 'Loading...';

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

  toolbar.appendChild(copyBtn);
  contentEl.appendChild(toolbar);

  // Render code lines
  var lines = code.split('\n');
  var fragment = document.createDocumentFragment();

  lines.forEach(function (line) {
    var span = document.createElement('span');
    span.style.display = 'block';
    span.style.minHeight = '1.2em';

    if (/^\/\/\/{3,}/.test(line.trim())) {
      span.className = 'code-separator';
      span.textContent = line;
    } else if (/^\s*\/\//.test(line)) {
      span.className = 'code-comment';
      span.textContent = line;
    } else {
      span.innerHTML = highlightLine(line);
    }

    fragment.appendChild(span);
  });

  contentEl.appendChild(fragment);
  contentEl.dataset.loaded = 'true';
}

/**
 * Java syntax highlighting for a single line
 */
function highlightLine(line) {
  if (!line.trim()) return '\n';

  var html = line
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');

  // Inline comments
  var commentIndex = findCommentIndex(html);
  var codePart = html;
  var commentPart = '';

  if (commentIndex !== -1) {
    codePart = html.substring(0, commentIndex);
    commentPart = '<span class="code-comment">' + html.substring(commentIndex) + '</span>';
  }

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

  if (query !== "") {
    banner.style.height = "0px";
  } else {
    banner.style.height = "180px";
  }

  filterTopics(query);
}

function filterTopics(query) {
  var normalizedQuery = query.toLowerCase().trim();
  var sections = document.querySelectorAll('.section');

  sections.forEach(function (section) {
    var topics = section.querySelectorAll('.section-topic');
    var visibleTopics = 0;

    topics.forEach(function (topic) {
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
}

// ============================================================
// BACK TO TOP BUTTON
// ============================================================

(function () {
  var backToTop = document.getElementById('backToTop');
  if (!backToTop) return;

  window.addEventListener('scroll', function () {
    if (window.scrollY > 300) {
      backToTop.classList.add('visible');
    } else {
      backToTop.classList.remove('visible');
    }
  }, { passive: true });
})();

// ============================================================
// TOPIC COUNTS
// ============================================================

(function () {
  document.querySelectorAll('.section').forEach(function (section) {
    var count = section.querySelectorAll('.section-topic').length;
    var title = section.querySelector('.section-title');
    if (title && count > 0) {
      var badge = document.createElement('span');
      badge.className = 'section-title-count';
      badge.textContent = '(' + count + ' topics)';
      title.appendChild(badge);
    }
  });
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
