'use strict';

const $delBtn = document.getElementById('registration2');

$delBtn.addEventListener('click',e=>{
	console.log('클릭');
    if(confirm('삭제 하시겠습니까?') == true){
				alert('삭제되었습니다.')
    }else{
        alert('취소되었습니다.')
    }
});


var slideIndex = 1;
showSlides(slideIndex);



// 다음, 이전 제어
function plusSlides(n) {
  showSlides(slideIndex += n);
}

// 사진 제어
function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("dot");
  if (n > slides.length) {slideIndex = 1} 
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none"; 
  }
  for (i = 0; i < dots.length; i++) {
      dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex-1].style.display = "block"; 
  dots[slideIndex-1].className += " active";
}





