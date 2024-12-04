/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


$(document).ready(function () {
    // Load user details on page load
    loadUserDetails();

    // Save changes
    $('#save-changes').on('click', function () {
        const formData = {};
        $('#edit-user-form')
            .serializeArray()
            .forEach((item) => (formData[item.name] = item.value));

        $.ajax({
            url: 'EditUser',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                $('#ajax-message').text('Changes saved successfully!').css('color', 'green');
            },
            error: function (xhr) {
                $('#ajax-message').text(`Error: ${xhr.responseText}`).css('color', 'red');
            },
        });
    });
});

function loadUserDetails() {
    $.ajax({
        url: 'GetUserSession',
        type: 'GET',
        contentType: 'application/json',
        success: function (response) {
            const user = response;

            // Populate the form fields
            $('#username').val(user.username);
            $('#email').val(user.email);
            $('#firstname').val(user.firstname);
            $('#lastname').val(user.lastname);
            $('#birthdate').val(user.birthdate);
            $('#gender').val(user.gender);
            $('#country').val(user.country);
            $('#address').val(user.address);
            $('#municipality').val(user.municipality);
            $('#prefecture').val(user.prefecture);
            $('#job').val(user.job);
            $('#telephone').val(user.telephone);
            $('#afm').val(user.afm);
        },
        error: function () {
            $('#ajax-message').text('Error loading user details.').css('color', 'red');
        },
    });
}
