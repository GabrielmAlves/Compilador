const editButton = document.querySelector(".edit");
const textarea = document.querySelector("#textarea");
let editable = false;
editButton.addEventListener("click", () => {
  editable = !editable;
  if (editable) {
    textarea.disabled = false;
    editable = true;
  } else {
    textarea.disabled = true;
    editable = false;
  }
});
