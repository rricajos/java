var CACHE_NAME = 'sjb-v2';
var ASSETS = [
  './',
  './index.html',
  './css/main.css',
  './css/banner.css',
  './css/search-box.css',
  './css/cubes.css',
  './css/sections.css',
  './css/footer.css',
  './js/banner.js',
  './js/sections.js',
  './icons/favicon.svg',
  // Java topic files
  './basics/VariablesAndTypes.java',
  './basics/OperatorsArithmetic.java',
  './basics/OperatorsAssignment.java',
  './basics/OperatorsLogical.java',
  './basics/OperatorsConditional.java',
  './basics/ControlFlow.java',
  './basics/DateTimeAPI.java',
  './basics/PackagesAndAccess.java',
  './good/Methods.java',
  './good/Strings.java',
  './good/ArraysDemo.java',
  './good/OptionalDemo.java',
  './good/InnerClasses.java',
  './good/RegexDemo.java',
  './pro/OopClasses.java',
  './pro/OopInheritance.java',
  './pro/Exceptions.java',
  './pro/CollectionsFramework.java',
  './pro/Annotations.java',
  './pro/JDBC.java',
  './pro/JUnitDemo.java',
  './pro/DesignPatterns.java',
  './pro/BuildToolsDemo.java',
  './geek/Generics.java',
  './geek/LambdasAndFunctional.java',
  './geek/Streams.java',
  './geek/Concurrency.java',
  './geek/FileIO.java',
  './geek/Networking.java',
  './geek/Reflection.java',
  './geek/ModulesDemo.java'
];

self.addEventListener('install', function (event) {
  event.waitUntil(
    caches.open(CACHE_NAME).then(function (cache) {
      return cache.addAll(ASSETS);
    })
  );
  self.skipWaiting();
});

self.addEventListener('activate', function (event) {
  event.waitUntil(
    caches.keys().then(function (names) {
      return Promise.all(
        names.filter(function (name) { return name !== CACHE_NAME; })
          .map(function (name) { return caches.delete(name); })
      );
    })
  );
  self.clients.claim();
});

self.addEventListener('fetch', function (event) {
  event.respondWith(
    caches.match(event.request).then(function (cached) {
      if (cached) return cached;
      return fetch(event.request).then(function (response) {
        // Cache font requests for offline use
        if (response.ok && (
          event.request.url.indexOf('fonts.googleapis.com') !== -1 ||
          event.request.url.indexOf('fonts.gstatic.com') !== -1
        )) {
          var clone = response.clone();
          caches.open(CACHE_NAME).then(function (cache) {
            cache.put(event.request, clone);
          });
        }
        return response;
      });
    }).catch(function () {
      // Offline fallback for navigation
      if (event.request.mode === 'navigate') {
        return caches.match('./index.html');
      }
    })
  );
});
