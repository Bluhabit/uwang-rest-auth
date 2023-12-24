document.addEventListener("DOMContentLoaded", function () {
  var numberOtpWrapper = document.querySelector(".numberOtp-wrapper");
  var otpString = "1234";

  for (var i = 0; i < otpString.length; i++) {
    var h3Element = document.createElement("h3");
    h3Element.textContent = otpString[i];
    h3Element.classList.add("numberOtp");
    numberOtpWrapper.appendChild(h3Element);
  }
});
