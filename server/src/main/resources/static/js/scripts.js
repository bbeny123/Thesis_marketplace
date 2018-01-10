function prePost() {
    document.getElementById('password').value =  document.getElementById('email').value + document.getElementById('rawPassword').value;
}

$(document).ready( function() {
    $('#data').dataTable();
});