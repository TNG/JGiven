function toggle(id) {
   var element = document.getElementById(id);
   console.log(element.style.display);
   if (element.style.display === 'block') {
       element.style.display = 'none';
   } else {
       element.style.display = 'block';
   }
}