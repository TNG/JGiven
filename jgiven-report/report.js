init();

function init() {
   if (document.location.search.indexOf('toc=0') != -1) {
      hideToc();
   }
}

function showToc() {
   toggle('toc');
   toggle('show-menu-icon');
   adaptHrefs(false);
}

function hideToc() {
   toggle('toc');
   toggle('show-menu-icon');
   adaptHrefs(true);
}

function adaptHrefs(hideToc) {
   var elements = document.getElementsByTagName("A");
   for (i = 0; i < elements.length; i++) {
      var href = elements[i].getAttribute('href');

      var newHref;
      if (hideToc) {
         newHref = href + "?toc=0";
      } else {
         newHref = href.replace("?toc=0","");
      }

      elements[i].setAttribute('href', newHref);
   }
}

/**
 * Invoked when the use clicks on the header of a collapsibel element
 * @param id
 */
function toggle(id) {
   var element = document.getElementById(id);
   toggleElement(element);
}

function toggleElement(element) {
   if (!element.classList) {
      return;
   }

   if (element.classList.toggle) {
      element.classList.toggle('collapsed');
   } else {
      if (element.style.display === 'block') {
          element.style.display = 'none';
      } else {
          element.style.display = 'block';
      }
   }
}

function isCollapsed(element) {
   if (!element.classList) {
      return false;
   }

   if (element.classList.toggle){
      return element.classList.contains('collapsed');
   }

   return element.style.display === 'none';
}

var searchTimeout;

/**
 * Invoked when the content of the content search field changes
 */
function contentSearchChanged(event) {
   if (searchTimeout) {
      window.clearTimeout(searchTimeout);
   }

   searchTimeout = window.setTimeout( function() {
      var toc = document.getElementById('content');
      var searchfield = document.getElementById('content-search-input');
      var search = searchfield.value;
      console.log("Search for " + search);
      openMatchingElements(new RegExp(search,'i'), toc, 2, contentElementsToToggle, elementsToBeSearched);

      if (search === '') {
         collapseAll(toc, 1, contentElementsToBeClosed);
      }
   }, 100);
}

function contentElementsToToggle(element) {
   return element.className === 'scenario-body'
      || element.className === 'scenario'
      || element.className === 'case-content';
}

function tocElementsToToggle(element) {
   return element.tagName === "UL" || element.tagName === "LI";
}

/**
 * Invoked when the content of the TOC search field changes
 */
function searchChanged(event) {
   if (searchTimeout) {
      window.clearTimeout(searchTimeout);
   }

   searchTimeout = window.setTimeout( function() {
      var toc = document.getElementById('toc');
      var searchfield = document.getElementById('toc-search-input');
      var search = searchfield.value;
      console.log("Search for " + search);
      openMatchingElements(new RegExp(search,'i'), toc, 0, tocElementsToToggle, elementsToBeSearched);

      if (search === '') {
         collapseAll(toc, 0, ulElement);
      }
   }, 100);
}

function elementsToBeSearched(element) {
   if (element.className) {
      return element.className.indexOf('extended-description') === -1;
   }
   return true;
}

function contentElementsToBeClosed(element) {
   return element.className === 'scenario-body'
      || element.className === 'case-content';
}

function ulElement(element) {
   return element.tagName === "UL";
}

function collapseAll(element, depth, toBeClosed) {
   if (depth > 2 && toBeClosed(element) && !isCollapsed(element)) {
      toggleElement(element);
   }

   var children = element.childNodes;
   for(var i=0; i < children.length; i++) {
      collapseAll(children[i], depth + 1, toBeClosed);
   }
}

function onlyHasTextNode(element) {
   return element.childNodes.length === 1
      && element.childNodes[0].nodeType === 3;
}

function openMatchingElements(search, element, depth, shouldToggleElement, shouldSearchElement) {
   if (!shouldSearchElement(element)) {
      return false;
   }

   if (element.nodeType==3 && element.nodeValue.match(search )) {
      return true;
   }

   // close element by default, in case it is currently visible
   if (depth > 2 && shouldToggleElement(element) && !isCollapsed(element)) {
      toggleElement(element);
   }

   if (element.getAttribute) {
      var originalValue = element.getAttribute("data-original");
      if (originalValue) {
         element.innerHTML = originalValue;
      }
   }

   var found = false;
   var children = element.childNodes;
   for(var i=0; i < children.length; i++) {
      if (openMatchingElements(search, children[i], depth + 1,shouldToggleElement, shouldSearchElement)) {
         found = true;
      }
   }

   if (found) {
      if (isCollapsed(element)) {
         toggleElement(element);
      }
      if (onlyHasTextNode(element)) {
         element.setAttribute("data-original", element.innerHTML);
         element.innerHTML = highlightMatches(element.innerHTML, search);
      }
   }
   return found;
}

function highlightMatches(text, search) {
   var result = text.replace(search, "<span class='highlight'>$&</span>");
   return result;
}

function showExtendedDescription(id) {
   toggle(id);
}
