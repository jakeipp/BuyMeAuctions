var acc = document.getElementsByClassName("message");
var i;

for (i = 0; i < acc.length; i++) {
  acc[i].addEventListener("click", function() {
    this.classList.toggle("active");
    var panel = this.nextElementSibling;
    if (panel.style.minHeight) {
      panel.style.minHeight = null;
    } else {
      panel.style.minHeight = "7ch";
    } 
  });
}