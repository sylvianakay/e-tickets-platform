
$(document).ready(function () {
        populateEventDropdown("bookingEventDropdown");
    });

function createTableFromJSON(data) {
    var html = "<table><tr><th>Category</th><th>Value</th></tr>";
    for (const x in data) {
        var category = x;
        var value = data[x];
        html += "<tr><td>" + category + "</td><td>" + value + "</td></tr>";
    }
    html += "</table>";
    return html;

}

function logout() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.status === 200) {
            window.location.href = "index.html"; // Redirect to login page
            $("#ajaxContent").html("Successful Logout");
        } else {
            $("#ajaxContent").html("Error: " + xhr.responseText);
        }
    };
    xhr.open('GET', 'Logout', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
}


function getUser() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            $("#ajaxContent").html("Successful Login!\n"+createTableFromJSON(JSON.parse(xhr.responseText)));
        } else if (xhr.status !== 200) {
             $("#ajaxContent").html("User not exists or incorrect password");
        }
    };
    var data = $('#loginForm').serialize();
    xhr.open('GET', 'GetUser?'+data);
    xhr.setRequestHeader('Content-type','application/x-www-form-urlencoded');
    xhr.send();
}


//
//$("#edit-user-form").on("submit", function (event) {
//     event.preventDefault(); // Prevent form submission
//
//        const formData = {};
//        $(this).serializeArray().forEach((field) => {
//            formData[field.name] = field.value;
//        });
//
//        $.ajax({
//            url: '/EditUser', // Adjust to your servlet path
//            type: 'POST',
//            contentType: 'application/json',
//            data: JSON.stringify(formData),
//            success: function (response) {
//                $('#edit-message').text('User details updated successfully.').css('color', 'green');
//            },
//            error: function (xhr) {
//                $('#edit-message').text('Error updating user: ' + xhr.responseText).css('color', 'red');
//            }
//        });
//    });
 


//function createUser(){
//    var xhr = new XMLHttpRequest();
//    xhr.onload =  XMLHttpRequest();
//    xhr.onload = function () {
//        if (xhr.readyState === 4 && xhr.status === 200) {
//            $("#ajaxContent").html(createTableFromJSON(JSON.parse(xhr.responseText)));
//            $("#ajaxContent").html("Successful Login");
//        } else if (xhr.status !== 200) {
//             $("#ajaxContent").html("User not exists or incorrect password");
//        }
//    };
//}


function initDB() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById("dbMessage").innerText = "Database initialized successfully.";
        } else if (xhr.status !== 200) {
            document.getElementById("dbMessage").innerText = "Error occurred while initializing database: " + xhr.status;
        }
    };

    xhr.open('GET', 'InitDB');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function deleteDB() {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById("dbMessage").innerText = "Database deleted successfully.";
        } else if (xhr.status !== 200) {
            document.getElementById("dbMessage").innerText = "Error occurred while deleting database: " + xhr.status;
        }
    };

    xhr.open('GET', 'DeleteDB');
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function cancelEvent() {
        const selectedEventName = $(".eventDropdown").val(); // Assuming only one dropdown triggers this action

    if (!selectedEventName) {
        alert("Please select an event to cancel.");
        return;
    }

    if (!confirm("Are you sure you want to cancel this event? This will refund all customers.")) {
        return;
    }

    $.ajax({
        url: "CancelEvent",
        method: "POST",
        data: { eventName: selectedEventName },
        success: function (data) {
            location.reload();
            if (data.error) {
                $("#cancelEventMessage").text(data.error);
            } else {
                $("#cancelEventMessage").text(data.message);
                populateEventDropdown(); // Refresh all dropdowns
            }
        },
        error: function (xhr) {
            $("#cancelEventMessage").text(`Error: ${xhr.responseText || "Unknown error"}`);
        }
    });
    }
    
function populateEventDropdown(selectorId) {
    $.ajax({
        url: "Events", // Replace with your servlet URL that provides event names and IDs
        method: "GET",
        dataType: "json",
        success: function (data) {
            console.log("Dropdown Data:", data);
            $(".eventDropdown").each(function () {
                $(this).empty(); // Clear existing options
                $(this).append('<option value="" disabled selected>Select an Event</option>');
                
                data.forEach(event => {
                    $(this).append(`<option value="${event.EventID}">${event.EventName}</option>`);
                });
            });
        },
        error: function (xhr, status, error) {
            console.error("Error fetching events:", error);
            $(".eventDropdown").each(function () {
                $(this).html('<option value="" disabled>Error loading events</option>');
            });
        }
    });
    
   
}
