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
 * Invoked when the content of the search field changes
 */
function searchChanged(event) {
   if (searchTimeout) {
      window.clearTimeout(searchTimeout);
   }

   searchTimeout = window.setTimeout( function() {
      var toc = document.getElementById('toc');
      var searchfield = document.getElementById('searchfield');
      var search = searchfield.value;
      console.log("Search for " + search);
      openMatchingElements(new RegExp(search,'i'), toc, 0);

      if (search === '') {
         collapseAllUl(toc, 0);
      }
   }, 100);
}

function collapseAllUl(element, depth) {
   if (depth > 2 && element.tagName === "UL" && !isCollapsed(element)) {
      toggleElement(element);
   }

   var children = element.childNodes;
   for(var i=0; i < children.length; i++) {
      collapseAllUl(children[i], depth + 1);
   }
}

function openMatchingElements(search, element, depth) {
   if (element.nodeType==3 && element.nodeValue.match(search )) {
      return true;
   }

   if (depth > 2 && (element.tagName === "UL" || element.tagName === "LI") && !isCollapsed(element)) {
      toggleElement(element);
   }

   if (element.tagName === "A") {
      var originalValue = element.getAttribute("data-original");
      if (originalValue) {
         element.innerHTML = originalValue;
      }
      element.classList.remove('diff');
   }


   var found = false;
   var children = element.childNodes;
   for(var i=0; i < children.length; i++) {
      if (openMatchingElements(search, children[i], depth + 1)) {
         found = true;
      }
   }

   if (found) {
      if (isCollapsed(element)) {
         toggleElement(element);
      }
      if (element.tagName === "A") {
         element.setAttribute("data-original", element.innerHTML);
         element.innerHTML = highlightMatches(element.innerHTML, search);
      }
   }
   return found;
}

function highlightMatches(text, search) {

   var result = text.replace(search, "<span class='diff'>$&</span>");
   console.log("Replaced "+result);
   return result;
}
